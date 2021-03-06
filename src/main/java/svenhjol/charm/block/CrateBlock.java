package svenhjol.charm.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import svenhjol.charm.blockentity.CrateBlockEntity;
import svenhjol.charm.module.Crates;
import svenhjol.meson.MesonModule;
import svenhjol.meson.block.MesonBlockWithEntity;
import svenhjol.meson.enums.IVariantMaterial;

import javax.annotation.Nullable;
import java.util.List;

public class CrateBlock extends MesonBlockWithEntity {
    private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    private static final Identifier CONTENTS = new Identifier("contents");
    private IVariantMaterial type;

    public CrateBlock(MesonModule module, IVariantMaterial type) {
        super(module, type.asString() + "_crate", AbstractBlock.Settings
            .of(Material.WOOD)
            .sounds(BlockSoundGroup.WOOD)
            .strength(1.5F));

        this.type = type;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        CrateBlockEntity crate = new CrateBlockEntity();
        crate.setCustomName(new TranslatableText("block." + this.module.mod.getId() + "." + type.asString() + "_crate"));
        return crate;
    }

    @Override
    public ItemGroup getItemGroup() {
        return ItemGroup.DECORATIONS;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (itemStack.hasCustomName()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof CrateBlockEntity) {
                ((CrateBlockEntity)blockEntity).setCustomName(itemStack.getName());
            }
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrateBlockEntity) {
            CrateBlockEntity crate = (CrateBlockEntity)blockEntity;

            if (!world.isClient && player.isCreative() && !crate.isEmpty()) {
                ItemStack stack = new ItemStack(getBlockByMaterial(this.type));
                CompoundTag tag = crate.toTag(new CompoundTag());

                if (!tag.isEmpty())
                    stack.putSubTag(BLOCK_ENTITY_TAG, tag);

                if (crate.hasCustomName())
                    stack.setCustomName(crate.getCustomName());

                ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
                entity.setToDefaultPickupDelay();
                world.spawnEntity(entity);
            } else {
                crate.checkLootInteraction(player);
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity = builder.get(LootContextParameters.BLOCK_ENTITY);
        if (blockEntity instanceof CrateBlockEntity) {
            CrateBlockEntity crate = (CrateBlockEntity)blockEntity;

            builder = builder.putDrop(CONTENTS, ((context, consumer) -> {
                for (int i = 0; i < crate.size(); i++) {
                    consumer.accept(crate.getStack(i));
                }
            }));
        }
        return super.getDroppedStacks(state, builder);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && !player.isSpectator()) {

            // original implementation with loot check
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrateBlockEntity) {
                CrateBlockEntity crate = (CrateBlockEntity)blockEntity;
                crate.checkLootInteraction(player);
                player.openHandledScreen(crate);
            }

            // fabric default implementation
            if (false) {
                NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);

                if (screenHandlerFactory != null) {
                    player.openHandledScreen(screenHandlerFactory);
                }
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof CrateBlockEntity)
                world.updateComparators(pos, state.getBlock());

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public PistonBehavior getPistonBehavior(BlockState state) {
        return PistonBehavior.NORMAL;
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        CrateBlockEntity crate = (CrateBlockEntity)world.getBlockEntity(pos);

        if (crate == null)
            return ItemStack.EMPTY;

        CompoundTag tag = crate.toTag(new CompoundTag());
        if (!tag.isEmpty())
            stack.putSubTag(BLOCK_ENTITY_TAG, tag);

        return stack;
    }

    private static Block getBlockByMaterial(IVariantMaterial type) {
        return Crates.CRATE_BLOCKS.get(type);
    }
}
