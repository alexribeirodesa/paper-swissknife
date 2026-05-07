package org.elalezito.swissKnife

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.entity.Wither
import org.bukkit.plugin.java.JavaPlugin
import org.elalezito.swissKnife.objects.Toolkit
import java.util.UUID
import kotlin.math.absoluteValue

class BossBarManager(private val plugin: JavaPlugin, private val deathManager: DeathManager) {
	private val activeBossBars = mutableMapOf<UUID, BossBar>()
	private val playerLastPosition = mutableMapOf<UUID, Location>()
	private val PlayerLastPositionCooldown = mutableMapOf<UUID, Int>()
	private val mm = MiniMessage.miniMessage()

	fun isPlayerMoving(player: Player): Boolean {
		val lastPosition = playerLastPosition[player.uniqueId];
		val currentPosition = player.location
		playerLastPosition[player.uniqueId] = currentPosition

		if(lastPosition == null) return false
		if(lastPosition.world != currentPosition.world) return false

		val deltaX = currentPosition.x - lastPosition.x
		val deltaZ = currentPosition.z - lastPosition.z
		val deltaYaw = currentPosition.yaw != lastPosition.yaw
		val distanceSq = (deltaX * deltaX) + (deltaZ * deltaZ)

		val output = distanceSq > 0.001

		if(output || deltaYaw)
			PlayerLastPositionCooldown[player.uniqueId] = 10

		return output
	}

	fun canShowBossBar(player: Player): Boolean {
		//val activeBars = player.activeBossBars()
		//val bossBar: BossBar = activeBossBars[player.uniqueId] ?: return false

		// bossbar
		val hasVanillaBoss = player.world.entities.any {
			(it is Wither && it.location.distance(player.location) < (96 + 8)) ||
			(it is EnderDragon && it.location.distance(player.location) < (192 + 8))
		}

		return !hasVanillaBoss
	}

	fun createBossBar(player: Player) {
		player.location

		val config = plugin.config
		val bossBarEnabled: Boolean = config.getBoolean("hud.bossbar.enabled", false)

		val bossBar = BossBar.bossBar(
			mm.deserialize(">> OH! HELLO THERE! <<"),
			1.0f,
			BossBar.Color.GREEN,
			BossBar.Overlay.PROGRESS
		)

		activeBossBars[player.uniqueId] = bossBar;
		playerLastPosition[player.uniqueId] = player.location
		PlayerLastPositionCooldown[player.uniqueId] = 0

		// player.showBossBar(bossBar)
	}

	fun destroyBossBar(player: Player) {
		val config = plugin.config
		val bossBarEnabled: Boolean = config.getBoolean("hud.bossbar.enabled", false)

		val bossBar = activeBossBars.remove(player.uniqueId)
		playerLastPosition.remove(player.uniqueId)
		PlayerLastPositionCooldown.remove(player.uniqueId)

		if (bossBar != null) {
			player.hideBossBar(bossBar)
		}
	}

	fun showBossBar(player: Player) {
		val bossBar = activeBossBars.remove(player.uniqueId)

		if (bossBar != null) {
			player.showBossBar(bossBar)
		}
	}

	fun hideBossBar(player: Player) {
		val bossBar = activeBossBars.remove(player.uniqueId)

		if (bossBar != null) {
			player.hideBossBar(bossBar)
		}
	}

	fun startBossBarLoop() {
		plugin.launch {
			while (isActive) {
				val config = plugin.config
				val bossBarEnabled: Boolean = config.getBoolean("hud.bossbar.enabled", false)
				val compassEnabled: Boolean = config.getBoolean("hud.compass.enabled", false)


				val compassNorth: String = config.getString("hud.compass.north") ?: "N"
				val compassSouth: String = config.getString("hud.compass.south") ?: "S"
				val compassEast: String = config.getString("hud.compass.east") ?: "E"
				val compassWest: String = config.getString("hud.compass.west") ?: "W"
				val compassDivider: String = config.getString("hud.compass.divider") ?: " - - | - - "

				val compassWaypointDeathEnabled: Boolean = config.getBoolean("hud.compass.waypoints.death.enabled", false)
				val compassWaypointDeathIcon: String = config.getString("hud.compass.waypoints.death.icon") ?: "D"
				val compassWaypointDeathMessageNear: String = config.getString("hud.compass.waypoints.death.messages.near") ?: "D"

				plugin.server.onlinePlayers.forEach { player ->
					val bossBar = activeBossBars[player.uniqueId] ?: return@forEach
					val deathList = deathManager.getDeathList(player)
					val positionCooldown: Int = PlayerLastPositionCooldown[player.uniqueId] ?: 0

					if (bossBarEnabled && canShowBossBar(player)) {
						player.showBossBar(bossBar)

						val isMoving = isPlayerMoving(player)
						var component: Component;

						if(!isMoving && positionCooldown > 0) {
							PlayerLastPositionCooldown[player.uniqueId] = positionCooldown - 1
						}

						if(compassEnabled &&
							(isMoving || positionCooldown > 0)) {
							var normalizedYaw = (player.yaw % 360)
							if (normalizedYaw < 0) normalizedYaw += 360

							// compass base strip (360°)
							//val strip = "L   -   -   |   -   -   S   -   -   |   -   -   O   -   -   |   -   -   N   -   -   |   -   -   "
							var strip = StringBuilder("${compassEast}${compassDivider}${compassSouth}${compassDivider}${compassWest}${compassDivider}${compassNorth}${compassDivider}")

							// death waypoint
							if(compassWaypointDeathEnabled) {
								deathList.forEach { point ->
									val dX: Double = (point.x - player.location.blockX).toDouble()
									val dY: Double = (point.y - player.location.blockY).toDouble()
									val dZ: Double = (point.z - player.location.blockZ).toDouble()

									val dist: Int = (dX.absoluteValue + dY.absoluteValue + dZ.absoluteValue).toInt() / 3

									if (dist > 2 && dist < 10) {
										Toolkit.send(player, compassWaypointDeathMessageNear)
										deathManager.removeDeath(player, point)
										return@forEach
									}

									var deathAngle = Math.toDegrees(Math.atan2(dZ, dX))
									deathAngle = (deathAngle % 360 + 360) % 360
									val index: Int = ((deathAngle / 360.0) * strip.length).toInt()

									if (index in 0 until strip.length) {
										strip.setCharAt(index, compassWaypointDeathIcon.toCharArray()[0])
									}
								}
							}

							val axis = "${strip.toString()}${strip.toString()}"
							val index = ((normalizedYaw / 360.0) * strip.length).toInt()

							val rawText: String = axis.substring(index.toInt(), index.toInt() + (strip.length / 2 + 1))
							component = mm.deserialize(rawText)
						} else {
							val rawText: String = config.getString("hud.bossbar.message") ?: "MESSAGE NOT SET"
							val formatted = PlaceholderAPI.setPlaceholders(player, rawText)
							component = mm.deserialize(formatted)
						}

						bossBar.name(component)
					} else {
						player.hideBossBar(bossBar)
					}
				}

				delay(if (bossBarEnabled) 500 else 5000)
			}
		}
	}
}