package cuebitt.trowelkey.client.mixin;

import cuebitt.trowelkey.client.TrowelConfig;
import cuebitt.trowelkey.client.TrowelKeyClient;
import cuebitt.trowelkey.client.TrowelMode;
import cuebitt.trowelkey.client.TrowelUtil;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Forces the enchantment glint on a trowel while it is in inventory mode, giving the player
 * a visual cue for which mode is active. Other items are left untouched.
 */
@Mixin(ItemStack.class)
public class MixinItemStack_HasFoil {

    @Inject(method = "hasFoil", at = @At("RETURN"), cancellable = true)
    private void onHasFoil(CallbackInfoReturnable<Boolean> ci) {
        if (!ci.getReturnValue()
                && TrowelUtil.isTrowel((ItemStack) (Object) this)
                && TrowelConfig.getInstance().isShowEnchantGlint()
                && TrowelKeyClient.getMode((ItemStack) (Object) this) == TrowelMode.INVENTORY) {
            ci.setReturnValue(true);
        }
    }
}
