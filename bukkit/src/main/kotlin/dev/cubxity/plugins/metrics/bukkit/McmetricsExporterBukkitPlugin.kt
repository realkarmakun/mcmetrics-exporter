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

package dev.cubxity.plugins.metrics.bukkit

import dev.cubxity.plugins.metrics.api.UnifiedMetrics
import dev.cubxity.plugins.metrics.bukkit.bootstrap.UnifiedMetricsBukkitBootstrap
import dev.cubxity.plugins.metrics.bukkit.metric.events.EventsCollection
import dev.cubxity.plugins.metrics.bukkit.metric.server.ServerCollection
import dev.cubxity.plugins.metrics.bukkit.metric.tick.TickCollection
import dev.cubxity.plugins.metrics.bukkit.metric.world.WorldCollection
import dev.cubxity.plugins.metrics.common.plugin.AbstractUnifiedMetricsPlugin
import org.bukkit.plugin.ServicePriority

class McmetricsExporterBukkitPlugin(
    override val bootstrap: UnifiedMetricsBukkitBootstrap
) : AbstractUnifiedMetricsPlugin() {
    override fun registerPlatformService(api: UnifiedMetrics) {
        bootstrap.server.servicesManager.register(UnifiedMetrics::class.java, api, bootstrap, ServicePriority.Normal)
    }

    override fun registerPlatformMetrics() {
        super.registerPlatformMetrics()

        apiProvider.metricsManager.apply {
            registerCollection(ServerCollection(bootstrap))
            registerCollection(WorldCollection(bootstrap))
            registerCollection(TickCollection(bootstrap))
            registerCollection(EventsCollection(bootstrap))
        }
    }
}
