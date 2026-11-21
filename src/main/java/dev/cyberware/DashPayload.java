package dev.cyberware;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Sent client -> server when the player presses the Sandevistan key. */
public record DashPayload() implements CustomPacketPayload {
    public static final Type<DashPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberwareMod.MODID, "dash"));
    public static final StreamCodec<RegistryFriendlyByteBuf, DashPayload> CODEC = StreamCodec.unit(new DashPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
