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

  // field names match the json keys on purpose, so keep them snake_case
  private String trowel_item = "minecraft:iron_shovel";
  private String trowel_name = "Trowel";
  private boolean show_enchant_glint = true;

  private boolean hotkey_enabled = true;
  private TrowelMode hotkey_mode = TrowelMode.HOTBAR;
  private boolean hotkey_requires_trowel = true;
  private TrowelMode default_mode = TrowelMode.HOTBAR;
  private BlockFilterMode block_filter_mode = BlockFilterMode.NONE;
  private List<String> block_filter = new ArrayList<>();

  public static TrowelConfig getInstance() {
    if (INSTANCE == null) {
      INSTANCE = load();
    }
    return INSTANCE;
  }

  public Item getTrowelItem() {
    return BuiltInRegistries.ITEM.get(ResourceLocation.parse(trowel_item));
  }

  public String getTrowelItemId() {
    return trowel_item;
  }

  public void setTrowelItem(Item item) {
    ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
    if (key != null) {
      this.trowel_item = key.toString();
    }
  }

  public String getTrowelName() {
    return trowel_name;
  }

  public void setTrowelName(String name) {
    this.trowel_name = name;
  }

  public boolean isShowEnchantGlint() {
    return show_enchant_glint;
  }

  public void setShowEnchantGlint(boolean show) {
    this.show_enchant_glint = show;
  }

  public boolean isHotkeyEnabled() {
    return hotkey_enabled;
  }

  public void setHotkeyEnabled(boolean enabled) {
    this.hotkey_enabled = enabled;
  }

  public TrowelMode getHotkeyMode() {
    return hotkey_mode;
  }

  public void setHotkeyMode(TrowelMode mode) {
    this.hotkey_mode = mode;
  }

  public boolean isHotkeyRequiresTrowel() {
    return hotkey_requires_trowel;
  }

  public void setHotkeyRequiresTrowel(boolean requires) {
    this.hotkey_requires_trowel = requires;
  }

  // the mode a fresh trowel starts in, before the player toggles it with Shift + Right Click
  public TrowelMode getDefaultMode() {
    return default_mode;
  }

  public void setDefaultMode(TrowelMode mode) {
    this.default_mode = mode;
  }

  public BlockFilterMode getBlockFilterMode() {
    return block_filter_mode;
  }

  public void setBlockFilterMode(BlockFilterMode mode) {
    this.block_filter_mode = mode;
  }

  public List<String> getBlockFilter() {
    return block_filter;
  }

  public void setBlockFilter(List<String> filter) {
    this.block_filter = filter;
  }

  // if the file is missing or unreadable we just fall back to defaults (and write them out)
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

  public void save() {
    try {
      Files.createDirectories(CONFIG_PATH.getParent());
      Files.writeString(CONFIG_PATH, GSON.toJson(this));
    } catch (IOException e) {
      // a broken config dir shouldn't be able to crash the game
    }
  }
}
