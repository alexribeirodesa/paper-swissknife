# SwissKnife 🛠️

![Minecraft](https://img.shields.io/badge/Minecraft-Paper%2026.1.2-orange)
![Build Version](https://img.shields.io/badge/version-1.0--SNAPSHOT-orange)
![Build Status](https://img.shields.io/github/actions/workflow/status/alexribeirodesa/paper-petrespawn/.github/workflows/main_build.yml?branch=main&label=build)
![Kotlin](https://img.shields.io/badge/language-Kotlin-purple)
![License](https://img.shields.io/badge/license-MIT-blue)

A versatile "all-in-one" utility plugin for PaperMC designed to handle alerts, player guidance, and server management notifications. Created to fill the gaps left by other tools, SwissKnife provides essential features with deep customization.

## ✨ Features
* **Custom Welcome System:** Greet players with fully customizable Titles, Subtitles, and Chat messages upon joining.

* **Dynamic BossBar & Death Compass:**
  * Displays a custom BossBar message.
  * Features a built-in compass pointing to the player's last 5 death locations.
  * **Smart Priority:** The BossBar automatically hides during Boss fights or Raids to keep the focus on the action.

* **Scheduled Restart Alerts:** Set up automated countdowns and messages for server maintenance, updates, or backups.
  * **Note:** This handles the notifications only; actual restarts should be managed via Cron or Pterodactyl schedules.
  * **Modern Formatting:** Full support for MiniMessage (rich colors/events) and PlaceholderAPI integration for all messages.

* **Scheduled Restart Alerts:** Set up automated countdowns and messages for server maintenance, updates, or backups.
  * **Note:** This handles the notifications only; actual restarts should be managed via Cron or Pterodactyl schedules.
  * **Modern Formatting:** Full support for MiniMessage (rich colors/events) and PlaceholderAPI integration for all messages.

## 🛠️ Requirements
*   **Server:** [PaperMC](https://papermc.io/) (Recommended version: 26.1.2)

## 🚀 Installation
1.  Download the latest `.jar` from the releases page.
2.  Ensure **GriefPrevention** is installed on your server.
3.  Drop the `PetRespawn.jar` into your `/plugins` folder.
4.  Restart or reload the server.

## ⌨️ Commands (TODO)
| Command | Description | Permission |
| :--- | :--- | :--- |

## ⚙️ Configuration
The configuration file will be generated at `plugins/PetRespawn/config.yml`.

Currently, it focuses on localization, allowing you to translate all messages sent to players:

This plugin supports **MiniMessage** (Adventure) tags for rich text formatting and internal placeholders for dynamic information.

```yaml
# Language Settings
messages:
  # Soul Egg
  soul-egg: "<white>Soul Egg"
  charged-soul-egg: "<aqua>Charged Soul Egg"
  soul-egg-lore: "<grey>Pet: {petname}"
```

## 🏗️ Development
Built with **Kotlin** for the Paper API.

### Build from source:
```bash
# Clone the repository
git clone https://github.com/alexribeirodesa/paper-petrespawn.git

# Build with Gradle
./gradlew build
```

## ☕ Support the Project
If this plugin helps your server, consider supporting its development!

[![Buy Me A Coffee](https://img.shields.io/badge/Buy%20Me%20A%20Coffee-Donate-yellow?style=for-the-badge&logo=buy-me-a-coffee)](https://www.buymeacoffee.com/elalezito)

*Every coffee helps me keep the "Paper Projects" series updated and free.*