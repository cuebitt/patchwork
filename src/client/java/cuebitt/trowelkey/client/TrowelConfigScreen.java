package cuebitt.trowelkey.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.stream.Collectors;

/**
 * Builds the ModMenu config screen for the mod using Cloth Config.
 *
 * <p>Exposes three options: the trowel item, the custom name that marks a trowel, and
 * whether the enchantment glint is shown. Changes are written back through the config and
 * persisted when the player saves.
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

        builder.setSavingRunnable(config::save);

        return builder.build();
    }
}
