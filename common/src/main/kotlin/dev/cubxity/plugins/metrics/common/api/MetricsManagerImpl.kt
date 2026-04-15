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

package dev.cubxity.plugins.metrics.common.api

import dev.cubxity.plugins.metrics.api.metric.MetricsManager
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.collector.collect
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import dev.cubxity.plugins.metrics.api.util.fastForEach
import dev.cubxity.plugins.metrics.common.plugin.UnifiedMetricsPlugin
import dev.cubxity.plugins.metrics.common.plugin.dispatcher.CurrentThreadDispatcher
import dev.cubxity.plugins.metrics.prometheus.PrometheusMetricsDriver
import dev.cubxity.plugins.metrics.prometheus.discovery.DiscoveryTask
import dev.cubxity.plugins.metrics.prometheus.discovery.HttpDiscovery
import kotlinx.coroutines.withContext
import kotlin.system.measureTimeMillis

class MetricsManagerImpl(private val plugin: UnifiedMetricsPlugin) : MetricsManager {
    private val _collections: MutableList<CollectorCollection> = ArrayList()

    private var driver: PrometheusMetricsDriver? = null
    private var _discoveryTask: DiscoveryTask? = null

    val discoveryTask: DiscoveryTask?
        get() = _discoveryTask

    override val collections: List<CollectorCollection>
        get() = _collections

    override fun initialize() {
        plugin.bootstrap.logger.info("Initializing Prometheus metrics.")
        val time = measureTimeMillis {
            try {
                val config = plugin.config.metrics.prometheus

                val driver = PrometheusMetricsDriver(plugin.apiProvider, config)
                driver.initialize()
                this.driver = driver

                _discoveryTask = HttpDiscovery.Factory.create(plugin.apiProvider, config.discovery)
            } catch (error: Throwable) {
                plugin.apiProvider.logger.severe("An error occurred whilst initializing Prometheus metrics", error)
            }
        }
        plugin.bootstrap.logger.info("Prometheus metrics initialized ($time ms).")
    }

    override fun registerCollection(collection: CollectorCollection) {
        try {
            collection.initialize()
            _collections.add(collection)
        } catch (error: Throwable) {
            plugin.bootstrap.logger.warn("An error occurred whilst registering metric", error)
        }
    }

    override fun unregisterCollection(collection: CollectorCollection) {
        try {
            _collections.remove(collection)
            collection.dispose()
        } catch (error: Throwable) {
            plugin.bootstrap.logger.warn("An error occurred whilst unregistering metric", error)
        }
    }

    override suspend fun collect(): List<Metric> {
        val list = ArrayList<Metric>()
        val dispatcher = plugin.apiProvider.dispatcher

        if (dispatcher is CurrentThreadDispatcher) {
            collections.fastForEach { collection ->
                list.addAll(collection.collect())
            }
        } else {
            withContext(dispatcher) {
                collections.fastForEach { collection ->
                    if (!collection.isAsync) {
                        list.addAll(collection.collect())
                    }
                }
            }
            collections.fastForEach { collection ->
                if (collection.isAsync) {
                    list.addAll(collection.collect())
                }
            }
        }
        return list
    }

    override fun dispose() {
        collections.toList().fastForEach { collection ->
            unregisterCollection(collection)
        }

        driver?.close()
        driver = null
        _discoveryTask = null
    }
}
