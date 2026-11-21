package dev.cyberware;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

public class ClientModEvents {
    public static final KeyMapping OVERCLOCK = new KeyMapping("key.cyberware.overclock",
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.cyberware");

    @EventBusSubscriber(modid = CyberwareMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBus {
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(OVERCLOCK);
        }
    }

    @EventBusSubscriber(modid = CyberwareMod.MODID, value = Dist.CLIENT)
    public static class GameBus {
        @SubscribeEvent
        public static void onTick(ClientTickEvent.Post event) {
            while (OVERCLOCK.consumeClick()) {
                if (Minecraft.getInstance().player != null) {
                    PacketDistributor.sendToServer(new DashPayload());
                }
            }
        }
    }
}
