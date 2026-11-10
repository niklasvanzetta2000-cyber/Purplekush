package de.purplekush.elitemobs;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** /elitemobs reload  und  /elitemobs spawn [Monstertyp] */
public final class EliteCommand implements TabExecutor {

    private final PurpleKushEliteMobs plugin;

    public EliteCommand(PurpleKushEliteMobs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§5PurpleKush-EliteMobs §7– Befehle: §f/" + label + " reload§7, §f/" + label + " spawn [Typ]");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadSettings();
            sender.sendMessage("§5EliteMobs §7– Config neu geladen.");
            return true;
        }

        if (args[0].equalsIgnoreCase("spawn")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("§cDieser Befehl geht nur im Spiel.");
                return true;
            }
            EntityType type = EntityType.ZOMBIE;
            if (args.length >= 2) {
                try {
                    type = EntityType.valueOf(args[1].toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException ex) {
                    sender.sendMessage("§cUnbekannter Monstertyp: " + args[1]);
                    return true;
                }
            }
            Location loc = player.getLocation();
            Entity spawned = loc.getWorld().spawnEntity(loc, type);
            if (spawned instanceof LivingEntity living) {
                plugin.makeElite(living);
                sender.sendMessage("§5EliteMobs §7– Elite-Monster gespawnt: §f" + type.name());
            } else {
                spawned.remove();
                sender.sendMessage("§cDieser Typ ist kein Lebewesen.");
            }
            return true;
        }

        sender.sendMessage("§cUnbekannter Unterbefehl.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (String s : List.of("reload", "spawn")) {
                if (s.startsWith(args[0].toLowerCase(Locale.ROOT))) {
                    result.add(s);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("spawn")) {
            for (EntityType type : plugin.settings().mobTypes) {
                String name = type.name();
                if (name.toLowerCase(Locale.ROOT).startsWith(args[1].toLowerCase(Locale.ROOT))) {
                    result.add(name);
                }
            }
        }
        return result;
    }
}
