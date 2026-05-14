# SwissKnife 🛠️

![Minecraft](https://img.shields.io/badge/Minecraft-Paper%2026.1.2-orange)
![Build Version](https://img.shields.io/badge/version-1.0--SNAPSHOT-orange)
![Kotlin](https://img.shields.io/badge/language-Kotlin-purple)
![License](https://img.shields.io/badge/license-MIT-blue)

SwissKnife is a tailored utility plugin for PaperMC built to solve a specific problem: the lack of flexibility in existing notification tools.

Developed to meet the unique demands of a production server, it combines essential alerts and player guidance tools into a single, highly customizable package.

## ✨ Features
* **Custom Welcome System:** Greet players with fully customizable Titles, Subtitles, and Chat messages upon joining.

* **Dynamic BossBar & Death Compass:**
  * Displays a custom BossBar message.
  * Features a built-in compass pointing to the player's last 5 death locations.
  * **Smart Priority:** The BossBar automatically hides during Boss fights or Raids to keep the focus on the action.

* **Scheduled Restart Alerts:** Set up automated countdowns and messages for server maintenance, updates, or backups.
  * **Note:** This handles the notifications only; actual restarts should be managed via Cron or Pterodactyl schedules.
  * **Modern Formatting:** Full support for MiniMessage (rich colors/events) and PlaceholderAPI integration for all messages.

## 🛠️ Requirements
* **Server:** [PaperMC](https://papermc.io/) (Recommended version: 26.1.2)
* **Dependency:** [GriefPrevention](https://modrinth.com/plugin/placeholderapi) (Required)

## 🚀 Installation
1. Download the latest `.jar` from the releases page.
2. Ensure **PlaceholderAPI** is installed on your server.
3. Drop the `swissknife.jar` into your `/plugins` folder.
4. Restart or reload the server.

## ⌨️ Commands
| Command | Description | Permission |
| :--- | :--- | :--- |
| `/swissknife reload` | Reload config file. | *Admin* |

## ⚙️ Configuration
The configuration file will be generated at `plugins/SwissKnife/config.yml`.

Everything in SwissKnife is designed to be personalized. You can toggle modules on or off and adjust every string to match your server's style using the `config.yml`

This plugin supports **MiniMessage** (Adventure) tags for rich text formatting and internal placeholders for dynamic information.

```yaml
welcome:
  enabled: true
  title: "<gold>Server <aqua>Name"
  subtitle: "<gray>Welcome <white>%player_name%</white>!"
  message: |
    <green>Welcome back to the server, <white>%player_name%!
    <gold>• <yellow>Use <gold>/home <yellow>to return to your base.
    <gold>• <yellow>Use <gold>/claim <yellow>to protect a new territory.
    <gold>• <yellow>Join our <gold>Discord <yellow>to report any issues!
    <aqua>[NOTICE] <white>This server may have players recording or streaming.
    <gray>Please avoid sharing personal info or using inappropriate language.

hud:
  actionbar:
    enabled: true
    message: "Next restart at <gray>%swissknife_restarttime_formated%"
  bossbar:
    enabled: true
    message: "<gray>Player: <white>%player_name% <red>❤ %player_health_rounded%"
    hide-during-raids: true
    hide-near-bosses: true
  compass:
    enabled: true
    north: "N"
    south: "S"
    east: "E"
    west: "W"
    divider: "  -  -  |  -  -  "
    waypoints:
      death:
        enabled: true
        icon: "☠"
        color: "<white>"
        despawn-time: 1800
        messages:
          death: "#I <yellow>%player_name% died at location <white>#X #Y #Z (#W)."
          near: "<yellow>You are close to your items!"
      waypoint:
        enabled: true
        icon: "V"
        note: "TODO"
      spawn:
        enabled: true
        icon: "#"
        node: "TODO"

restart:
  enabled: true
  schedules:
    - "3:00"
    - "12:00"
    - "18:19"
  alerts: [1800, 600, 60, 30, 10, 3, 2, 1]
  messages:
    minutes: "<yellow>The server will restart in <white># <yellow>minutes!"
    minute: "<yellow>The server will restart in <white># <yellow>minute!"
    seconds: "<yellow>The server will restart in <white># <yellow>seconds!"
    second: "<yellow>The server will restart in <white># <yellow>second!"
    restarting: "<red>Restarting now..."
```

## 🏗️ Development
Built with **Kotlin** for the Paper API.

### Build from source:
```bash
# Clone the repository
git clone [https://github.com/alexribeirodesa/paper-swissknife.git](https://github.com/alexribeirodesa/paper-swissknife.git)

# Build with Gradle
./gradlew build
```

## ☕ Support the Project
If this plugin helps your server, consider supporting its development!

[![Buy Me A Coffee](https://img.shields.io/badge/Buy%20Me%20A%20Coffee-Donate-yellow?style=for-the-badge&logo=buy-me-a-coffee)](https://www.buymeacoffee.com/elalezito)

*Every coffee helps me keep the "Paper Projects" series updated and free.*
