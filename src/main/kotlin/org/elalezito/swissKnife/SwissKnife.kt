package org.elalezito.swissKnife

import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import org.elalezito.swissKnife.objects.Config
import org.elalezito.swissKnife.objects.Toolkit

class SwissKnife : JavaPlugin() {
	lateinit var actionBarManager: ActionBarManager
	lateinit var bossBarManager: BossBarManager
	lateinit var restartManager: RestartManager
	lateinit var deathManager: DeathManager

	override fun onEnable() {
		Toolkit.setPlugin(this)

		// inicia o config
		Config.init(this)

		// registra comandos
		val manager = this.lifecycleManager
		manager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
			val commands = event.registrar()

			commands.register(
				Commands.literal("swissknife")
					.then(
						Commands.literal("reload")
							.requires { source -> source.sender.hasPermission("swissknife.admin") }
							.executes { context ->
								Config.load()
								return@executes 1
							}
					).build(),
				"Comando principal do SwissKnife",
				listOf("sk")
			)
		}

		// inicia o gerenciador do actionbar
		actionBarManager = ActionBarManager(this)
		actionBarManager.startActionBarLoop()

		// inicia o gerenciador do bossbar
		deathManager = DeathManager(this)
		bossBarManager = BossBarManager(this, deathManager)
		bossBarManager.startBossBarLoop()

		// inicia o gerenciador do aviso de restart
		restartManager = RestartManager(this)
		restartManager.startRestartLoop()

		// registra eventos
		val playerListener = PlayerListener(this, deathManager, bossBarManager)
		server.pluginManager.registerEvents(playerListener, this)

		// registra placeholders
		if (server.pluginManager.getPlugin("PlaceholderAPI") != null) {
			SwissKnifeExpansion(this).register()
			Toolkit.log("PlaceholderAPI expansion registered!")
		}

		Toolkit.log("SwissKnife habilitado com sucesso!")
	}

	override fun onDisable() {
		Toolkit.log("SwissKnife desabilitado com sucesso!")
	}
}
