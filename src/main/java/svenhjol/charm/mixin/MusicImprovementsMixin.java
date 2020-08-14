package svenhjol.charm.mixin;

import net.minecraft.client.audio.BackgroundMusicSelector;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.MusicTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import svenhjol.charm.client.MusicClient;

import javax.annotation.Nullable;

@Mixin(MusicTicker.class)
public class MusicImprovementsMixin {
    @Shadow @Nullable private ISound currentMusic;

    @Inject(
        method = "tick",
        at = @At("HEAD"),
        cancellable = true
    )
    private void tickHook(CallbackInfo ci) {
        if (MusicClient.enabled && MusicClient.handleTick(this.currentMusic))
            ci.cancel();
    }

    @Inject(
        method = "stop",
        at = @At("HEAD"),
        cancellable = true
    )
    private void stopHook(CallbackInfo ci) {
        if (MusicClient.enabled && MusicClient.handleStop())
            ci.cancel();
    }

    @Inject(
        method = "isBackgroundMusicPlaying",
        at = @At("HEAD"),
        cancellable = true
    )
    private void isPlayingHook(BackgroundMusicSelector music, CallbackInfoReturnable<Boolean> cir) {
        if (MusicClient.enabled && MusicClient.handlePlaying(music))
            cir.setReturnValue(true);
    }
}
