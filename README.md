# LightLoader

A small Minecraft-like loader built on the Java Instrumentation Agent API. Its mod API follows the basic Fabric idea: a mod implements `ModInitializer`, and the loader discovers it through `ServiceLoader`.

## Build

```bash
./gradlew build
```

The agent JAR is created in `app/build/libs/`. The mods directory is created next to the JAR:

```text
app/build/libs/
├── app.jar
└── mods/
```

Run Minecraft with the loader by adding the agent argument to Minecraft's JVM arguments:

```bash
java -javaagent:/path/to/lightloader.jar -jar minecraft.jar
```

For standalone API testing:

```bash
./gradlew :app:run
```

A mod can be registered with `META-INF/lightloader.mod.properties`:

```properties
id=my-mod
name=My Mod
version=1.0.0
minecraft=26.2
entrypoint=com.example.mymod.MyMod
```

The entrypoint class must implement `ModInitializer`. The legacy `META-INF/services/com.redbutton.lightloader.ModInitializer` format is also supported.

## Minecraft Versions

The Chaos Cubed `26.2` version is currently supported by the adapter in `com.redbutton.lightloader.v1_26_2`. Select the Minecraft version with:

```bash
java -Dlightloader.minecraftVersion=26.2 -javaagent:app/build/libs/app.jar -jar minecraft-like-client.jar
```

New versions are added as separate packages, such as `v1_27_0`, and registered through `META-INF/services/com.redbutton.lightloader.VersionAdapter`. Mods are always placed in the `mods` directory next to the loader JAR.

## Mod API

`LoaderContext` provides the initial APIs for mods:

```java
context.chat().send("Hello from my mod");
context.commands().register(new MyCommand());
context.storage().modDirectory("my-mod");
context.storage().worldDirectory("my-mod");
context.creativeTabs().register(
	"my-mod",
	"My Mod",
	"assets/my-mod/icon.png",
	MyMod.class);
```

Creative tab icons are loaded from the mod's resources. The current chat and creative-tab implementations are loader APIs ready for the `26.2` Minecraft integration; they do not yet draw the real Minecraft UI.

The loader registers a built-in world join message without requiring the world name:

```text
LightLoader 0.1.0 loaded
```

The `26.2` adapter will call `LightLoader.onWorldJoin()` from its Minecraft world hook. Mods can still use `context.worldEvents().onJoin(...)` when they need the world name.

LightLoader is a loader, not an installer or a launcher. Minecraft remains responsible for starting the JVM. The Java Agent is loaded before Minecraft classes, allowing the `26.2` adapter and mod `ClassTransformer` implementations to register before the game starts.

### Without JVM arguments

If your launcher does not provide a JVM arguments field, set the version JSON `mainClass` to:

```json
"mainClass": "com.redbutton.lightloader.Bootstrap"
```

This bootstrap starts LightLoader and then calls Minecraft's `net.minecraft.client.main.Main`. It supports mod entrypoints, chat API logging, commands and storage, but bytecode transformers require the Java Agent mode.