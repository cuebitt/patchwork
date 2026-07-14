package cuebitt.trowelkey.client.mixin;

import cuebitt.trowelkey.client.TrowelKeyClient;
import cuebitt.trowelkey.client.TrowelMode;
import cuebitt.trowelkey.client.TrowelUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Redirects a right-click while a trowel is held so it places a random block instead of
 * performing the shovel's normal use action.
 *
 * <p>Shift + Right Click toggles the trowel between hotbar and inventory mode; a plain
 * right-click delegates to {@link TrowelKeyClient#placeRandomBlock(Minecraft)}.
 */
@Mixin(Minecraft.class)
public class MixinMinecraft_StartUse {

    @Shadow
    private int rightClickDelay;

    @Inject(method = "startUseItem", at = @At("HEAD"), cancellable = true)
    private void onStartUse(CallbackInfo ci) {
        Minecraft client = (Minecraft) (Object) this;
        if (client.player == null) return;

        boolean hasTrowel = TrowelUtil.isTrowel(client.player.getMainHandItem())
                || TrowelUtil.isTrowel(client.player.getOffhandItem());

        if (!hasTrowel) return;

        this.rightClickDelay = 4;
        ci.cancel();

        if (client.options.keyShift.isDown()) {
            ItemStack trowel = TrowelUtil.isTrowel(client.player.getMainHandItem())
                    ? client.player.getMainHandItem()
                    : client.player.getOffhandItem();
            TrowelMode currentMode = TrowelKeyClient.getMode(trowel);
            TrowelMode newMode = currentMode == TrowelMode.HOTBAR ? TrowelMode.INVENTORY : TrowelMode.HOTBAR;
            TrowelKeyClient.setMode(trowel, newMode);

            String translationKey = newMode == TrowelMode.HOTBAR
                    ? "action.trowel-key.mode.hotbar"
                    : "action.trowel-key.mode.inventory";
            client.player.displayClientMessage(Component.translatable(translationKey), true);
            client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
            return;
        }

        TrowelKeyClient.placeRandomBlock(client);
    }
}
