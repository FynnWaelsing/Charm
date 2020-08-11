package svenhjol.charm.mixin;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ShulkerBoxSlot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.block.CrateBlock;
import svenhjol.meson.Meson;

@Mixin(ShulkerBoxSlot.class)
public class CratesShulkerSlotMixin {
    @Inject(
        method = "isItemValid",
        at = @At("HEAD"),
        cancellable = true
    )
    private void isItemValidHook(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (Meson.enabled("charm:crates") && Block.getBlockFromItem(stack.getItem()) instanceof CrateBlock)
            cir.setReturnValue(false);
    }
}
