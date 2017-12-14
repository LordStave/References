package de.lordstave.mongostats.commands;

import de.lordstave.mongostats.Main;
import de.lordstave.mongostats.cache.PlayerStats;
import de.lordstave.mongostats.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandStats implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String... args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player!");
            return true;
        }
        Player player = (Player) sender;
        switch(args.length) {
            case 0: {
                Main.getInstance().getStatsCache().addPlayerToCache(player.getUniqueId());
                PlayerStats stats = Main.getInstance().getStatsCache().getPlayerFromCache(player.getUniqueId());
                stats.executeIfReady(() -> {
                    player.sendMessage("§aYour Stats");
                    player.sendMessage("§7Kills: §e" + stats.getKills());
                    player.sendMessage("§7Deaths: §e" + stats.getDeaths());
                    player.sendMessage("§7KD: §e" + stats.getKD());
                });
                return true;
            }
            case 1: {
                Player target = Bukkit.getPlayer(args[0]);
                if(target != null) {
                    Main.getInstance().getStatsCache().addPlayerToCache(target.getUniqueId());
                    PlayerStats stats = Main.getInstance().getStatsCache().getPlayerFromCache(target.getUniqueId());
                    stats.executeIfReady(() -> {
                        player.sendMessage("§aStats from §6" + target.getName());
                        player.sendMessage("§7Kills: §e" + stats.getKills());
                        player.sendMessage("§7Deaths: §e" + stats.getDeaths());
                        player.sendMessage("§7KD: §e" + stats.getKD());
                    });
                    return true;
                }
                UUIDFetcher.getUUID(args[0], (UUID uuid) -> {
                    if(uuid == null) {
                        player.sendMessage("§cThis player does not exist.");
                        return;
                    }
                    Main.getInstance().getStatsCache().addPlayerToCache(uuid);
                    PlayerStats stats = Main.getInstance().getStatsCache().getPlayerFromCache(uuid);
                    stats.executeIfReady(() -> {
                        player.sendMessage("§aStats from §6" + args[0]);
                        player.sendMessage("§7Kills: §e" + stats.getKills());
                        player.sendMessage("§7Deaths: §e" + stats.getDeaths());
                        player.sendMessage("§7KD: §e" + stats.getKD());
                    });
                });
                return true;
            }
            default: {
                player.sendMessage("Syntax: /stats");
                player.sendMessage("Sytax: /stats <player>");
                return true;
            }
        }
    }
}
