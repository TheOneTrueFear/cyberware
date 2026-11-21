package dev.cyberware;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ImplantItem extends Item {
    private final Implant implant;

    public ImplantItem(Properties props, Implant implant) {
        super(props);
        this.implant = implant;
    }

    public Implant implant() { return implant; }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.cyberware.slot." + implant.slot.name().toLowerCase()));
        tooltip.add(Component.translatable("tooltip.cyberware." + implant.id));
        tooltip.add(Component.translatable("tooltip.cyberware.humanity", (int) (implant.humanityCost / 2)));
        tooltip.add(Component.translatable("tooltip.cyberware.install"));
    }
}
