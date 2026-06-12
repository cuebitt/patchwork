package cuebitt.trowelkey.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side mod entrypoint that registers a key binding to place a random
 * placeable block from
 * the player's hotbar.
 */
public class TrowelKeyClient implements ClientModInitializer {
  private static final String CATEGORY = "key.categories.trowel-key";
  private static final String KEY_PLACE_RANDOM = "key.trowel-key.place-random";

  /** The registered key binding for placing a random hotbar block. */
  public static KeyMapping placeRandomKeyMapping;

  @Override
  public void onInitializeClient() {
    placeRandomKeyMapping = KeyBindingHelper.registerKeyBinding(
        new KeyMapping(KEY_PLACE_RANDOM, GLFW.GLFW_KEY_R, CATEGORY));

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (placeRandomKeyMapping.consumeClick()) {
            placeRandomHotbarBlock(client);
          }
        });
  }

  /**
   * Places a random placeable block from the player's hotbar at the targeted
   * block. Temporarily
   * switches the selected hotbar slot, places the block, then restores the
   * original slot.
   *
   * @param client the Minecraft client instance
   */
  private void placeRandomHotbarBlock(Minecraft client) {
    if (client.player == null || client.level == null) {
      return;
    }

    List<Integer> placeableSlots = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      ItemStack stack = client.player.getInventory().getItem(i);
      if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
        placeableSlots.add(i);
      }
    }

    if (placeableSlots.isEmpty()) {
      return;
    }

    int randomSlot = placeableSlots.get(new Random().nextInt(placeableSlots.size()));
    int originalSlot = client.player.getInventory().selected;

    client.player.getInventory().selected = randomSlot;
    client.player.inventoryMenu.broadcastChanges();

    HitResult hitResult = client.hitResult;
    if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
      BlockHitResult blockHitResult = (BlockHitResult) hitResult;
      client.gameMode.useItemOn(client.player, client.player.getUsedItemHand(), blockHitResult);
    }

    client.player.getInventory().selected = originalSlot;
    client.player.inventoryMenu.broadcastChanges();
  }
}
