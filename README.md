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

Run a Java program with the loader:

```bash
java -javaagent:app/build/libs/app.jar -jar minecraft-like-client.jar
```

For standalone execution:

```bash
./gradlew :app:run
```

A mod is registered through `META-INF/services/com.redbutton.lightloader.ModInitializer`. The file must contain the fully qualified name of a class that implements `ModInitializer`.

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