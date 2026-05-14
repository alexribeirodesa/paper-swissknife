package org.elalezito.swissKnife.objects

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.plugin.java.JavaPlugin
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// welcome
data class WelcomeData(
	val enabled: Boolean,
	val title: String,
	val subtitle: String,
	val message: String
)

// hud
data class HudData(
	val actionbarData: HudActionbarData,
	val bossbarData: HudBossbarData,
	val compassData: HudCompassData,
)

data class HudActionbarData(
	val enabled: Boolean,
	val message: String
)

data class HudBossbarData(
	val enabled: Boolean,
	val message: String,
	val hideDuringRaids: Boolean,
	val hideNearBosses: Boolean
)

data class HudCompassData(
	val enabled: Boolean,
	val north: String,
	val south: String,
	val east: String,
	val west: String,
	val divider: String,
	val waypoints: Map<String, HudCompassWaypointsData> = emptyMap()
)

data class HudCompassWaypointsData(
	val enabled: Boolean = true,
	val icon: String = "",
	val color: String = "<white>",
	val despawnTime: Int = 1800,
	val message: Map<String, String> = emptyMap()
)

// restart
data class RestartData(
	val enabled: Boolean,
	var schedules: List<LocalTime> = emptyList(),
	var alerts: List<Int> = emptyList(),
	val messages: Map<String, String> = emptyMap()
)

object Config {
	private val mm = MiniMessage.miniMessage()
	private val legacySerializer = LegacyComponentSerializer.legacySection()

	private lateinit var plugin: JavaPlugin

	private lateinit var _welcome: WelcomeData
	val welcome: WelcomeData
		get() = _welcome

	private lateinit var _hud: HudData
	val hud: HudData
		get() = _hud

	private lateinit var _restart: RestartData
	val restart: RestartData
		get() = _restart

	fun init(javaPlugin: JavaPlugin) {
		plugin = javaPlugin
		plugin.saveDefaultConfig()
		load()
	}

	fun load() {
		Toolkit.log("Carregando configuração.")
		plugin.reloadConfig()

		// welcome
		_welcome = WelcomeData(
			getBool("welcome.enabled", true),
			getString("welcome.title", "<gold>Server <aqua>Name"),
			getString("welcome.subtitle", "<gray>Welcome <white>%player_name%</white>!"),
			getString("welcome.message", "<green>Welcome back to the server, <white>%player_name%!")
		)

		// hud
		_hud = HudData(
			HudActionbarData(
				getBool("hud.actionbar.enabled", true),
				getString("hud.actionbar.message", "Next restart at <gray>%swissknife_restarttime_formated%")
			),
			HudBossbarData(
				getBool("hud.bossbar.enabled", true),
				getString("hud.bossbar.message", "<gray>Player: <white>%player_name% <red>❤ %player_health_rounded%"),
				getBool("hud.bossbar.hide-during-raids", true),
				getBool("hud.bossbar.hide-near-bosses", true)
			),
			HudCompassData(
				getBool("hud.compass.enabled", true),
				getString("hud.compass.north", "N"),
				getString("hud.compass.south", "S"),
				getString("hud.compass.east", "E"),
				getString("hud.compass.west", "W"),
				getString("hud.compass.divider", "  -  -  |  -  -  "),
				mapOf(
					"death" to HudCompassWaypointsData(
						getBool("hud.compass.waypoints.death.enabled", true),
						getString("hud.compass.waypoints.death.icon", "X"),
						getString("hud.compass.waypoints.death.color", "<white>"),
						getInt("hud.compass.waypoints.death.despawn-time", 1800),
						mapOf(
							"death" to getString("hud.compass.waypoints.death.messages.death", "#I <yellow>%player_name% died at location <white>#X #Y #Z (#W)."),
							"near" to getString("hud.compass.waypoints.death.messages.near", "<yellow>You are close to your items!")
						)
					)
				)
			)
		)

		// reload
		_restart = RestartData(
			getBool("hud.compass.enabled", true),
			emptyList(),
			emptyList(),
			mapOf(
				"minutes" to getString("hud.compass.messages.minutes", "<yellow>The server will restart in <white># <yellow>minutes!"),
				"minute" to getString("hud.compass.messages.minute", "<yellow>The server will restart in <white># <yellow>minute!"),
				"seconds" to getString("hud.compass.messages.seconds", "<yellow>The server will restart in <white># <yellow>seconds!"),
				"second" to getString("hud.compass.messages.minusecondtes", "<yellow>The server will restart in <white># <yellow>second!"),
				"restarting" to getString("hud.compass.messages.restarting", "<red>Restarting now...")
			)
		)

		_restart.schedules = plugin.config.getStringList("restart.schedules")
			.map { LocalTime.parse(it, DateTimeFormatter.ofPattern("H:mm")) }
			.sorted()

		_restart.alerts = plugin.config.getIntegerList("restart.alerts")

		Toolkit.log("Configuração carregada com sucesso!")
	}

	private fun getInt(path: String, default: Int = 0): Int {
		val config = plugin.config
		return config.getInt(path, default)
	}

	private fun getBool(path: String, default: Boolean = false): Boolean {
		val config = plugin.config
		return config.getBoolean(path, default)
	}

	private fun getString(path: String, default: String = "NULLERR"): String {
		val config = plugin.config
		return config.getString(path) ?: default
	}
}