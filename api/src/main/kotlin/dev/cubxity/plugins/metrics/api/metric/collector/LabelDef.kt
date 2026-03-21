/*
    This file is part of mcmetrics-exporter.

    mcmetrics-exporter is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UnifiedMetrics is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with UnifiedMetrics.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.cubxity.plugins.metrics.api.metric.collector

/**
 * Defines a single label key and the value that should be used when the runtime
 * value is unavailable.
 *
 * The [invoke] operator lets you stamp a runtime value onto the definition in one
 * call, producing a [Pair] ready to be passed to [Counter.inc]:
 *
 * ```
 * val hostname = LabelDef("hostname")
 *
 * // known at runtime
 * counter.inc(hostname("play.example.com"))
 *
 * // null → falls back to default ("unknown")
 * counter.inc(hostname(connection.virtualHost.getOrNull()?.hostString))
 * ```
 *
 * @param key     the label key as it will appear in the exported metric
 * @param default the fallback value used when [invoke] receives `null`
 */
data class LabelDef(val key: String, val default: String = "unknown") {

    /**
     * Returns a [Pair] of this label's [key] and the resolved value.
     *
     * If [value] is `null` (e.g. the information was not available at the call
     * site) the [default] is used instead, so callers never have to repeat the
     * null-check themselves.
     */
    operator fun invoke(value: String? = null): Pair<String, String> = key to (value ?: default)
}
