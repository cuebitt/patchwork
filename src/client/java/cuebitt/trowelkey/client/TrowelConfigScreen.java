package cuebitt.trowelkey.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds the ModMenu config screen for the mod using Cloth Config.
 *
 * <p>Exposes the trowel item and its name, the enchantment glint toggle, and the default
 * trowel mode, and a reset for per-trowel modes under General; the hotkey enable flag, mode,
 * and whether a trowel is required under Hotkey; and the block filter under Filtering. Changes
 * are written back through the config and persisted when the player saves.
 */
public class TrowelConfigScreen {

    /**
     * Creates the config screen with {@code parent} as its back button target.
     */
    public static Screen createScreen(Screen parent) {
        TrowelConfig config = TrowelConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("title.trowel-key.config"));

        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable("category.trowel-key.general"));

        general.addEntry(builder.entryBuilder()
                .startDropdownMenu(
                        Component.translatable("option.trowel-key.trowel_item"),
                        DropdownMenuBuilder.TopCellElementBuilder.ofItemObject(config.getTrowelItem()),
                        DropdownMenuBuilder.CellCreatorBuilder.ofItemObject()
                )
                .setDefaultValue(Items.IRON_SHOVEL)
                .setSelections(BuiltInRegistries.ITEM.stream().collect(Collectors.toSet()))
                .setSaveConsumer(item -> config.setTrowelItem((Item) item))
                .build());

        general.addEntry(builder.entryBuilder()
                .startStrField(
                        Component.translatable("option.trowel-key.trowel_name"),
                        config.getTrowelName()
                )
                .setDefaultValue("Trowel")
                .setSaveConsumer(config::setTrowelName)
                .build());

        general.addEntry(builder.entryBuilder()
                .startBooleanToggle(
                        Component.translatable("option.trowel-key.show_enchant_glint"),
                        config.isShowEnchantGlint()
                )
                .setDefaultValue(true)
                .setSaveConsumer(config::setShowEnchantGlint)
                .build());

        general.addEntry(builder.entryBuilder()
                .startEnumSelector(
                        Component.translatable("option.trowel-key.default_mode"),
                        TrowelMode.class,
                        config.getDefaultMode()
                )
                .setDefaultValue(TrowelMode.HOTBAR)
                .setEnumNameProvider(mode -> Component.translatable("enum.trowel-key." + mode.name().toLowerCase()))
                .setSaveConsumer(config::setDefaultMode)
                .build());

        general.addEntry(new ResetButtonEntry(
                Component.translatable("option.trowel-key.reset_per_trowel_modes"),
                Component.translatable("option.trowel-key.reset_per_trowel_modes.button"),
                TrowelKeyClient::resetModes));

        ConfigCategory hotkey = builder.getOrCreateCategory(
                Component.translatable("category.trowel-key.hotkey"));

        hotkey.addEntry(builder.entryBuilder()
                .fillKeybindingField(
                        Component.translatable("option.trowel-key.hotkey_key"),
                        TrowelKeyClient.placeRandomKeyMapping)
                .setTooltip(Component.translatable("option.trowel-key.hotkey_key.tooltip"))
                .build());

        hotkey.addEntry(builder.entryBuilder()
                .startBooleanToggle(
                        Component.translatable("option.trowel-key.hotkey_enabled"),
                        config.isHotkeyEnabled()
                )
                .setDefaultValue(true)
                .setSaveConsumer(config::setHotkeyEnabled)
                .build());

        hotkey.addEntry(builder.entryBuilder()
                .startEnumSelector(
                        Component.translatable("option.trowel-key.hotkey_mode"),
                        TrowelMode.class,
                        config.getHotkeyMode()
                )
                .setDefaultValue(TrowelMode.HOTBAR)
                .setEnumNameProvider(mode -> Component.translatable("enum.trowel-key." + mode.name().toLowerCase()))
                .setSaveConsumer(config::setHotkeyMode)
                .build());

        hotkey.addEntry(builder.entryBuilder()
                .startBooleanToggle(
                        Component.translatable("option.trowel-key.hotkey_requires_trowel"),
                        config.isHotkeyRequiresTrowel()
                )
                .setDefaultValue(true)
                .setSaveConsumer(config::setHotkeyRequiresTrowel)
                .build());

        ConfigCategory filtering = builder.getOrCreateCategory(
                Component.translatable("category.trowel-key.filtering"));

        filtering.addEntry(builder.entryBuilder()
                .startEnumSelector(
                        Component.translatable("option.trowel-key.block_filter_mode"),
                        BlockFilterMode.class,
                        config.getBlockFilterMode()
                )
                .setDefaultValue(BlockFilterMode.NONE)
                .setEnumNameProvider(mode -> Component.translatable("enum.trowel-key.filter." + mode.name().toLowerCase()))
                .setSaveConsumer(config::setBlockFilterMode)
                .build());

        filtering.addEntry(builder.entryBuilder()
                .startStrList(
                        Component.translatable("option.trowel-key.block_filter"),
                        config.getBlockFilter()
                )
                .setDefaultValue(new ArrayList<>())
                .setSaveConsumer(config::setBlockFilter)
                .build());

        builder.setSavingRunnable(config::save);

        return builder.build();
    }
}
