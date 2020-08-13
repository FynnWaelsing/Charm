package svenhjol.charm.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import svenhjol.meson.MesonModule;
import svenhjol.meson.item.MesonItem;

public class BeeswaxItem extends MesonItem {
    public BeeswaxItem(MesonModule module) {
        super(module, "beeswax", new Item.Properties().group(ItemGroup.MATERIALS));
    }

    @Override
    public int getBurnTime(ItemStack itemStack) {
        return 800;
    }
}
