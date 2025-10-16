# HyperTP v{{VERSION}}

Advanced teleportation plugin with GUI support for Minecraft 1.21+

## 🚀 What's New

{{CHANGELOG}}

## 📦 Installation

1. Download the JAR file from the assets below
2. Place it in your server's `plugins/` folder
3. Restart your server
4. Configure the plugin in `plugins/HyperTP/config.yml`

## ⚙️ Features

- 🏠 **Home System** - Set, delete, and teleport to homes
- 📞 **TPA System** - Send and accept teleport requests
- 🎯 **RTP** - Random teleport to safe locations
- ↩️ **Back System** - Return to previous location
- 🌐 **Multi-language** - English & Vietnamese support
- ⚡ **Folia Support** - Optimized for multi-threaded servers
- 🎨 **Custom GUIs** - Fully customizable interfaces

## 🔧 Requirements

- Minecraft Server: 1.21 - 1.21.8
- Server Type: Paper or Folia
- Java Version: 21+

## 📋 Commands

```yaml
/home [name]       # Teleport to home
/sethome <name>    # Set a home
/delhome <name>    # Delete a home
/homelist          # Open homes GUI
/tpa <player>      # Request to teleport to player
/tpahere <player>  # Request player to teleport to you
/tpaccept          # Accept TPA request
/tpdeny            # Deny TPA request
/back              # Return to previous location
/rtp               # Random teleport
/htp reload        # Reload configuration
/htp help          # Show help
