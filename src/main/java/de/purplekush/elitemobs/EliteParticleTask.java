package de.purplekush.elitemobs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/** Erzeugt in kurzen Abständen leuchtende Partikel um alle bekannten Elite-Monster. */
public final class EliteParticleTask extends BukkitRunnable {

    private final PurpleKushEliteMobs plugin;
    private final Set<UUID> tracked;

    public EliteParticleTask(PurpleKushEliteMobs plugin, Set<UUID> tracked) {
        this.plugin = plugin;
        this.tracked = tracked;
    }

    @Override
    public void run() {
        EliteSettings s = plugin.settings();
        Iterator<UUID> it = tracked.iterator();
        while (it.hasNext()) {
            Entity entity = Bukkit.getEntity(it.next());
            // Tot oder entladen: aus der Liste nehmen (beim Laden des Chunks wird es wieder erfasst)
            if (entity == null || !entity.isValid()) {
                it.remove();
                continue;
            }
            Location center = entity.getLocation().add(0, entity.getHeight() * 0.5, 0);
            double spreadXZ = entity.getWidth() * 0.6;
            double spreadY = entity.getHeight() * 0.45;

            if (s.dustParticle) {
                entity.getWorld().spawnParticle(s.particle, center, s.particleCount,
                        spreadXZ, spreadY, spreadXZ, 0.0, s.dustOptions);
            } else {
                entity.getWorld().spawnParticle(s.particle, center, s.particleCount,
                        spreadXZ, spreadY, spreadXZ, 0.01);
            }
        }
    }
}
