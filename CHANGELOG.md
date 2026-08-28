# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.4.0] - 2026-08-28

### Added

- Custom trowel item texture authored in Aseprite (`assets-src/textures/item/trowel.ase`) and build-time rendering pipeline (`assets-src/export.py`) via `aseprite-reader` — no Aseprite binary required; output to `build/generated/resources`. Script supports both `.ase` and `.aseprite` (case-insensitive), single parse per file, overwrite-safe render, per-file error handling with non-zero exit on failure, and `Ruff` clean.

### Changed

- Updated mod description in `fabric.mod.json`.
- Trimmed `README.md` attribution for the previous `malcolmriley/unused-textures` trowel texture.

### Fixed

- Hotkey (`R`) no longer requires a trowel to be held — placement now uses the configured `hotkey_mode` regardless of held item (right-click still requires a trowel via `MixinMinecraft_StartUse`).

### Removed

- `Require Trowel for Hotkey` setting (`TrowelConfig.hotkey_requires_trowel`, `TrowelConfigScreen` option, `en_us.json` key) and the `requireTrowel` gating in `PatchworkClient.placeRandomBlock` (now `placeRandomBlock(Minecraft, TrowelMode)`; early-return moved to hotkey-enabled check).
- Committed `src/client/resources/assets/patchwork/textures/item/trowel.png` in favour of the generated asset (now gitignored via source `assets-src`).

## [1.3.0] - 2026-08-24

### Changed

- Replaced Cloth Config with Yet Another Config Lib (YACL) for the config
  screen (`yacl_version=3.6.1+1.21-fabric`, `maven.isxander.dev`).
- Reworked `TrowelConfigScreen` on top of YACL and removed the custom
  `ResetButtonEntry` widget.

## [1.1.2] - 2026-08-24

### Added

- Spotless CI workflow (`.github/workflows/spotless.yml`) running
  `./gradlew spotlessCheck` on push and pull requests.

### Changed

- Streamlined random block placement in `PatchworkClient.java:147` — consolidated
  `placeRandomBlockFromInventory` / `placeRandomHotbarBlock` into a single
  `placeRandomFromRange` helper.
- Cleaned up `TrowelConfig.java` (removed unused `getTrowelItemId` and
  redundant Javadoc).

### Removed

- Unused trowel item mixin `MixinShovelItem.java` and its entry in
  `patchwork.client.mixins.json`.

## [1.1.1] - 2026-07-14

### Changed

- Updated project and repository links to match the renamed Patchwork Modrinth
  project and GitHub repository.
- Bumped the mod version to 1.1.1 so built jars carry the correct Modrinth and
  source URLs.

## [1.1.0] - 2026-07-14

### Added

- A configurable trowel item with an enchantment glint shown in Inventory mode.
- Hotbar and Inventory placement modes, toggled with Shift + Right Click.
- Cloth Config and Mod Menu integration for in-game settings.
- Hotkey settings, block filtering, and a reset for per-trowel modes.

### Changed

- Rebranded the mod from Trowel Key to Patchwork.

## [1.0.0] - 2026-06-12

### Added

- Initial release of Trowel Key for Minecraft 1.21.1.
- Press **R** (configurable) to place a random placeable block from the hotbar.
- Only placeable blocks from occupied slots are selected, and the original
  hotbar selection is restored after placing.
