/*
 *     This file is part of UnifiedMetrics.
 *
 *     UnifiedMetrics is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     UnifiedMetrics is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with UnifiedMetrics.  If not, see <https://www.gnu.org/licenses/>.
 */

/*
 *   Originally part of UnifiedMetrics.
 *   Forked and modified by MeProject (2026) for mcmetrics-exporter.
 *   Licensed under LGPL v3 or later.
 */

plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

dependencies {
    api(platform(kotlin("bom")))
    api(kotlin("stdlib"))
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.10.2")
    api("org.jetbrains.kotlinx", "kotlinx-serialization-core", "1.8.1")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/realkarmakun/mcmetrics-exporter")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name.set("mcmetrics-exporter")
                description.set("mcmetrics-exporter is opinionated fork of UnifiedMetrics")
                url.set("https://github.com/Cubxity/UnifiedMetrics/")

                licenses {
                    license {
                        name.set("GNU Lesser General Public License v3.0")
                        url.set("https://github.com/Cubxity/UnifiedMetrics/blob/dev/0.3.x/COPYING.LESSER")
                    }
                }
                developers {
                    developer {
                        id.set("rkkm")
                        name.set("rkkm")
                        email.set("p.konstantinov@meproject.ru")
                    }
                }
            }
        }
    }
}
