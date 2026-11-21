package dev.cyberware;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** All implant logic lives here: install, remove, and applying effects. */
public class Cyber {
    public static List<String> installed(Player p) {
        return p.getData(ModAttachments.IMPLANTS);
    }

    public static boolean has(Player p, Implant imp) {
        return installed(p).contains(imp.id);
    }

    private static void giveBack(Player p, Implant imp) {
        p.getInventory().placeItemBackInInventory(new ItemStack(ModItems.IMPLANT_ITEMS.get(imp).get()));
    }

    /** Installs an implant, swapping out anything in the same slot. */
    public static void install(Player p, Implant imp) {
        List<String> cur = new ArrayList<>(installed(p));
        cur.removeIf(id -> {
            Implant old = Implant.byId(id);
            if (old == null) return true;
            if (old.slot == imp.slot || old == imp) { giveBack(p, old); return true; }
            return false;
        });
        cur.add(imp.id);
        p.setData(ModAttachments.IMPLANTS, List.copyOf(cur));
        sync(p);
    }

    public static void removeAll(Player p) {
        for (String id : installed(p)) {
            Implant old = Implant.byId(id);
            if (old != null) giveBack(p, old);
        }
        p.setData(ModAttachments.IMPLANTS, List.of());
        sync(p);
    }

    private static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(CyberwareMod.MODID, path);
    }

    private static void setMod(Player p, Holder<Attribute> attr, ResourceLocation id, double amount, boolean on) {
        AttributeInstance inst = p.getAttribute(attr);
        if (inst == null) return;
        if (on) inst.addOrUpdateTransientModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
        else inst.removeModifier(id);
    }

    /** Re-applies every implant's passive effects. Safe to call as often as you like. */
    public static void sync(Player p) {
        for (Implant imp : Implant.values()) {
            boolean on = has(p, imp);
            if (imp.attribute != null) setMod(p, imp.attribute, loc("implant_" + imp.id), imp.amount, on);
            setMod(p, Attributes.MAX_HEALTH, loc("humanity_" + imp.id), -imp.humanityCost, on);
        }
        if (has(p, Implant.OPTICS)) {
            p.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false, false));
        }
        if (p.getHealth() > p.getMaxHealth()) p.setHealth(p.getMaxHealth());
    }
}
