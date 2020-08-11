package svenhjol.charm.mixin;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.block.CrateBlock;
import svenhjol.meson.Meson;

@Mixin(ShulkerBoxTileEntity.class)
public class CratesShulkerTileMixin {
    @Inject(
        method = "canInsertItem",
        at = @At("HEAD"),
        cancellable = true
    )
    private void canInsertItemHook(int index, ItemStack stack, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (Meson.enabled("charm:crates") && Block.getBlockFromItem(stack.getItem()) instanceof CrateBlock)
            cir.setReturnValue(false);
    }
}
