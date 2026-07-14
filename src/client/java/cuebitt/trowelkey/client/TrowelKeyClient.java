package cuebitt.trowelkey.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import org.lwjgl.glfw.GLFW;

/**
 * Client entry point for the Trowel Key mod.
 *
 * <p>Registers the random-place keybinding and the "is_trowel" item property that the
 * glint mixin relies on. Actual placement is triggered either by that keybinding (handled
 * here on the client tick) or by a right-click, which {@code MixinMinecraft_StartUse}
 * intercepts and redirects to {@link #placeRandomBlock(Minecraft)}.
 */
public class TrowelKeyClient implements ClientModInitializer {
  private static final String CATEGORY = "key.categories.trowel-key";
  private static final String KEY_PLACE_RANDOM = "key.trowel-key.place-random";

  public static KeyMapping placeRandomKeyMapping;

  private static final Random RANDOM = new Random();
  private static final Map<String, TrowelMode> MODE_MAP = new HashMap<>();

  /**
   * Builds a stable lookup key for a trowel stack from its item id and custom name, so two
   * differently-named trowels each remember their own hotbar/inventory mode.
   */
  private static String modeKey(ItemStack stack) {
    return BuiltInRegistries.ITEM.getKey(stack.getItem())
        + "|" + stack.get(DataComponents.CUSTOM_NAME).getString();
  }

  /**
   * Initializes the mod on the client: loads the config, registers the "is_trowel" item
   * property used for the enchantment glint, binds the place key, and wires up the per-tick
   * handler that fires placement whenever the key is pressed.
   */
  @Override
  public void onInitializeClient() {
    TrowelConfig.getInstance();

    ItemProperties.register(
        TrowelConfig.getInstance().getTrowelItem(),
        ResourceLocation.parse("trowel-key:is_trowel"),
        (stack, world, entity, seed) -> TrowelUtil.isTrowel(stack) ? 1.0F : 0.0F);

    placeRandomKeyMapping = KeyBindingHelper.registerKeyBinding(
        new KeyMapping(KEY_PLACE_RANDOM, GLFW.GLFW_KEY_R, CATEGORY));

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (placeRandomKeyMapping.consumeClick()) {
            placeRandomBlock(client);
          }
        });
  }

  /**
   * Returns the placement mode stored for the given trowel, defaulting to
   * {@link TrowelMode#HOTBAR} for non-trowels or trowels that have not been configured yet.
   */
  public static TrowelMode getMode(ItemStack stack) {
    if (!TrowelUtil.isTrowel(stack)) return TrowelMode.HOTBAR;
    return MODE_MAP.getOrDefault(modeKey(stack), TrowelMode.HOTBAR);
  }

  /**
   * Records the placement mode for the given trowel. Non-trowels are ignored.
   */
  public static void setMode(ItemStack stack, TrowelMode mode) {
    if (!TrowelUtil.isTrowel(stack)) return;
    MODE_MAP.put(modeKey(stack), mode);
  }

  /**
   * Places a random block from the trowel's active inventory.
   *
   * <p>Detects which hand holds the trowel, resolves its mode, and delegates to the hotbar
   * or full-inventory routine. Does nothing if no trowel is held or the world/player is not
   * ready.
   */
  public static void placeRandomBlock(Minecraft client) {
    if (client.player == null || client.level == null) return;

    ItemStack mainHand = client.player.getMainHandItem();
    boolean inMainHand = TrowelUtil.isTrowel(mainHand);
    ItemStack offHand = client.player.getOffhandItem();
    boolean inOffHand = TrowelUtil.isTrowel(offHand);

    if (!inMainHand && !inOffHand) return;

    TrowelMode mode = inMainHand ? getMode(mainHand) : getMode(offHand);

    if (mode == TrowelMode.INVENTORY) {
      placeRandomBlockFromInventory(client);
    } else {
      placeRandomHotbarBlock(client);
    }
  }

  /**
   * Places a random block drawn from the entire player inventory (slots 0-35).
   */
  public static void placeRandomBlockFromInventory(Minecraft client) {
    if (client.player == null || client.level == null) return;
    placeRandomFromSlots(client, collectPlaceableSlots(client, 0, 36));
  }

  /**
   * Places a random block drawn only from the nine hotbar slots (0-8).
   */
  private static void placeRandomHotbarBlock(Minecraft client) {
    if (client.player == null || client.level == null) return;
    placeRandomFromSlots(client, collectPlaceableSlots(client, 0, 9));
  }

  /**
   * Collects the inventory slot indices in {@code [from, to)} that currently hold a
   * placeable block item.
   */
  private static List<Integer> collectPlaceableSlots(Minecraft client, int from, int to) {
    List<Integer> slots = new ArrayList<>();
    for (int i = from; i < to; i++) {
      ItemStack stack = client.player.getInventory().getItem(i);
      if (!stack.isEmpty() && stack.getItem() instanceof BlockItem) {
        slots.add(i);
      }
    }
    return slots;
  }

  /**
   * Core placement routine. Picks a random slot from {@code placeableSlots}, temporarily
   * makes it the selected hotbar slot (swapping it in from the inventory first if needed),
   * syncs that selection to the server, and simulates a right-click use on the block the
   * player is currently looking at. The player's original selection is always restored
   * afterwards.
   */
  private static void placeRandomFromSlots(
      Minecraft client, List<Integer> placeableSlots) {
    if (placeableSlots.isEmpty()) return;

    int randomSlot =
        placeableSlots.get(RANDOM.nextInt(placeableSlots.size()));
    int originalSlot = client.player.getInventory().selected;

    if (randomSlot >= 9) {
      swapWithHotbar(client, randomSlot, originalSlot);
    }

    client.player.getInventory().selected = randomSlot < 9 ? randomSlot : originalSlot;
    // the server reads the selected slot when placing, so sync it before we send the use packet
    client.getConnection().send(new ServerboundSetCarriedItemPacket(
        client.player.getInventory().selected));

    HitResult hitResult = client.hitResult;
    if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK
        && client.gameMode != null) {
      BlockHitResult blockHitResult = (BlockHitResult) hitResult;
      client.gameMode.useItemOn(
          client.player,
          client.player.getUsedItemHand(),
          blockHitResult);
    }

    if (randomSlot >= 9) {
      swapWithHotbar(client, randomSlot, originalSlot);
    }

    client.player.getInventory().selected = originalSlot;
  }

  /**
   * Swaps the item in {@code inventorySlot} with the hotbar slot {@code hotbarSlot} via a
   * SWAP inventory click, so the block ends up in a selectable hotbar slot.
   */
  private static void swapWithHotbar(Minecraft client, int inventorySlot, int hotbarSlot) {
    // SWAP the block into the player's hotbar slot so useItemOn can actually reach it
    client.gameMode.handleInventoryMouseClick(
        client.player.inventoryMenu.containerId,
        inventorySlot,
        hotbarSlot,
        ClickType.SWAP,
        client.player
    );
  }
}
