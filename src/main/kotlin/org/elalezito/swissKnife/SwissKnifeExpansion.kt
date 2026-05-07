package org.elalezito.swissKnife

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.entity.Player

class SwissKnifeExpansion(private val plugin: SwissKnife): PlaceholderExpansion() {
	// o que vem antes do _, ex: %swissknife_...%
	override fun getIdentifier(): String = "swissknife"
	override fun getAuthor(): String = "ElAlezito"
	override fun getVersion(): String = "1.0"

	override fun onPlaceholderRequest(player: Player?, params: String): String? {
		if (player == null) return ""

		// %swissknife_test%
		if (params == "test") {
			return "Hello World :)"
		}

		// %swissknife_restarttime%
		if (params == "restarttime") {
			// Aqui depois chamaremos sua função de cálculo de tempo
			return plugin.restartManager.getSecondsUntilRestart().toString()
		}
		if (params == "restarttime_formated") {
			val seconds = plugin.restartManager.getSecondsUntilRestart()

			val h = seconds / 3600
			val m = (seconds % 3600) / 60
			val s = seconds % 60
			return "%02d:%02d:%02d".format(h, m, s)
		}
		return null // Placeholder não encontrado
	}
}

