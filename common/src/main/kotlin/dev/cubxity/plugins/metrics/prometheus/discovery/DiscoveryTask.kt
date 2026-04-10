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

import kotlin.time.Duration

/**
 * A scheduled discovery task produced by [HttpDiscovery.Factory].
 *
 * @property task     The lambda to invoke on each tick. Performs the HTTP POST.
 * @property interval How often the task should run.
 */
data class DiscoveryTask(
    val task: () -> Unit,
    val interval: Duration
)
