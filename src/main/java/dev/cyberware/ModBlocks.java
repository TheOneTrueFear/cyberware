package dev.cyberware;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CyberwareMod.MODID);

    public static final DeferredBlock<Block> SURGERY_STATION = BLOCKS.registerBlock("surgery_station",
            SurgeryStationBlock::new,
            BlockBehaviour.Properties.of().strength(3.5f).sound(SoundType.METAL).requiresCorrectToolForDrops());

    public static final DeferredItem<BlockItem> SURGERY_STATION_ITEM =
            ModItems.ITEMS.registerSimpleBlockItem("surgery_station", SURGERY_STATION);
}
