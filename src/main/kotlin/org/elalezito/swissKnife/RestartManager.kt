package org.elalezito.swissKnife

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.bukkit.plugin.java.JavaPlugin
import org.elalezito.swissKnife.objects.Config
import org.elalezito.swissKnife.objects.Toolkit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// say Servidor vai reiniciar em §4§l10§r minutos!

class RestartManager(private val plugin: JavaPlugin) {
	private val formatter = DateTimeFormatter.ofPattern("H:mm")

	fun getSecondsUntilRestart(): Long {
		val now = LocalDateTime.now()
		val nextSchedule = getNextSchedule().atDate(LocalDate.now())

		val diff: Long = if (nextSchedule.isAfter(now)) {
			ChronoUnit.SECONDS.between(now, nextSchedule)
		} else {
			ChronoUnit.SECONDS.between(now,nextSchedule.plusDays(1) )
		}

		return diff
	}

	private fun getNextSchedule(): LocalTime {
		val now = LocalTime.now()

		val schedules = plugin.config.getStringList("restart.schedules")
			.map { LocalTime.parse(it, formatter) }
			.sorted()

		return schedules.firstOrNull { it.isAfter(now) } ?: schedules.first()
	}

	fun startRestartLoop() {
		plugin.launch {
			while (isActive) {
				//plugin.config.getIntegerList("restart.alerts")
				val diff: Long = getSecondsUntilRestart()

				if (diff.toInt() in Config.restart.alerts) {
					val seconds = diff.toInt()
					val type = if (seconds >= 60) "minutes" else "seconds"
					val value = if (seconds >= 60) seconds / 60 else seconds

					val message: String = (Config.restart.messages[type] ?: "err-restartMessages$type")
						.replace("#", value.toString())

					plugin.server.onlinePlayers.forEach { player ->
						Toolkit.send(player, message)
					}
				}

				if (diff <= 0L) {
					val message: String = Config.restart.messages["restarting"] ?: "err-restartMessagesRestarting"

					plugin.server.onlinePlayers.forEach { player ->
						Toolkit.send(player, message)
					}
				}

				delay(1000)
			}
		}
	}
}