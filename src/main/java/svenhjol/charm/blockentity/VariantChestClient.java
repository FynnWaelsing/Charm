package svenhjol.charm.blockentity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.Identifier;
import svenhjol.charm.Charm;
import svenhjol.charm.block.VariantChestBlock;
import svenhjol.charm.block.VariantTrappedChestBlock;
import svenhjol.meson.event.BlockItemRenderCallback;
import svenhjol.meson.event.TextureStitchCallback;
import svenhjol.charm.module.VariantChests;
import svenhjol.charm.render.VariantChestBlockEntityRenderer;
import svenhjol.meson.MesonModule;
import svenhjol.meson.enums.IVariantMaterial;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class VariantChestClient {
    private final VariantChestBlockEntity CACHED_NORMAL_CHEST = new VariantChestBlockEntity();
    private final VariantTrappedChestBlockEntity CACHED_TRAPPED_CHEST = new VariantTrappedChestBlockEntity();

    public VariantChestClient(MesonModule module) {
        BlockEntityRendererRegistry.INSTANCE.register(VariantChests.NORMAL_BLOCK_ENTITY, VariantChestBlockEntityRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(VariantChests.TRAPPED_BLOCK_ENTITY, VariantChestBlockEntityRenderer::new);

        TextureStitchCallback.EVENT.register(((atlas, textures) -> {
            if (atlas.getId().toString().equals("minecraft:textures/atlas/chest.png")) {
                VariantChests.NORMAL_CHEST_BLOCKS.keySet().forEach(type -> {
                    addChestTexture(textures, type, ChestType.LEFT);
                    addChestTexture(textures, type, ChestType.RIGHT);
                    addChestTexture(textures, type, ChestType.SINGLE);
                });
            }
        }));

        BlockItemRenderCallback.EVENT.register(block -> {
            if (block instanceof VariantChestBlock) {
                VariantChestBlock chest = (VariantChestBlock)block;
                CACHED_NORMAL_CHEST.setMaterialType(chest.getMaterialType());
                return CACHED_NORMAL_CHEST;

            } else if (block instanceof VariantTrappedChestBlock) {
                VariantTrappedChestBlock chest = (VariantTrappedChestBlock)block;
                CACHED_TRAPPED_CHEST.setMaterialType(chest.getMaterialType());
                return CACHED_TRAPPED_CHEST;
            }

            return null;
        });
    }


    private void addChestTexture(Set<Identifier> textures, IVariantMaterial variant, ChestType chestType) {
        String chestTypeName = chestType != ChestType.SINGLE ? "_" + chestType.asString().toLowerCase() : "";
        String[] bases = {"trapped", "normal"};

        for (String base : bases) {
            Identifier id = new Identifier(Charm.MOD_ID, "entity/chest/" + variant.asString() + "_" + base + chestTypeName);
            VariantChestBlockEntityRenderer.addTexture(variant, chestType, id, base.equals("trapped"));
            textures.add(id);
        }
    }
}
