package svenhjol.charm.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import svenhjol.meson.event.PlaySoundCallback;
import svenhjol.charm.module.MusicImprovements;
import svenhjol.meson.Meson;
import svenhjol.meson.MesonModule;
import svenhjol.meson.helper.DimensionHelper;
import svenhjol.meson.helper.SoundHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused")
public class MusicClient {
    private final MesonModule module;
    private SoundInstance musicToStop = null;
    private int ticksBeforeStop = 0;
    private static SoundInstance currentMusic;
    private static Identifier currentDim = null;
    private static int timeUntilNextMusic = 100;
    private static final List<MusicCondition> musicConditions = new ArrayList<>();
    public static boolean enabled;

    public MusicClient(MesonModule module) {
        this.module = module;

        // set statically so hooks can check this is enabled
        enabled = module.enabled;

        if (MusicImprovements.playCreativeMusic)
            addCreativeMusicCondition();

        UseBlockCallback.EVENT.register(((player, world, hand, hitResult) -> {
            stopRecord(player, hitResult.getBlockPos(), player.getStackInHand(hand));
            return ActionResult.PASS;
        }));

        PlaySoundCallback.EVENT.register(((soundSystem, sound) -> {
            checkShouldStopMusic(sound);
        }));

        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            checkActuallyStopMusic();
        }));
    }

    public void addCreativeMusicCondition() {
        musicConditions.add(new MusicCondition(
            SoundEvents.MUSIC_CREATIVE, 1200, 3600, mc -> mc.player != null
                && (!mc.player.isCreative() || !mc.player.isSpectator())
                && DimensionHelper.isDimension(mc.player.world, new Identifier("overworld"))
                && new Random().nextFloat() < 0.25F
        ));
    }

    public void stopRecord(Entity entity, BlockPos pos, ItemStack stack) {
        if (entity.world.isClient
            && entity instanceof PlayerEntity
            && stack.getItem() instanceof MusicDiscItem
        ) {
            BlockState state = entity.world.getBlockState(pos);
            if (state.getBlock() == Blocks.JUKEBOX && !state.get(JukeboxBlock.HAS_RECORD))
                SoundHelper.getSoundManager().stopSounds(null, SoundCategory.MUSIC);
        }
    }

    public void checkShouldStopMusic(SoundInstance sound) {
        if (sound.getCategory() == SoundCategory.MUSIC) {
            // check if there are any records playing
            SoundHelper.getPlayingSounds().forEach((category, s) -> {
                if (category == SoundCategory.RECORDS) {
                    musicToStop = sound;
                    Meson.LOG.debug("Triggered background music while record playing");
                }
            });
        }
    }

    public void checkActuallyStopMusic() {
        if (musicToStop != null
            && ++ticksBeforeStop % 10 == 0
        ) {
            SoundHelper.getSoundManager().stop(musicToStop);
            ticksBeforeStop = 0;
            musicToStop = null;
        }
    }

    public static boolean handleTick(SoundInstance current) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null) return false;
        MusicCondition ambient = getMusicCondition();

        if (currentMusic != null) {
            if (!DimensionHelper.isDimension(mc.world, currentDim))
                forceStop();

            if (!mc.getSoundManager().isPlaying(currentMusic)) {
                currentMusic = null;
                timeUntilNextMusic = Math.min(MathHelper.nextInt(new Random(), ambient.getMinDelay(), 3600), timeUntilNextMusic);
            }
        }

        timeUntilNextMusic = Math.min(timeUntilNextMusic, ambient.getMaxDelay());

        if (currentMusic == null && timeUntilNextMusic-- <= 0) {
            currentDim = DimensionHelper.getDimension(mc.world);
            currentMusic = PositionedSoundInstance.music(ambient.getSound());

            if (currentMusic.getSound() != SoundManager.MISSING_SOUND) {
                mc.getSoundManager().play(currentMusic);
                timeUntilNextMusic = Integer.MAX_VALUE;
            }
        }

        return true;
    }

    public static boolean handleStop() {
        if (currentMusic != null) {
            MinecraftClient.getInstance().getSoundManager().stop(currentMusic);
            currentMusic = null;
            timeUntilNextMusic = 0;
        }
        return true;
    }

    public static boolean handlePlaying(MusicSound music) {
        return currentMusic != null && music.getSound().getId().equals(currentMusic.getId());
    }

    public static void forceStop() {
        MinecraftClient.getInstance().getSoundManager().stop(currentMusic);
        currentMusic = null;
        timeUntilNextMusic = 3600;
    }

    public static MusicCondition getMusicCondition() {
        MusicCondition condition = null;

        // select an available condition from the pool of conditions
        for (MusicCondition c : musicConditions) {
            if (c.handle()) {
                condition = c;
                break;
            }
        }

        // if none available, just play a default background track
        if (condition == null)
            condition = new MusicCondition(MinecraftClient.getInstance().getMusicType());

        return condition;
    }

    public static List<MusicCondition> getMusicConditions() {
        return musicConditions;
    }
}
