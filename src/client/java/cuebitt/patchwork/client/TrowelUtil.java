package cuebitt.patchwork.client;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

/**
 * Helpers for recognising a configured trowel item stack.
 *
 * <p>A trowel is an item matching the configured trowel item (default iron shovel) that also
 * carries the configured custom name. Matching the custom name lets an ordinary shovel be turned
 * into a trowel simply by renaming it.
 */
public class TrowelUtil {

  public static boolean isTrowel(ItemStack stack) {
    if (stack.isEmpty()) return false;

    TrowelConfig config = TrowelConfig.getInstance();

    if (stack.getItem() != config.getTrowelItem()) return false;

    if (!stack.has(DataComponents.CUSTOM_NAME)) return false;

    String customName = stack.get(DataComponents.CUSTOM_NAME).getString();
    return customName.equals(config.getTrowelName());
  }
}
