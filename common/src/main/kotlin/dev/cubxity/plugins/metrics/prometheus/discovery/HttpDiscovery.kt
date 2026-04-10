/*
 *     This file is part of mcmetrics-exporter.
 *
 *     Copyright (C) 2025 rkkm (MeProject)
 *
 *     mcmetrics-exporter is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     mcmetrics-exporter is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with mcmetrics-exporter.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.cubxity.plugins.metrics.prometheus.discovery

import dev.cubxity.plugins.metrics.api.UnifiedMetrics
import dev.cubxity.plugins.metrics.prometheus.config.DiscoveryConfig
import okhttp3.MediaType.Companion.toMediaType
import kotlin.time.Duration.Companion.seconds
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HttpDiscovery private constructor(
    private val api: UnifiedMetrics,
    private val config: DiscoveryConfig
) {

    companion object Factory {
        fun create(api: UnifiedMetrics, config: DiscoveryConfig): DiscoveryTask? {
            if (!config.enabled) return null
            val http = HttpDiscovery(api, config)
            return DiscoveryTask(
                task = http::send,
                interval = config.intervalSeconds.seconds
            )
        }
    }

    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun send() {
        try {
            val body = buildPayload().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(config.url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    api.logger.warn("HTTP discovery returned non-2xx status: ${response.code}")
                }
            }
        } catch (e: Exception) {
            api.logger.severe("An error occurred whilst sending HTTP discovery", e)
        }
    }

    private fun buildPayload(): String {
        val target = "${config.host}:${config.port}"
        val labels = buildMap<String, String> {
            put("job", config.job)
            putAll(config.labels)
        }
        val labelsJson = labels.entries.joinToString(",") { (k, v) ->
            "\"${k.escape()}\":\"${v.escape()}\""
        }
        return """{"targets":["$target"],"labels":{$labelsJson}}"""
    }

    private fun String.escape(): String =
        replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
}
