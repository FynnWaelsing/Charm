package svenhjol.meson.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.meson.event.AddEntityCallback;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(
        method = "addEntity",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/world/ServerWorld.getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/Chunk;"
        )
    )
    private void hookAddEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        AddEntityCallback.EVENT.invoker().interact(entity);
    }

    @Inject(
        method = "loadEntity",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/world/ServerWorld.loadEntityUnchecked(Lnet/minecraft/entity/Entity;)V"
        )
    )
    private void hookLoadEntity(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        AddEntityCallback.EVENT.invoker().interact(entity);
    }

    /**
     * Calls our AddEntityCallback event when a player
     * joins the game on the server side.
     *
     * Inspired by Forge's EntityJoinWorldEvent which
     * provides a hook at the same point in the code,
     * except allows it to be cancellable.
     */
    @Inject(
        method = "addPlayer",
        at = @At("HEAD")
    )
    private void hookAddPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        AddEntityCallback.EVENT.invoker().interact(player);
    }
}
