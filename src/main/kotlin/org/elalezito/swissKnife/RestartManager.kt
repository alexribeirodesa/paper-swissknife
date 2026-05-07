package org.elalezito.swissKnife

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.plugin.java.JavaPlugin
import org.elalezito.swissKnife.objects.Toolkit
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// say Servidor vai reiniciar em §4§l10§r minutos!

class RestartManager(private val plugin: JavaPlugin) {
	private val mm = MiniMessage.miniMessage()
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
				val alertSeconds = plugin.config.getIntegerList("restart.alerts")
				val diff: Long = getSecondsUntilRestart()

				if (diff.toInt() in alertSeconds) {
					val seconds = diff.toInt()
					val type = if (seconds >= 60) "minutes" else "seconds"
					val value = if (seconds >= 60) seconds / 60 else seconds

					val message: String = (plugin.config.getString("restart.messages.$type") ?: "-")
						.replace("#", value.toString())
					//val component = mm.deserialize(message.replace("#", value.toString()))

					plugin.server.onlinePlayers.forEach { player ->
						Toolkit.send(player, message)
						//player.sendMessage(message)
					}
				}

				if (diff <= 0L) {
					val message: String = plugin.config.getString("restart.messages.restarting") ?: "-"
					//val component = mm.deserialize(message)

					plugin.server.onlinePlayers.forEach { player ->
						Toolkit.send(player, message)
						//player.sendMessage(component)
					}
				}

				delay(1000)
			}
		}
	}
}