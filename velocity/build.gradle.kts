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
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":mcmetrics-exporter-common"))

    compileOnly("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
    kapt("com.velocitypowered:velocity-api:3.5.0-SNAPSHOT")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        relocate("com.charleskorn", "ru.meproject.mcmetrics-exporter.metrics.libs.com.charleskorn")
        relocate("io.prometheus", "ru.meproject.mcmetrics-exporter.metrics.libs.io.prometheus")
        relocate("okhttp3", "ru.meproject.mcmetrics-exporter.plugins.metrics.libs.okhttp3")
        relocate("okio", "ru.meproject.mcmetrics-exporter.plugins.metrics.libs.okio")
    }

}

kotlin {
    jvmToolchain(21)
}
