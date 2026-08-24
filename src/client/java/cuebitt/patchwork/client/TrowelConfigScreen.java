package cuebitt.patchwork.client;

import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.LabelOption;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import java.util.ArrayList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Builds the ModMenu config screen for the mod using YACL.
 *
 * <p>Exposes the trowel item and its name, the enchantment glint toggle, and the default trowel
 * mode, and a reset for per-trowel modes under General; the hotkey enable flag, mode, and whether a
 * trowel is required under Hotkey; and the block filter under Filtering. Changes are written back
 * through the config and persisted when the player saves.
 */
public class TrowelConfigScreen {

  /** Creates the config screen with {@code parent} as its back button target. */
  public static Screen createScreen(Screen parent) {
    TrowelConfig config = TrowelConfig.getInstance();

    return YetAnotherConfigLib.createBuilder()
        .title(Component.translatable("title.patchwork.config"))
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("category.patchwork.general"))
                .option(
                    Option.<String>createBuilder()
                        .name(Component.translatable("option.patchwork.trowel_item"))
                        .binding(
                            "minecraft:iron_shovel",
                            () -> {
                              var key = BuiltInRegistries.ITEM.getKey(config.getTrowelItem());
                              return key != null ? key.toString() : "minecraft:iron_shovel";
                            },
                            newVal -> {
                              try {
                                var item =
                                    BuiltInRegistries.ITEM.get(ResourceLocation.parse(newVal));
                                if (item != null) {
                                  config.setTrowelItem(item);
                                }
                              } catch (Exception ignored) {
                                // invalid id - keep previous value
                              }
                            })
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(
                    Option.<String>createBuilder()
                        .name(Component.translatable("option.patchwork.trowel_name"))
                        .binding("Trowel", config::getTrowelName, config::setTrowelName)
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(
                    Option.<Boolean>createBuilder()
                        .name(Component.translatable("option.patchwork.show_enchant_glint"))
                        .binding(true, config::isShowEnchantGlint, config::setShowEnchantGlint)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(
                    Option.<TrowelMode>createBuilder()
                        .name(Component.translatable("option.patchwork.default_mode"))
                        .binding(TrowelMode.HOTBAR, config::getDefaultMode, config::setDefaultMode)
                        .controller(
                            opt ->
                                EnumControllerBuilder.create(opt)
                                    .enumClass(TrowelMode.class)
                                    .formatValue(
                                        mode ->
                                            Component.translatable(
                                                "enum.patchwork." + mode.name().toLowerCase())))
                        .build())
                .option(
                    ButtonOption.createBuilder()
                        .name(Component.translatable("option.patchwork.reset_per_trowel_modes"))
                        .text(
                            Component.translatable(
                                "option.patchwork.reset_per_trowel_modes.button"))
                        .action((screen, opt) -> PatchworkClient.resetModes())
                        .build())
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("category.patchwork.hotkey"))
                .option(
                    LabelOption.create(
                        Component.translatable("option.patchwork.hotkey_key.tooltip")))
                .option(
                    Option.<Boolean>createBuilder()
                        .name(Component.translatable("option.patchwork.hotkey_enabled"))
                        .binding(true, config::isHotkeyEnabled, config::setHotkeyEnabled)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .option(
                    Option.<TrowelMode>createBuilder()
                        .name(Component.translatable("option.patchwork.hotkey_mode"))
                        .binding(TrowelMode.HOTBAR, config::getHotkeyMode, config::setHotkeyMode)
                        .controller(
                            opt ->
                                EnumControllerBuilder.create(opt)
                                    .enumClass(TrowelMode.class)
                                    .formatValue(
                                        mode ->
                                            Component.translatable(
                                                "enum.patchwork." + mode.name().toLowerCase())))
                        .build())
                .option(
                    Option.<Boolean>createBuilder()
                        .name(Component.translatable("option.patchwork.hotkey_requires_trowel"))
                        .binding(
                            true, config::isHotkeyRequiresTrowel, config::setHotkeyRequiresTrowel)
                        .controller(TickBoxControllerBuilder::create)
                        .build())
                .build())
        .category(
            ConfigCategory.createBuilder()
                .name(Component.translatable("category.patchwork.filtering"))
                .option(
                    Option.<BlockFilterMode>createBuilder()
                        .name(Component.translatable("option.patchwork.block_filter_mode"))
                        .binding(
                            BlockFilterMode.NONE,
                            config::getBlockFilterMode,
                            config::setBlockFilterMode)
                        .controller(
                            opt ->
                                EnumControllerBuilder.create(opt)
                                    .enumClass(BlockFilterMode.class)
                                    .formatValue(
                                        mode ->
                                            Component.translatable(
                                                "enum.patchwork.filter."
                                                    + mode.name().toLowerCase())))
                        .build())
                .option(
                    ListOption.<String>createBuilder()
                        .name(Component.translatable("option.patchwork.block_filter"))
                        .binding(new ArrayList<>(), config::getBlockFilter, config::setBlockFilter)
                        .controller(StringControllerBuilder::create)
                        .initial("")
                        .build())
                .build())
        .save(config::save)
        .build()
        .generateScreen(parent);
  }
}
