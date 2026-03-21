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

rootProject.name = "mcmetrics-exporter"

val modulePrefix = ":mcmetrics-exporter-"

include(modulePrefix + "api")
include(modulePrefix + "common")

include(modulePrefix + "bukkit")
include(modulePrefix + "velocity")

project(modulePrefix + "api").projectDir = File(rootDir, "api")
project(modulePrefix + "common").projectDir = File(rootDir, "common")

project(modulePrefix + "bukkit").projectDir = File(rootDir, "bukkit")
project(modulePrefix + "velocity").projectDir = File(rootDir, "velocity")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
