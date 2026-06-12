# Trowel Key

A Fabric mod for Minecraft 1.21.1 that adds a hotkey to place a random placeable block from your hotbar.

## Features

- Press **R** (configurable) to place a random block from your hotbar
- Only selects placeable blocks (excludes tools, armor, food, etc.)
- Only uses occupied slots
- Automatically restores your original hotbar selection after placing

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.1
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release from [GitHub](https://github.com/cuebitt/trowel-key/releases)
4. Place the `.jar` file in your `mods` folder

## Configuration

The key binding can be changed in Minecraft's **Controls** menu under the **Trowel Key** category.

## Building from Source

```bash
./gradlew build
```

The built jar will be in `build/libs/`.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

This mod's icon uses a texture from [malcolmriley/unused-textures](https://github.com/malcolmriley/unused-textures), which is provided under the [CC-BY-4.0](https://github.com/malcolmriley/unused-textures/blob/master/LICENSE) license.