package dev.cyberware;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public enum Implant {
    OPTICS("optics", Slot.EYES, null, 0, 2),
    SUBDERMAL_PLATING("subdermal_plating", Slot.SKIN, Attributes.ARMOR, 6, 2),
    TENDON_BOOSTERS("tendon_boosters", Slot.LEGS, Attributes.SAFE_FALL_DISTANCE, 12, 2),
    GORILLA_ARMS("gorilla_arms", Slot.ARMS, Attributes.ATTACK_DAMAGE, 3, 2),
    SANDEVISTAN("sandevistan", Slot.NERVOUS, null, 0, 4);

    public enum Slot { EYES, ARMS, LEGS, NERVOUS, SKIN }

    public final String id;
    public final Slot slot;
    public final Holder<Attribute> attribute; // nullable
    public final double amount;
    public final double humanityCost; // health points lost while installed

    Implant(String id, Slot slot, Holder<Attribute> attribute, double amount, double humanityCost) {
        this.id = id; this.slot = slot; this.attribute = attribute;
        this.amount = amount; this.humanityCost = humanityCost;
    }

    public static Implant byId(String id) {
        for (Implant i : values()) if (i.id.equals(id)) return i;
        return null;
    }
}
