package de.purplekush.elitemobs;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class PurpleKushEliteMobs extends JavaPlugin {

    private NamespacedKey eliteKey;
    private EliteSettings settings;
    private final Set<UUID> tracked = new HashSet<>();
    private BukkitTask particleTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.eliteKey = new NamespacedKey(this, "elite");
        this.settings = new EliteSettings(getConfig(), getLogger());

        getServer().getPluginManager().registerEvents(new EliteSpawnListener(this), this);

        EliteCommand command = new EliteCommand(this);
        var pluginCommand = getCommand("elitemobs");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(command);
            pluginCommand.setTabCompleter(command);
        }

        startParticleTask();

        // Bereits geladene Elite-Monster (z. B. Spawn-Chunks) einmalig erfassen
        Bukkit.getScheduler().runTask(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                for (LivingEntity living : world.getLivingEntities()) {
                    if (isElite(living)) {
                        track(living);
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {
        if (particleTask != null) {
            particleTask.cancel();
        }
        tracked.clear();
    }

    public void reloadSettings() {
        reloadConfig();
        this.settings = new EliteSettings(getConfig(), getLogger());
        startParticleTask();
    }

    private void startParticleTask() {
        if (particleTask != null) {
            particleTask.cancel();
            particleTask = null;
        }
        if (settings.particlesEnabled) {
            particleTask = new EliteParticleTask(this, tracked)
                    .runTaskTimer(this, 20L, settings.intervalTicks);
        }
    }

    public EliteSettings settings() {
        return settings;
    }

    /** Macht ein Monster zum Elite-Monster: bessere Ausrüstung, Markierung, Partikel. Kein Name, keine Bossbar. */
    public void makeElite(LivingEntity mob) {
        EliteGear.apply(mob, settings);
        mob.getPersistentDataContainer().set(eliteKey, PersistentDataType.BYTE, (byte) 1);
        if (settings.glowingOutline) {
            mob.setGlowing(true);
        }
        track(mob);
    }

    public boolean isElite(LivingEntity mob) {
        return mob.getPersistentDataContainer().has(eliteKey, PersistentDataType.BYTE);
    }

    public void track(LivingEntity mob) {
        tracked.add(mob.getUniqueId());
    }
}
