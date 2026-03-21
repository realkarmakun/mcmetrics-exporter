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

package dev.cubxity.plugins.metrics.api.metric.collector

import dev.cubxity.plugins.metrics.api.metric.data.CounterMetric
import dev.cubxity.plugins.metrics.api.metric.data.Labels
import dev.cubxity.plugins.metrics.api.metric.data.Metric
import dev.cubxity.plugins.metrics.api.metric.store.DoubleAdderStore
import dev.cubxity.plugins.metrics.api.metric.store.DoubleStore
import dev.cubxity.plugins.metrics.api.metric.store.DoubleStoreFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * A [Collector] that tracks a counter broken down by zero or more labels whose
 * **keys** (and fallback defaults) are fixed at construction time but whose
 * **values** are supplied at increment time via [LabelDef.invoke].
 *
 * A child store is created lazily the first time a particular combination of
 * label values is observed. When no [labelDefs] are provided the counter
 * behaves as a plain single-value counter.
 *
 * Example:
 * ```
 * val hostname = LabelDef("hostname")
 *
 * val logins = Counter("minecraft_events_login_total", hostname)
 *
 * // at event time — null-safe, falls back to LabelDef.default automatically
 * logins.inc(hostname(connection.virtualHost.getOrNull()?.hostString))
 * ```
 *
 * Plain usage (no labels):
 * ```
 * val logins = Counter("minecraft_events_login_total")
 * logins.inc()
 * ```
 *
 * @param name              metric name, should end with `_total`
 * @param labelDefs         zero or more label definitions (key + default value)
 * @param valueStoreFactory factory used when creating each label set's store
 */
class Counter(
    private val name: String,
    private vararg val labelDefs: LabelDef,
    private val valueStoreFactory: DoubleStoreFactory = DoubleAdderStore
) : Collector {

    private val stores = ConcurrentHashMap<Labels, DoubleStore>()

    override fun collect(): List<Metric> =
        stores.entries.map { (labels, store) -> CounterMetric(name, labels, store.get()) }

    /**
     * Increments the counter for the resolved label combination by one.
     *
     * Each element of [labelValues] is typically produced by calling a [LabelDef]
     * as a function (its `invoke` operator), e.g. `hostname("play.example.com")`.
     * Any label whose key is absent from [labelValues] falls back to its
     * [LabelDef.default].
     */
    fun inc(vararg labelValues: Pair<String, String>): Counter = apply {
        val provided = labelValues.toMap()
        val labels: Labels = labelDefs.associate { it.key to (provided[it.key] ?: it.default) }
        stores.computeIfAbsent(labels) { valueStoreFactory.create() }.add(1.0)
    }
}
