package de.purplekush.elitemobs;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.EntitiesLoadEvent;

import java.util.concurrent.ThreadLocalRandom;

public final class EliteSpawnListener implements Listener {

    private final PurpleKushEliteMobs plugin;

    public EliteSpawnListener(PurpleKushEliteMobs plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        EliteSettings s = plugin.settings();
        LivingEntity mob = event.getEntity();

        if (!s.mobTypes.contains(mob.getType())) {
            return;
        }
        if (!s.spawnReasons.contains(event.getSpawnReason())) {
            return;
        }
        World world = mob.getWorld();
        if (world.getEnvironment() != World.Environment.NORMAL) {
            return;
        }
        if (!s.worlds.isEmpty() && !s.worlds.contains(world.getName().toLowerCase())) {
            return;
        }
        if (s.nightOnly && !isNight(world, s)) {
            return;
        }
        if (ThreadLocalRandom.current().nextDouble() >= s.chance) {
            return;
        }
        plugin.makeElite(mob);
    }

    /** Merkt sich bereits vorhandene Elite-Monster wieder, wenn ihr Chunk neu geladen wird. */
    @EventHandler
    public void onEntitiesLoad(EntitiesLoadEvent event) {
        for (Entity entity : event.getEntities()) {
            if (entity instanceof LivingEntity living && plugin.isElite(living)) {
                plugin.track(living);
            }
        }
    }

    private static boolean isNight(World world, EliteSettings s) {
        long time = world.getTime() % 24000L;
        if (s.nightStart <= s.nightEnd) {
            return time >= s.nightStart && time < s.nightEnd;
        }
        return time >= s.nightStart || time < s.nightEnd;
    }
}
