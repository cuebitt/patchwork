package cuebitt.patchwork.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * Persisted mod configuration, loaded from {@code config/patchwork/config.json}.
 *
 * <p>Holds the item id treated as a trowel, the custom name that marks a stack as a trowel, and
 * whether the enchantment glint is shown on trowels. It also holds the hotkey settings (enabled
 * flag, mode, whether a trowel must be held, and a resettable per-trowel default mode) and the
 * block filter used to include or exclude blocks from random placement. A single shared instance is
 * created lazily and saved on change.
 */
public class TrowelConfig {
  private static final Path CONFIG_PATH = Paths.get("config", "patchwork", "config.json");
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private static TrowelConfig INSTANCE;

  private String trowel_item = "minecraft:iron_shovel";
  private String trowel_name = "Trowel";
  private boolean show_enchant_glint = true;

  private boolean hotkey_enabled = true;
  private TrowelMode hotkey_mode = TrowelMode.HOTBAR;
  private boolean hotkey_requires_trowel = true;
  private TrowelMode default_mode = TrowelMode.HOTBAR;
  private BlockFilterMode block_filter_mode = BlockFilterMode.NONE;
  private List<String> block_filter = new ArrayList<>();

  /** Returns the shared configuration instance, loading it from disk on first use. */
  public static TrowelConfig getInstance() {
    if (INSTANCE == null) {
      INSTANCE = load();
    }
    return INSTANCE;
  }

  /** Returns the configured trowel item. */
  public Item getTrowelItem() {
    return BuiltInRegistries.ITEM.get(ResourceLocation.parse(trowel_item));
  }

  /** Returns the configured trowel item id as a string (mainly for display/saving). */
  public String getTrowelItemId() {
    return trowel_item;
  }

  /** Updates the trowel item from a resolved {@link Item}. */
  public void setTrowelItem(Item item) {
    ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
    if (key != null) {
      this.trowel_item = key.toString();
    }
  }

  /** Returns the custom name that marks a stack as a trowel. */
  public String getTrowelName() {
    return trowel_name;
  }

  /** Sets the custom name that marks a stack as a trowel. */
  public void setTrowelName(String name) {
    this.trowel_name = name;
  }

  /** Returns whether the enchantment glint is shown on trowels in inventory mode. */
  public boolean isShowEnchantGlint() {
    return show_enchant_glint;
  }

  /** Toggles the enchantment glint shown on trowels in inventory mode. */
  public void setShowEnchantGlint(boolean show) {
    this.show_enchant_glint = show;
  }

  /** Returns whether the place-random keybinding (hotkey) is enabled. */
  public boolean isHotkeyEnabled() {
    return hotkey_enabled;
  }

  /** Enables or disables the place-random keybinding (hotkey). */
  public void setHotkeyEnabled(boolean enabled) {
    this.hotkey_enabled = enabled;
  }

  /** Returns the mode (hotbar or inventory) used when placing via the hotkey. */
  public TrowelMode getHotkeyMode() {
    return hotkey_mode;
  }

  /** Sets the mode (hotbar or inventory) used when placing via the hotkey. */
  public void setHotkeyMode(TrowelMode mode) {
    this.hotkey_mode = mode;
  }

  /** Returns whether a trowel must be held for the hotkey to place blocks. */
  public boolean isHotkeyRequiresTrowel() {
    return hotkey_requires_trowel;
  }

  /** Sets whether a trowel must be held for the hotkey to place blocks. */
  public void setHotkeyRequiresTrowel(boolean requires) {
    this.hotkey_requires_trowel = requires;
  }

  /**
   * Returns the default placement mode assigned to a trowel before it has been toggled with Shift +
   * Right Click.
   */
  public TrowelMode getDefaultMode() {
    return default_mode;
  }

  /** Sets the default placement mode assigned to a trowel before it has been toggled. */
  public void setDefaultMode(TrowelMode mode) {
    this.default_mode = mode;
  }

  /** Returns how the block filter is applied to random placement. */
  public BlockFilterMode getBlockFilterMode() {
    return block_filter_mode;
  }

  /** Sets how the block filter is applied to random placement. */
  public void setBlockFilterMode(BlockFilterMode mode) {
    this.block_filter_mode = mode;
  }

  /** Returns the list of block item ids used by the block filter. */
  public List<String> getBlockFilter() {
    return block_filter;
  }

  /** Sets the list of block item ids used by the block filter. */
  public void setBlockFilter(List<String> filter) {
    this.block_filter = filter;
  }

  /**
   * Loads the config from disk, falling back to defaults (and writing them out) when no valid file
   * exists or it cannot be read.
   */
  private static TrowelConfig load() {
    if (Files.exists(CONFIG_PATH)) {
      try {
        String json = Files.readString(CONFIG_PATH);
        TrowelConfig config = GSON.fromJson(json, TrowelConfig.class);
        if (config != null) return config;
      } catch (IOException e) {
        // fall through to default
      }
    }
    TrowelConfig config = new TrowelConfig();
    config.save();
    return config;
  }

  /**
   * Writes the current config to disk. I/O failures are swallowed so a broken config directory
   * never crashes the game.
   */
  public void save() {
    try {
      Files.createDirectories(CONFIG_PATH.getParent());
      Files.writeString(CONFIG_PATH, GSON.toJson(this));
    } catch (IOException e) {
      // ignore
    }
  }
}
