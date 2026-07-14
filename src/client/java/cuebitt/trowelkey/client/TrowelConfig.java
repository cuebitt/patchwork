package cuebitt.trowelkey.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Persisted mod configuration, loaded from {@code config/trowel-key/config.json}.
 *
 * <p>Holds the item id treated as a trowel, the custom name that marks a stack as a trowel,
 * and whether the enchantment glint is shown on trowels in inventory mode. A single shared
 * instance is created lazily and saved on change.
 */
public class TrowelConfig {
    private static final Path CONFIG_PATH = Paths.get("config", "trowel-key", "config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static TrowelConfig INSTANCE;

    private String trowel_item = "minecraft:iron_shovel";
    private String trowel_name = "Trowel";
    private boolean show_enchant_glint = true;

    /**
     * Returns the shared configuration instance, loading it from disk on first use.
     */
    public static TrowelConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    /**
     * Returns the configured trowel item.
     */
    public Item getTrowelItem() {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(trowel_item));
    }

    /**
     * Returns the configured trowel item id as a string (mainly for display/saving).
     */
    public String getTrowelItemId() {
        return trowel_item;
    }

    /**
     * Updates the trowel item from a resolved {@link Item}.
     */
    public void setTrowelItem(Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        if (key != null) {
            this.trowel_item = key.toString();
        }
    }

    /**
     * Returns the custom name that marks a stack as a trowel.
     */
    public String getTrowelName() {
        return trowel_name;
    }

    /**
     * Sets the custom name that marks a stack as a trowel.
     */
    public void setTrowelName(String name) {
        this.trowel_name = name;
    }

    /**
     * Returns whether the enchantment glint is shown on trowels in inventory mode.
     */
    public boolean isShowEnchantGlint() {
        return show_enchant_glint;
    }

    /**
     * Toggles the enchantment glint shown on trowels in inventory mode.
     */
    public void setShowEnchantGlint(boolean show) {
        this.show_enchant_glint = show;
    }

    /**
     * Loads the config from disk, falling back to defaults (and writing them out) when no
     * valid file exists or it cannot be read.
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
     * Writes the current config to disk. I/O failures are swallowed so a broken config
     * directory never crashes the game.
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
