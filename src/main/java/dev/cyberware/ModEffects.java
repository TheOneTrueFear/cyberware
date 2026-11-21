package dev.cyberware;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, CyberwareMod.MODID);

    /** Marker effect: while a player has it, they leave afterimages, sparks, and the screen glows. */
    public static final DeferredHolder<MobEffect, OverclockEffect> OVERCLOCK =
            EFFECTS.register("overclock", OverclockEffect::new);

    public static class OverclockEffect extends MobEffect {
        public OverclockEffect() { super(MobEffectCategory.BENEFICIAL, 0xFFE600); }
    }
}
