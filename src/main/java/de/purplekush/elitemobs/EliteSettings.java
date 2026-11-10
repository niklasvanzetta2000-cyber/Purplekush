package de.purplekush.elitemobs;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/** Liest alle Werte aus der config.yml und hält sie fertig geparst bereit. */
public final class EliteSettings {

    // Elite
    public final double chance;
    public final boolean nightOnly;
    public final long nightStart;
    public final long nightEnd;
    public final Set<String> worlds;
    public final Set<SpawnReason> spawnReasons;
    public final Set<EntityType> mobTypes;

    // Ausrüstung
    public final double armorSlotChance;
    public final Map<String, Integer> armorTiers;
    public final double weaponChance;
    public final Map<String, Integer> weaponTiers;
    public final double enchantChance;
    public final int maxEnchantLevel;
    public final float dropChance;

    // Partikel
    public final boolean particlesEnabled;
    public final Particle particle;
    public final boolean dustParticle;
    public final Particle.DustOptions dustOptions;
    public final int intervalTicks;
    public final int particleCount;
    public final boolean glowingOutline;

    public EliteSettings(FileConfiguration cfg, Logger log) {
        this.chance = clamp(cfg.getDouble("elite.chance", 0.15), 0.0, 1.0);
        this.nightOnly = cfg.getBoolean("elite.night-only", true);
        this.nightStart = cfg.getLong("elite.night-start", 13000L);
        this.nightEnd = cfg.getLong("elite.night-end", 23000L);

        this.worlds = new HashSet<>();
        for (String w : cfg.getStringList("elite.worlds")) {
            worlds.add(w.toLowerCase(Locale.ROOT));
        }

        this.spawnReasons = EnumSet.noneOf(SpawnReason.class);
        for (String s : cfg.getStringList("elite.spawn-reasons")) {
            try {
                spawnReasons.add(SpawnReason.valueOf(s.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ex) {
                log.warning("Unbekannte Spawn-Art in der Config: " + s);
            }
        }

        this.mobTypes = new HashSet<>();
        for (String s : cfg.getStringList("elite.mob-types")) {
            EntityType type = parseEntityType(s);
            if (type == null) {
                log.warning("Unbekannter Monstertyp in der Config: " + s);
            } else {
                mobTypes.add(type);
            }
        }

        this.armorSlotChance = clamp(cfg.getDouble("equipment.armor-slot-chance", 0.6), 0.0, 1.0);
        this.armorTiers = readTiers(cfg.getConfigurationSection("equipment.armor-tiers"), "_HELMET", log);
        this.weaponChance = clamp(cfg.getDouble("equipment.weapon-chance", 0.7), 0.0, 1.0);
        this.weaponTiers = readTiers(cfg.getConfigurationSection("equipment.weapon-tiers"), "_SWORD", log);
        this.enchantChance = clamp(cfg.getDouble("equipment.enchant-chance", 0.3), 0.0, 1.0);
        this.maxEnchantLevel = Math.max(1, cfg.getInt("equipment.max-enchant-level", 3));
        this.dropChance = (float) clamp(cfg.getDouble("equipment.drop-chance", 0.05), 0.0, 1.0);

        this.particlesEnabled = cfg.getBoolean("particles.enabled", true);
        String type = cfg.getString("particles.type", "DUST").toUpperCase(Locale.ROOT);
        this.particle = switch (type) {
            case "WITCH" -> Particle.WITCH;
            case "END_ROD" -> Particle.END_ROD;
            case "SOUL_FIRE_FLAME" -> Particle.SOUL_FIRE_FLAME;
            case "FLAME" -> Particle.FLAME;
            case "ENCHANT" -> Particle.ENCHANT;
            default -> Particle.DUST;
        };
        this.dustParticle = this.particle == Particle.DUST;
        this.dustOptions = new Particle.DustOptions(
                parseColor(cfg.getString("particles.color", "AA00FF"), log),
                (float) Math.max(0.1, cfg.getDouble("particles.size", 1.2)));
        this.intervalTicks = Math.max(1, cfg.getInt("particles.interval-ticks", 5));
        this.particleCount = Math.max(1, cfg.getInt("particles.count", 4));
        this.glowingOutline = cfg.getBoolean("particles.glowing-outline", false);
    }

    private static EntityType parseEntityType(String name) {
        try {
            return EntityType.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private static Map<String, Integer> readTiers(ConfigurationSection section, String suffixToCheck, Logger log) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (section == null) {
            return result;
        }
        for (String key : section.getKeys(false)) {
            int weight = section.getInt(key, 0);
            String tier = key.toUpperCase(Locale.ROOT);
            if (weight <= 0) {
                continue;
            }
            if (Material.matchMaterial(tier + suffixToCheck) == null) {
                log.warning("Unbekanntes Material-Level in der Config: " + key);
                continue;
            }
            result.put(tier, weight);
        }
        return result;
    }

    private static Color parseColor(String hex, Logger log) {
        try {
            String h = hex.startsWith("#") ? hex.substring(1) : hex;
            return Color.fromRGB(Integer.parseInt(h, 16));
        } catch (RuntimeException ex) {
            log.warning("Ungültige Farbe in der Config: " + hex + " (nutze AA00FF)");
            return Color.fromRGB(0xAA00FF);
        }
    }

    private static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
