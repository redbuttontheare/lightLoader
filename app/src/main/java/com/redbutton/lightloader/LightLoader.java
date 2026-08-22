package com.redbutton.lightloader;

import java.lang.instrument.Instrumentation;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.Properties;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LightLoader {
    private static final String DEFAULT_MINECRAFT_VERSION = "26.2";
    private static final String MOD_SERVICE = "META-INF/services/" + ModInitializer.class.getName();
    private static final String TRANSFORMER_SERVICE = "META-INF/services/" + ClassTransformer.class.getName();
    private static final String MOD_METADATA = "META-INF/lightloader.mod.properties";
    private static final Logger LOGGER = Logger.getLogger("LightLoader");
    private static final AtomicBoolean INITIALIZED = new AtomicBoolean();
    private static LoaderContext context;
    private static ModClassLoader modClassLoader;

    private LightLoader() {
    }

    public static void launch(String[] args) {
        launch(String.join(" ", args), null);
    }

    public static void launch(String options, Instrumentation instrumentation) {
        if (!INITIALIZED.compareAndSet(false, true)) {
            LOGGER.fine("Loader is already initialized");
            return;
        }

        LOGGER.info("LightLoader " + Client.VERSION + " starting"
                + (options == null || options.isBlank() ? "" : " with options: " + options));
        Path modsDirectory = modsDirectory();
        context = new LoaderContext(instrumentation, modsDirectory, worldDirectory());
        context.worldEvents().onJoin(worldName -> context.chat().send(
            "LightLoader " + Client.VERSION + " loaded"));
        initializeVersionAdapter(context);
        initializeExternalMods(context);
        ServiceLoader.load(ModInitializer.class).forEach(initializer -> initialize(initializer, context));
    }

    public static void onWorldJoin(String worldName) {
        LoaderContext currentContext = context;
        if (currentContext == null) {
            LOGGER.warning("Ignoring world join before LightLoader initialization");
            return;
        }
        currentContext.worldEvents().fireJoin(worldName);
    }

    public static void onWorldJoin() {
        onWorldJoin("");
    }

    private static void initializeVersionAdapter(LoaderContext context) {
        String requestedVersion = System.getProperty("lightloader.minecraftVersion", DEFAULT_MINECRAFT_VERSION);
        ServiceLoader.load(VersionAdapter.class).stream()
                .map(ServiceLoader.Provider::get)
                .filter(adapter -> adapter.minecraftVersion().equals(requestedVersion))
                .findFirst()
                .ifPresentOrElse(
                        adapter -> initialize(adapter, context),
                        () -> LOGGER.warning("No adapter found for Minecraft " + requestedVersion));
    }

    private static void initialize(VersionAdapter adapter, LoaderContext context) {
        try {
            adapter.install(context);
            adapter.initialize(context);
            LOGGER.info("Initialized Minecraft adapter " + adapter.minecraftVersion());
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Could not initialize Minecraft adapter " + adapter.minecraftVersion(), exception);
        }
    }

    private static void initializeExternalMods(LoaderContext context) {
        Path modsDirectory = modsDirectory();
        try {
            Files.createDirectories(modsDirectory);
            List<URL> modUrls;
            try (Stream<Path> files = Files.list(modsDirectory)) {
                modUrls = files.filter(Files::isRegularFile)
                        .filter(path -> path.getFileName().toString().endsWith(".jar"))
                        .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                        .map(LightLoader::toUrl)
                        .toList();
            }

            if (modUrls.isEmpty()) {
                LOGGER.info("No external mods found in " + modsDirectory);
                return;
            }

            modClassLoader = new ModClassLoader(modUrls.toArray(URL[]::new), LightLoader.class.getClassLoader());
                registerTransformers(context);
            for (URL modUrl : modUrls) {
                initializeMod(modUrl, context);
            }
            LOGGER.info("Scanned " + modUrls.size() + " mod JAR(s)");
        } catch (IOException | RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Could not scan mods directory " + modsDirectory, exception);
        }
    }

    private static void initializeMod(URL modUrl, LoaderContext context) {
        try (JarFile jar = new JarFile(Path.of(modUrl.toURI()).toFile())) {
            if (jar.getEntry(MOD_METADATA) != null) {
                Properties properties = new Properties();
                try (InputStream stream = jar.getInputStream(jar.getEntry(MOD_METADATA))) {
                    properties.load(stream);
                }
                ModMetadata metadata = ModMetadata.from(properties);
                if (!metadata.minecraftVersion().equals(System.getProperty(
                        "lightloader.minecraftVersion", DEFAULT_MINECRAFT_VERSION))) {
                    LOGGER.warning("Skipping mod " + metadata.id() + ": Minecraft version mismatch");
                    return;
                }
                Class<?> entrypoint = Class.forName(metadata.entrypoint(), true, modClassLoader);
                initialize((ModInitializer) entrypoint.getDeclaredConstructor().newInstance(), context);
                LOGGER.info("Loaded mod " + metadata.id() + " " + metadata.version());
                return;
            }

            ServiceLoader.load(ModInitializer.class, modClassLoader).stream()
                    .map(ServiceLoader.Provider::get)
                    .filter(initializer -> initializer.getClass().getProtectionDomain()
                            .getCodeSource().getLocation().equals(modUrl))
                    .forEach(initializer -> initialize(initializer, context));
        } catch (ReflectiveOperationException | IOException | java.net.URISyntaxException exception) {
            LOGGER.log(Level.SEVERE, "Could not load mod JAR " + modUrl, exception);
        }
    }

    private static void registerTransformers(LoaderContext context) {
        if (context.instrumentation() == null) {
            return;
        }
        ServiceLoader.load(ClassTransformer.class, modClassLoader)
                .forEach(transformer -> context.instrumentation().addTransformer(new java.lang.instrument.ClassFileTransformer() {
                    @Override
                    public byte[] transform(Module module, ClassLoader classLoader, String className,
                            Class<?> classBeingRedefined, java.security.ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
                        try {
                            return transformer.transform(className, classfileBuffer);
                        } catch (RuntimeException exception) {
                            LOGGER.log(Level.SEVERE, "Transformer failed for " + className, exception);
                            return null;
                        }
                    }
                }, true));
    }

    private static Path modsDirectory() {
        return loaderDirectory().resolve("mods");
    }

    private static Path worldDirectory() {
        return loaderDirectory().resolve("world");
    }

    private static Path loaderDirectory() {
        try {
            Path loaderPath = Path.of(LightLoader.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            return Files.isDirectory(loaderPath) ? loaderPath : loaderPath.getParent();
        } catch (URISyntaxException | NullPointerException exception) {
            throw new IllegalStateException("Could not locate LightLoader JAR", exception);
        }
    }

    private static URL toUrl(Path path) {
        try {
            return path.toUri().toURL();
        } catch (IOException exception) {
            throw new IllegalStateException("Invalid mod path " + path, exception);
        }
    }

    private static final class ModClassLoader extends URLClassLoader {
        private ModClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public java.util.Enumeration<URL> getResources(String name) throws IOException {
            if (MOD_SERVICE.equals(name) || TRANSFORMER_SERVICE.equals(name) || MOD_METADATA.equals(name)) {
                return findResources(name);
            }
            return super.getResources(name);
        }
    }

    private static void initialize(ModInitializer initializer, LoaderContext context) {
        try {
            initializer.onInitialize(context);
            LOGGER.info("Initialized mod " + initializer.getClass().getName());
        } catch (RuntimeException exception) {
            LOGGER.log(Level.SEVERE, "Could not initialize mod " + initializer.getClass().getName(), exception);
        }
    }
}