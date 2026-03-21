# mcmetrics-exporter

Opinionated fork of [UnifiedMetrics](https://github.com/Cubxity/UnifiedMetrics) 

UnifiedMetrics is a fully-featured free and open-source metrics collection plugin for Minecraft servers. 
This project is effort to make it work with RustMe server, and makes some opinionated choices about what to use, expose and etc.

## Main differences from UnifiedMetrics
- **Prometheus only** Prometheus is main way to work with metrics now, and project is easier to maintain.
- **Expose common module along with API** You can now build your own platform module instead of waiting for in-tree support and it's recommended way to integrate (exceptions are Velocity, Bukkit and Sponge)
- **Support for HTTP Service Discovery** Sends simple JSON to a server every other minute to make use of HTTP SD
- **API updates** Allow to dynamically set labels, cardinality warning etc.


## Compatibility

**Server:**

- Spigot servers, Java 11, 1.16.5+ *(includes Spigot-based forks)*
- Velocity

**Other servers:**
- TBA (Contributions are always welcome)

Don't see your server implementation? Pull `common` module from maven and implement it yourself, and make pull request to add it to the list!

## Getting started

## Metrics

<details> 
  <summary>Table of metrics (click to show)</summary>

| Collector     | Description                                     | Platform         | Default |
| ------------- | ----------------------------------------------- | ---------------- | ------- |
| systemGc      | Garbage collection duration and freed bytes     | All              | true    |
| systemMemory  | Memory used, committed, max and init            | All              | true    |
| systemProcess | CPU load, seconds, and process start time       | All              | true    |
| systemThread  | Current, daemon, started, and peak thread count | All              | true    |
| events        | Login, join, quit, chat, and ping event counter | All              | true    |
| server        | Plugins count and player counts                 | All              | true    |
| tick          | Tick duration histogram                         | Bukkit, Sponge   | true    |
| world         | World entities, players, and chunks count       | Bukkit, Sponge   | true    |

</details>

## Building from source

<details> 
  <summary>Instructions (click to show)</summary>

**Requirements:**

- JDK 8+
- Git (Optional)

To build UnifiedMetrics, you need to obtain the source code first. You can download the source from GitHub or use the
Git CLI.

```bash
$ git clone https://github.com/Cubxity/UnifiedMetrics && cd UnifiedMetrics
```

Open a terminal in the cloned directory and run the following command. The following command will build all subprojects.

```bash
$ ./gradlew assemble
```

To build a specific subproject, you can prefix it with the subproject path. For example:

```bash
$ ./gradlew :unifiedmetrics-platform-bukkit:assemble
```

The output artifacts can be found in `subproject/build/libs`.
</details>

## API


<details> 
  <summary>Instructions (click to show)</summary>

### Examples

Example plugins can be found under [examples](examples) directory.

### Gradle (Kotlin)

```kotlin
repositories {
    mavenCentral()

    // Snapshots repository (only required for -SNAPSHOT versions)
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
```

```kotlin
dependencies {
    // Replace this with the desired version
    compileOnly("dev.cubxity.plugins", "unifiedmetrics-api", "0.3.6")
}
```

### Usage

Add `:unifiedmetrics-api` as a dependency (compileOnly/provided). Prefer using platform's service manager if possible.

```kotlin
import dev.cubxity.plugins.metrics.api.UnifiedMetricsProvider

/* ... */

val api = UnifiedMetricsProvider.get()
```

</details>
