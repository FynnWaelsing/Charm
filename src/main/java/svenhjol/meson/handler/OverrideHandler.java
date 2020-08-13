package svenhjol.meson.handler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.PointOfInterestType;
import svenhjol.charm.mixin.accessor.PointOfInterestTypeAccessor;
import svenhjol.meson.MesonMod;
import svenhjol.charm.mixin.accessor.BlockAccessor;
import svenhjol.charm.mixin.accessor.ItemAccessor;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Inspired by Quark.
 * This needs the accesstransformer.cfg set up to enable public translationKey in Item and Block.
 * @link {ItemOverrideHandler.java}
 */
@SuppressWarnings({"unused", "deprecation"})
public class OverrideHandler {
    private static final Map<Item, String> defaultItemKeys = new HashMap<>();
    private static final Map<Block, String> defaultBlockKeys = new HashMap<>();

    public static void changeItemTranslationKey(Item item, @Nullable String newKey) {
        if (!defaultItemKeys.containsKey(item)) {
            // record the default before trying to set it
            defaultItemKeys.put(item, item.getTranslationKey());
        }

        if (newKey == null)
            newKey = defaultItemKeys.get(item);

        ((ItemAccessor)item).setTranslationKey(newKey);
    }

    public static void changeBlockTranslationKey(Block block, @Nullable String newKey) {
        if (!defaultBlockKeys.containsKey(block)) {
            // record the default before trying to set it
            defaultBlockKeys.put(block, block.getTranslationKey());
        }

        if (newKey == null)
            newKey = defaultBlockKeys.get(block);

        ((BlockAccessor)block).setTranslationKey(newKey);
    }

    public static void changeVanillaBlock(MesonMod mod, Block block, ResourceLocation newRes) {
        Registry.register(Registry.BLOCK, newRes, block);
    }

    public static void changeVanillaItem(MesonMod mod, Item item, ResourceLocation newRes) {
        Registry.register(Registry.ITEM, newRes, item);
    }

    public static void changeVanillaPointOfInterestType(MesonMod mod, PointOfInterestType type, ResourceLocation newRes) {
        Registry.register(Registry.POINT_OF_INTEREST_TYPE, newRes, type);

        Set<BlockState> blockStates = ((PointOfInterestTypeAccessor) type).getBlockStates();
        PointOfInterestTypeAccessor.getBlocksOfInterest().addAll(blockStates);
        blockStates.forEach(blockState -> PointOfInterestTypeAccessor.getPoitByBlockState().put(blockState, type));
    }
}