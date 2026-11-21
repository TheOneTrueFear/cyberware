package dev.cyberware;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Everything you SEE when the Sandevistan is on:
 * afterimages (ghost copies of the player), sparks, a screen glow, a wider FOV, and a cooldown bar.
 */
public class SandevistanFx {
    private static final int LIFE = 14; // how many ticks a ghost lasts
    private static final ResourceLocation WHITE =
            ResourceLocation.fromNamespaceAndPath(CyberwareMod.MODID, "textures/misc/white.png");
    private static final RenderType GHOST_TYPE = RenderType.entityTranslucent(WHITE);

    private static final List<Ghost> GHOSTS = new ArrayList<>();

    private static class Ghost {
        final Player player; final double x, y, z; int age;
        Ghost(Player p) { player = p; x = p.getX(); y = p.getY(); z = p.getZ(); }
    }

    @EventBusSubscriber(modid = CyberwareMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void registerLayers(RegisterGuiLayersEvent event) {
            event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(CyberwareMod.MODID, "sandevistan_hud"),
                    SandevistanFx::renderHud);
        }
    }

    @EventBusSubscriber(modid = CyberwareMod.MODID, value = Dist.CLIENT)
    public static class GameBus {

        /** Every tick: age the ghosts, make new ones, spawn sparks. */
        @SubscribeEvent
        public static void onTick(ClientTickEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) { GHOSTS.clear(); return; }
            if (mc.isPaused()) return;

            GHOSTS.removeIf(g -> ++g.age >= LIFE);

            for (Player p : mc.level.players()) {
                if (!p.hasEffect(ModEffects.OVERCLOCK)) continue;

                double dx = p.getX() - p.xo, dz = p.getZ() - p.zo;
                boolean moving = dx * dx + dz * dz > 0.0025;
                if (moving && p.tickCount % 2 == 0 && GHOSTS.size() < 150) {
                    GHOSTS.add(new Ghost(p));
                }
                for (int i = 0; i < 2; i++) {
                    mc.level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                            p.getRandomX(0.7), p.getRandomY(), p.getRandomZ(0.7), 0, 0, 0);
                }
                if (p.tickCount % 3 == 0) {
                    mc.level.addParticle(ParticleTypes.END_ROD,
                            p.getRandomX(0.5), p.getRandomY(), p.getRandomZ(0.5), 0, 0.02, 0);
                }
            }
        }

        /** Draws every ghost as a flat, glowing, fading copy of the player. */
        @SubscribeEvent
        public static void onRender(RenderLevelStageEvent event) {
            if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES || GHOSTS.isEmpty()) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) return;

            float partial = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            Vec3 cam = event.getCamera().getPosition();
            PoseStack ps = event.getPoseStack();
            MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
            EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

            for (Ghost g : GHOSTS) {
                if (g.player.isRemoved()) continue;
                float t = Math.min(1f, (g.age + partial) / LIFE);          // 0 = new, 1 = gone
                int a = (int) (170 * (1f - t));                              // fades out
                int r = (int) (255 * (1f - t));                              // magenta -> cyan
                int gr = (int) (60 + 195 * t);
                int b = (int) (200 + 55 * t);
                if (a <= 4) continue;

                MultiBufferSource tinted = type -> type.format() == DefaultVertexFormat.NEW_ENTITY
                        ? new TintConsumer(buffers.getBuffer(GHOST_TYPE), r, gr, b, a)
                        : new TintConsumer(null, 0, 0, 0, 0); // drop name tags, glints, etc.

                EntityRenderer<? super Player> renderer = dispatcher.getRenderer(g.player);
                Vec3 off = renderer.getRenderOffset(g.player, partial);
                ps.pushPose();
                ps.translate(g.x - cam.x + off.x, g.y - cam.y + off.y, g.z - cam.z + off.z);
                renderer.render(g.player, g.player.getYRot(), partial, ps, tinted, 0xF000F0);
                ps.popPose();
            }
            buffers.endBatch(GHOST_TYPE);
        }

        /** Wider field of view while overclocked, for that speed feeling. */
        @SubscribeEvent
        public static void onFov(ComputeFovModifierEvent event) {
            if (event.getPlayer().hasEffect(ModEffects.OVERCLOCK)) {
                event.setNewFovModifier(event.getNewFovModifier() * 1.2f);
            }
        }
    }

    /** Screen glow while active + a little bar that shows the recharge. */
    private static void renderHud(GuiGraphics gfx, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        if (p == null) return;
        float partial = delta.getGameTimeDeltaPartialTick(false);
        int w = gfx.guiWidth(), h = gfx.guiHeight();

        boolean active = p.hasEffect(ModEffects.OVERCLOCK);
        if (active) {
            float pulse = 0.5f + 0.5f * (float) Math.sin((p.tickCount + partial) * 0.6f);
            int a = (int) (28 + 22 * pulse);
            gfx.fill(0, 0, w, h, (a << 24) | 0xFF3CC8);
            gfx.fill(0, 0, w, 3, 0xCC00FFFF);
            gfx.fill(0, h - 3, w, h, 0xCC00FFFF);
        }

        Item item = ModItems.IMPLANT_ITEMS.get(Implant.SANDEVISTAN).get();
        float cd = p.getCooldowns().getCooldownPercent(item, partial); // 1 -> 0 as it recharges
        if (active || cd > 0f) {
            int bw = 90, x = w - bw - 12, y = h - 34;
            gfx.fill(x - 1, y - 1, x + bw + 1, y + 7, 0xAA000000);
            gfx.fill(x, y, x + (int) (bw * (1f - cd)), y + 6, active ? 0xFFFFE600 : 0xFFFF3CC8);
            gfx.drawString(mc.font, Component.literal(active ? "OVERCLOCK" : "RECHARGING"), x, y - 11, 0xFF00FFFF, true);
        }
    }
}
