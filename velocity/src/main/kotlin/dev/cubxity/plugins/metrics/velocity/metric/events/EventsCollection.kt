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

package dev.cubxity.plugins.metrics.velocity.metric.events

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.PreLoginEvent
import com.velocitypowered.api.event.player.PlayerChatEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import com.velocitypowered.api.event.proxy.ProxyPingEvent
import dev.cubxity.plugins.metrics.api.metric.collector.Collector
import dev.cubxity.plugins.metrics.api.metric.collector.CollectorCollection
import dev.cubxity.plugins.metrics.api.metric.collector.Counter
import dev.cubxity.plugins.metrics.api.metric.collector.LabelDef
import dev.cubxity.plugins.metrics.velocity.bootstrap.McmetricsExporterVelocityBootstrap

@Suppress("UNUSED_PARAMETER")
class EventsCollection(private val bootstrap: McmetricsExporterVelocityBootstrap) : CollectorCollection {
    private val loginCounter = Counter("minecraft_events_login_total", virtualhostLabel)
    private val joinCounter = Counter("minecraft_events_join_total", virtualhostLabel)
    private val quitCounter = Counter("minecraft_events_quit_total")
    private val chatCounter = Counter("minecraft_events_chat_total")
    private val pingCounter = Counter("minecraft_events_ping_total", virtualhostLabel)

    override val collectors: List<Collector> =
        listOf(loginCounter, joinCounter, quitCounter, chatCounter, pingCounter)

    override fun initialize() {
        bootstrap.server.eventManager.register(bootstrap, this)
    }

    override fun dispose() {
        bootstrap.server.eventManager.unregisterListener(bootstrap, this)
    }


    @Subscribe
    fun onLogin(event: PreLoginEvent) {
        loginCounter.inc(virtualhostLabel(event.connection.virtualHost.orElse(null)?.hostString))
    }

    @Subscribe
    fun onConnect(event: PlayerChooseInitialServerEvent) {
        joinCounter.inc(virtualhostLabel(event.player.virtualHost.orElse(null)?.hostString))
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        quitCounter.inc()
    }

    @Subscribe
    fun onChat(event: PlayerChatEvent) {
        chatCounter.inc()
    }

    @Subscribe
    fun onPing(event: ProxyPingEvent) {
        pingCounter.inc(virtualhostLabel(event.connection.virtualHost.orElse(null)?.hostString))
    }

    companion object {
        val virtualhostLabel = LabelDef(key = "virtualhost", default = "unknown")
    }
}
