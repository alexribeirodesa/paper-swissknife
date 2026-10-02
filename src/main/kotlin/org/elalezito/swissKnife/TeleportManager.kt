package org.elalezito.swissKnife

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import org.elalezito.swissKnife.objects.Config
import org.elalezito.swissKnife.objects.HubTeleportPositionData
import java.time.LocalTime
import javax.swing.text.Position

class TeleportManager(private val plugin: JavaPlugin) {
	fun teleport(player: Player, world: String) {
		if (!Config.hub.teleport.enabled)
			return;

		val teleportData: HubTeleportPositionData = Config.hub.teleport.worlds[world] ?: return
		val targetWorld = Bukkit.getWorld(world)
		val targetLocation = Location(targetWorld, teleportData.x.toDouble(),
			teleportData.y.toDouble(), teleportData.z.toDouble(), 0.0F, 0.0F)

		player.server.scheduler.runTask(plugin, Runnable {
			player.teleportAsync(targetLocation).thenAccept { success ->
				if(success) {
					player.velocity = Vector(0,0,0)
					player.fallDistance = 0f
					player.isFlying = false
				}
			}
		})
	}
}