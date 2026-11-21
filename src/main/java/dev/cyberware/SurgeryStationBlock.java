package dev.cyberware;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class SurgeryStationBlock extends Block {
    public SurgeryStationBlock(Properties props) { super(props); }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof ImplantItem item) {
            if (!level.isClientSide) {
                Cyber.install(player, item.implant());
                if (!player.getAbilities().instabuild) stack.shrink(1);
                level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6f, 1.6f);
                player.displayClientMessage(Component.translatable("message.cyberware.installed",
                        item.getDescription()), true);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        List<String> inst = Cyber.installed(player);
        if (player.isShiftKeyDown()) {
            Cyber.removeAll(player);
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.6f, 0.8f);
            player.displayClientMessage(Component.translatable("message.cyberware.removed_all"), true);
        } else if (inst.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.cyberware.none"), true);
        } else {
            for (String id : inst) {
                Implant imp = Implant.byId(id);
                if (imp != null) player.sendSystemMessage(Component.translatable("message.cyberware.entry",
                        ModItems.IMPLANT_ITEMS.get(imp).get().getDescription(),
                        Component.translatable("tooltip.cyberware.slot." + imp.slot.name().toLowerCase())));
            }
        }
        return InteractionResult.CONSUME;
    }
}
