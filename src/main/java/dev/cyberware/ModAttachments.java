package dev.cyberware;

import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CyberwareMod.MODID);

    /** Installed implant ids. Saved with the player and kept through death. */
    public static final Supplier<AttachmentType<List<String>>> IMPLANTS = ATTACHMENTS.register("implants",
            () -> AttachmentType.<List<String>>builder(() -> List.<String>of())
                    .serialize(Codec.STRING.listOf())
                    .copyOnDeath()
                    .build());
}
