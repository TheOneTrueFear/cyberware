package dev.cyberware;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(CyberwareMod.MODID)
public class CyberwareMod {
    public static final String MODID = "cyberware";

    public CyberwareMod(IEventBus modBus) {
        ModItems.ITEMS.register(modBus);
        ModItems.TABS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModAttachments.ATTACHMENTS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        modBus.addListener(CyberwareMod::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar("1");
        reg.playToServer(DashPayload.TYPE, DashPayload.CODEC, CyberwareMod::handleDash);
    }

    /** Sandevistan: speed + haste burst and a forward dash, 20s cooldown. */
    private static void handleDash(DashPayload payload, IPayloadContext ctx) {
        if (!(ctx.player() instanceof ServerPlayer p)) return;
        if (!Cyber.has(p, Implant.SANDEVISTAN)) return;

        Item cooldownItem = ModItems.IMPLANT_ITEMS.get(Implant.SANDEVISTAN).get();
        if (p.getCooldowns().isOnCooldown(cooldownItem)) {
            p.displayClientMessage(Component.translatable("message.cyberware.cooldown"), true);
            return;
        }
        p.getCooldowns().addCooldown(cooldownItem, 400);

        p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120, 3, true, false));
        p.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 120, 2, true, false));
        p.addEffect(new MobEffectInstance(ModEffects.OVERCLOCK, 120, 0, false, false, true));

        Vec3 look = p.getLookAngle();
        p.setDeltaMovement(look.x * 1.6, Math.max(look.y * 1.6, 0.25), look.z * 1.6);
        p.hurtMarked = true; // forces the velocity to sync to the client
        p.fallDistance = 0;
        p.level().playSound(null, p.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1f, 2f);
    }

    @EventBusSubscriber(modid = MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void onPlayerTick(PlayerTickEvent.Post event) {
            if (event.getEntity() instanceof ServerPlayer p && p.tickCount % 20 == 0) {
                Cyber.sync(p);
            }
        }
    }
}
