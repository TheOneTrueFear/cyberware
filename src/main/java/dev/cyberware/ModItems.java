package dev.cyberware;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CyberwareMod.MODID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CyberwareMod.MODID);

    public static final Map<Implant, DeferredItem<ImplantItem>> IMPLANT_ITEMS = new EnumMap<>(Implant.class);

    static {
        for (Implant imp : Implant.values()) {
            IMPLANT_ITEMS.put(imp, ITEMS.registerItem(imp.id, p -> new ImplantItem(p.stacksTo(1), imp)));
        }
    }

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.cyberware"))
                    .icon(() -> new ItemStack(IMPLANT_ITEMS.get(Implant.SANDEVISTAN).get()))
                    .displayItems((params, out) -> {
                        out.accept(ModBlocks.SURGERY_STATION_ITEM.get());
                        IMPLANT_ITEMS.values().forEach(i -> out.accept(i.get()));
                    })
                    .build());
}
