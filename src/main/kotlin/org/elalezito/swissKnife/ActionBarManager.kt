package org.elalezito.swissKnife

import com.github.shynixn.mccoroutine.bukkit.launch
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin

//import com.github.shynixn.mccoroutine.paper.launch // Importa a extensão .launch para o JavaPlugin
import kotlinx.coroutines.delay                // Importa a suspensão
import kotlinx.coroutines.isActive             // Importa a verificação de corrotina ativa
import me.clip.placeholderapi.PlaceholderAPI
import org.elalezito.swissKnife.objects.Config
import org.elalezito.swissKnife.objects.HudActionbarData
import org.elalezito.swissKnife.objects.Toolkit

class ActionBarManager(private val plugin: JavaPlugin) {
	private val mm = MiniMessage.miniMessage()

	fun startActionBarLoop() {
		plugin.launch {
			while (isActive) {
				val actionbarData: HudActionbarData = Config.hud.actionbarData

				if (!actionbarData.enabled) {
					delay(5000)
					continue
				}

				plugin.server.onlinePlayers.forEach { player ->
					val rawText: String = actionbarData.message
					val formatted = PlaceholderAPI.setPlaceholders(player, rawText)
					val component = mm.deserialize(formatted)
					player.sendActionBar(component)
				}

				delay(1000)
			}
		}
	}
}