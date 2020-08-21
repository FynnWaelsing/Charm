package svenhjol.charm;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import svenhjol.charm.base.CharmMessages;
import svenhjol.charm.base.CharmSounds;
import svenhjol.charm.handler.InventoryButtonHandler;
import svenhjol.charm.module.*;
import svenhjol.meson.MesonMod;
import svenhjol.meson.MesonModule;

import java.util.Arrays;
import java.util.List;

@Mod(Charm.MOD_ID)
public class Charm extends MesonMod {
    public static final String MOD_ID = "charm";

    public Charm() {
        super(MOD_ID);

        CharmMessages.init(this);
        CharmSounds.init(this);
    }

    @Override
    public void onClientSetup(FMLClientSetupEvent event) {
        super.onClientSetup(event);
        MinecraftForge.EVENT_BUS.register(new InventoryButtonHandler());
    }

    protected List<Class<? extends MesonModule>> getModules() {
        return Arrays.asList(
            AnvilImprovements.class,
            ArmorInvisibility.class,
            AutomaticRecipeUnlock.class,
            BatBucket.class,
            BeaconsHealMobs.class,
            BlockOfEnderPearls.class,
            BlockOfGunpowder.class,
            BlockOfSugar.class,
            Bookcases.class,
            CampfiresNoDamage.class,
            Candles.class,
            CaveSpidersDropCobwebs.class,
            ChickensDropFeathers.class,
            Core.class,
            Crates.class,
            DecreaseRepairCost.class,
            DirtToPath.class,
            EndermitePowder.class,
            ExtendNetheriteLifetime.class,
            ExtractEnchantments.class,
            FeatherFallingCrops.class,
            GoldBars.class,
            GoldChains.class,
            GoldLanterns.class,
            HoeHarvesting.class,
            HuskImprovements.class,
            InventoryCrafting.class,
            InventoryEnderChest.class,
            InventorySorting.class,
            Quark.class,
            LanternsObeyGravity.class,
            MineshaftImprovements.class,
            MoreVillageBiomes.class,
            MoreVillagerTrades.class,
            MusicImprovements.class,
            NetheriteNuggets.class,
            ParrotsStayOnShoulder.class,
            PathToDirt.class,
            PlayerState.class,
            RedstoneLanterns.class,
            RedstoneSand.class,
            RemoveNitwits.class,
            RemovePotionGlint.class,
            SleepImprovements.class,
            StackableEnchantedBooks.class,
            StackablePotions.class,
            StrayImprovements.class,
            TamedAnimalsNoDamage.class,
            UseTotemFromInventory.class,
            VariantAnimalTextures.class,
            VariantBarrels.class,
            VariantBookshelves.class,
            VariantChests.class,
            VillagersFollowEmeralds.class,
            WanderingTraderImprovements.class,
            WitchesDropLuck.class
        );
    }
}
