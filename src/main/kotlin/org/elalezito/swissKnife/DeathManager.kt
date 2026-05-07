package org.elalezito.swissKnife

import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.elalezito.swissKnife.data.DeathPoint
import org.elalezito.swissKnife.objects.Toolkit
import java.io.File
import java.io.IOException
import java.util.UUID

class DeathManager(private val plugin: JavaPlugin) {
	val config = plugin.config

	val compassWaypointDeathIcon: String = config.getString("hud.compass.waypoints.death.icon") ?: "D"
	val compassWaypointDeathMessage: String = config.getString("hud.compass.waypoints.death.messages.death") ?: "#X #Y #Z (#W)"
	val compassWaypointDeathDespawnTime: Int = config.getInt("hud.compass.waypoints.death.despawn-time") ?: 1800

	private val deathNote = mutableMapOf<UUID, MutableList<DeathPoint>>()

	fun loadDeathList(player: Player) {
		val file = File(plugin.dataFolder, "player/${player.uniqueId}.yml")
		if(!file.exists()) return

		val yaml = YamlConfiguration.loadConfiguration(file)
		val history = yaml.getList("history") as? List<Map<String, Any>> ?: return

		val deaths = history.mapNotNull { death ->
			try {
				DeathPoint(
					world_key = death["world_key"] as String,
					x = death["x"] as Int,
					y = death["y"] as Int,
					z = death["z"] as Int,
					timestamp = death["timestamp"] as Long
				)
			} catch (e: Exception) { null }
		}.toMutableList()

		deathNote[player.uniqueId] = deaths;
	}

	fun saveDeathList(player: Player) {
		val file = File(plugin.dataFolder, "player/${player.uniqueId}.yml")
		val config = YamlConfiguration.loadConfiguration(file)

		val serializedDeaths = deathNote[player.uniqueId]!!.map {
			mapOf(
				"world_key" to it.world_key,
				"x" to it.x,
				"y" to it.y,
				"z" to it.z,
				"timestamp" to it.timestamp
			)
		}

		config.set("history", serializedDeaths)

		try{
			config.save(file)
		} catch (e: IOException) {
			Toolkit.log("Não foi possível salvar a morte de ${player.name}!")
		}
	}

	fun deathMessage(player: Player, location: Location) {
		// #X #Y #Z (#W)
		// "\uE8F5"

		Toolkit.send(player, compassWaypointDeathMessage
			.replace("#I", compassWaypointDeathIcon)
			.replace("#X", location.blockX.toString())
			.replace("#Y", location.blockY.toString())
			.replace("#Z", location.blockZ.toString())
			.replace("#W", location.world.name)
		)
	}

	fun clearDeathList(player: Player) {
		saveDeathList(player)
		deathNote.remove(player.uniqueId)
	}

	fun getDeathList(player: Player): List<DeathPoint> {
		val list = deathNote.getOrPut(player.uniqueId) {mutableListOf()}

		list.removeIf { it.isExpired(compassWaypointDeathDespawnTime) }
		return list.filter { it.world_key == player.world.name }
	}

	fun addDeath(player: Player, location: Location) {
		val list = deathNote.getOrPut(player.uniqueId) {mutableListOf()}

		val newDeathPoint = DeathPoint(
			world_key = player.world.name,
			x = location.blockX,
			y = location.blockY,
			z = location.blockZ
		)

		list.add(0, newDeathPoint)

		if(list.size > 5) {
			list.removeLast()
		}

		saveDeathList(player)
	}

	fun removeDeath(player: Player, point: DeathPoint) {
		val list = deathNote.getOrPut(player.uniqueId) {mutableListOf()}
		list.remove(point)

		saveDeathList(player)
	}
}