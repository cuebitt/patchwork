package cuebitt.patchwork.client.mixin;

import cuebitt.patchwork.client.TrowelUtil;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cancels the shovel's block-use (e.g. pathing dirt) when the shovel in hand is a trowel, so
 * right-clicking with a trowel never triggers the vanilla shovel behaviour.
 */
@Mixin(ShovelItem.class)
public class MixinShovelItem {

  @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
  private void onUseOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> ci) {
    ItemStack stack = context.getItemInHand();
    if (TrowelUtil.isTrowel(stack)) {
      ci.setReturnValue(InteractionResult.PASS);
    }
  }
}
