package cuebitt.trowelkey.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * ModMenu entry point that supplies the mod's config screen.
 */
public class ModMenuIntegration implements ModMenuApi {

    /**
     * Returns a factory that opens {@link TrowelConfigScreen} when the user clicks the
     * config button in ModMenu.
     */
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return TrowelConfigScreen::createScreen;
    }
}
