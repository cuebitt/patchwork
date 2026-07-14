# Patchwork

A Fabric mod for Minecraft 1.21.1 that lets you place random blocks from your
inventory with a hotkey or right-click.

## Features

- **Right-click** with the trowel to place a random block from your current
  mode's inventory
- **R** (configurable), same as right-click, bound to whichever mode is active
- **Shift+Right Click** switches between two modes:
  - **Hotbar mode**: picks blocks from your 9 hotbar slots
  - **Inventory mode**: picks blocks from your entire inventory (36 slots)
- Only placeable blocks are selected (no tools, armor, food, etc.)
- The enchantment glint appears on the trowel when in Inventory mode, as a
  visual indicator
- The glint can be disabled in the config if you prefer plain looks
- Your original hotbar selection is always restored after placing

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.1
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest release from
   [GitHub](https://github.com/cuebitt/trowel-key/releases)
4. Place the `.jar` file in your `mods` folder

## Configuration

- The key binding can be changed in Minecraft's **Controls** menu under the
  **Patchwork** category
- Additional options (trowel item, trowel name, enchantment glint toggle) are
  available via ModMenu or by editing `config/patchwork/config.json`

## Building from Source

```bash
./gradlew build
```

The built jar will be in `build/libs/`.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE)
file for details.

This mod's icon and the trowel item use a texture from
[malcolmriley/unused-textures](https://github.com/malcolmriley/unused-textures),
which is provided under the
[CC-BY-4.0](https://github.com/malcolmriley/unused-textures/blob/master/LICENSE)
license.
