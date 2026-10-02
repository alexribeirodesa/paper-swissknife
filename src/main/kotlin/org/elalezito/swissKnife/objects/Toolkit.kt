package org.elalezito.swissKnife.objects

import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.awt.Component

object Toolkit {
	private val mm = MiniMessage.miniMessage()
	private val logPrefix = "»"
	lateinit private var plugin: JavaPlugin

	fun setPlugin(plugin: JavaPlugin) {
		this.plugin = plugin
	}

	fun send(player: Player, message: String) {
		val formatted = PlaceholderAPI.setPlaceholders(player, message)
		val component = mm.deserialize(formatted)
		player.sendMessage(component)
	}

	fun title(player: Player, title: String = "", subtitle: String = "") {
		if(title.isBlank() && subtitle.isBlank())
			return

		val formattedTitle = PlaceholderAPI.setPlaceholders(player, title)
		val mainTitle = mm.deserialize(formattedTitle)

		val formattedSubtitle = PlaceholderAPI.setPlaceholders(player, subtitle)
		val subtitle = mm.deserialize(formattedSubtitle)

		val title: Title = Title.title(mainTitle, subtitle)

		player.showTitle(title)
	}

	fun log(message: String) {
		plugin.logger.info("$logPrefix $message")
	}
}