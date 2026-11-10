package de.purplekush.elitemobs;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Gibt einem Elite-Monster bessere Rüstung und Waffen. Es werden keine Namen vergeben. */
public final class EliteGear {

    private record ArmorPiece(EquipmentSlot slot, String suffix) {
    }

    private static final ArmorPiece[] ARMOR = {
            new ArmorPiece(EquipmentSlot.HEAD, "_HELMET"),
            new ArmorPiece(EquipmentSlot.CHEST, "_CHESTPLATE"),
            new ArmorPiece(EquipmentSlot.LEGS, "_LEGGINGS"),
            new ArmorPiece(EquipmentSlot.FEET, "_BOOTS")
    };

    private EliteGear() {
    }

    public static void apply(LivingEntity mob, EliteSettings s) {
        EntityEquipment eq = mob.getEquipment();
        if (eq == null) {
            return;
        }
        ThreadLocalRandom r = ThreadLocalRandom.current();

        // Rüstung
        for (ArmorPiece piece : ARMOR) {
            if (r.nextDouble() >= s.armorSlotChance) {
                continue;
            }
            String tier = pickTier(s.armorTiers);
            if (tier == null) {
                break;
            }
            Material material = Material.matchMaterial(tier + piece.suffix());
            if (material == null) {
                continue;
            }
            ItemStack item = new ItemStack(material);
            maybeEnchant(item, Enchantment.PROTECTION, s);
            eq.setItem(piece.slot(), item);
        }

        // Waffe
        if (r.nextDouble() < s.weaponChance) {
            EntityType type = mob.getType();
            if (type == EntityType.SKELETON || type == EntityType.STRAY) {
                ItemStack bow = new ItemStack(Material.BOW);
                maybeEnchant(bow, Enchantment.POWER, s);
                eq.setItem(EquipmentSlot.HAND, bow);
            } else if (eq.getItemInMainHand().getType() != Material.TRIDENT) {
                String tier = pickTier(s.weaponTiers);
                if (tier != null) {
                    Material material = Material.matchMaterial(tier + "_SWORD");
                    if (material != null) {
                        ItemStack sword = new ItemStack(material);
                        maybeEnchant(sword, Enchantment.SHARPNESS, s);
                        eq.setItem(EquipmentSlot.HAND, sword);
                    }
                }
            }
        }

        // Drop-Chance der Ausrüstung
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST,
                EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            eq.setDropChance(slot, s.dropChance);
        }
    }

    private static void maybeEnchant(ItemStack item, Enchantment enchantment, EliteSettings s) {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        if (r.nextDouble() < s.enchantChance) {
            int level = 1 + r.nextInt(s.maxEnchantLevel);
            item.addUnsafeEnchantment(enchantment, level);
        }
    }

    /** Wählt ein Material-Level anhand der Gewichtung aus der Config. */
    private static String pickTier(Map<String, Integer> tiers) {
        int total = 0;
        for (int w : tiers.values()) {
            total += w;
        }
        if (total <= 0) {
            return null;
        }
        int roll = ThreadLocalRandom.current().nextInt(total);
        for (Map.Entry<String, Integer> e : tiers.entrySet()) {
            roll -= e.getValue();
            if (roll < 0) {
                return e.getKey();
            }
        }
        return null;
    }
}
