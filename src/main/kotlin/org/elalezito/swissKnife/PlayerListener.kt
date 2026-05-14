package org.elalezito.swissKnife

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.delay
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.elalezito.swissKnife.objects.Toolkit
import java.io.File
import java.io.IOException
import javax.tools.Tool

class PlayerListener(private val plugin: JavaPlugin,
										 private val deathManager: DeathManager,
										 private val bossBarManager: BossBarManager) : Listener {
	private val mm = MiniMessage.miniMessage()

	@EventHandler
	fun onJoin(event: PlayerJoinEvent) {
		val config = plugin.config

		// bossbar
		deathManager.loadDeathList(event.player)
		bossBarManager.createBossBar(event.player)

		// welcome message
		val welcomeMessageEnabled: Boolean = config.getBoolean("welcome.enabled", false)
		if (!welcomeMessageEnabled)
			return;

		plugin.launch {
			// delay pelo logo da mojang
			delay(5000)

			// title
			val welcomeTitle: String = config.getString("welcome.title") ?: "Welcome Title"
			val welcomeSubtitle: String = config.getString("welcome.subtitle") ?: "Welcome Subtitle"
			Toolkit.title(event.player, welcomeTitle, welcomeSubtitle)

			delay(5000)

			// welcome message
			val welcomeLines: List<String> = config.getStringList("welcome.message")
			welcomeLines.forEach { line ->
				Toolkit.send(event.player, line)
			}
		}
	}

	@EventHandler
	fun onQuit(event: PlayerQuitEvent) {
		bossBarManager.destroyBossBar(event.player)
		deathManager.clearDeathList(event.player)
	}

	@EventHandler(priority = EventPriority.LOWEST)
	fun onPlayerDeath(event: PlayerDeathEvent) {
		val player = event.entity
		val location = player.location

		val hasItems = event.drops.isNotEmpty()
		val hasXp = event.droppedExp > 0 || player.level > 0

		// não faz nada se o jogador não tiver itens ou XP
		if(!hasItems && !hasXp) {
			// Toolkit.log("${player.name} morreu sem dropar itens. Ignorar waypoint")
			return
		}

		// salva o histórico da morte
		deathManager.addDeath(player, location)

		// printa no chat a posição da morte
		deathManager.deathMessage(player, location)
	}
}