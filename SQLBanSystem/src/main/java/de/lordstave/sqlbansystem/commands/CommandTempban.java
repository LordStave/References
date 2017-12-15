package de.lordstave.sqlbansystem.commands;

import de.lordstave.sqlbansystem.Main;
import de.lordstave.sqlbansystem.punishment.BanManager;
import de.lordstave.sqlbansystem.util.UUIDFetcher;
import de.lordstave.sqlbansystem.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandTempban implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("system.tempban")) {
            sender.sendMessage("§cYou have not enough permissions.");
            return true;
        }
        if(args.length < 3) {
            /**
             * Time format
             * 10s = 10 seconds
             * 10m = 10 minutes
             * 10h = 10 hours
             * 10d = 10 days
             * 10mo = 10 months
             * 10y = 10 years
             *
             * Example: /tempban Kevin 8d10m Test
             */
            sender.sendMessage("§cSyntax: /tempban <player> <time> <reason>");
            return true;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 2; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        String reason = builder.substring(0, builder.length() - 1);
        long time = Util.parseDateDiff(args[1]);
        if(time == -1L) {
            sender.sendMessage("§cSyntax: /tempban <player> <time> <reason>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target != null) {
            Main.getInstance().getBanManager().ban(target.getUniqueId(), new BanManager.BanInformation(target.getUniqueId(), sender.getName(), reason, System.currentTimeMillis(), time), (Boolean success) -> {
                if(!success) {
                    sender.sendMessage("§cThe player couldn't be banned.");
                    return;
                }
                sender.sendMessage("§cThe player was banned successfully.");
                Bukkit.getScheduler().runTask(Main.getInstance(), () -> target.kickPlayer("§4You are banned!"));
            });
        } else {
            UUIDFetcher.getUUID(args[0], (UUID uuid) -> {
                if(uuid == null) {
                    sender.sendMessage("§cThis player doesn't exists.");
                    return;
                }
                boolean banned = Main.getInstance().getBanManager().isBanned(uuid);
                if(banned) {
                    sender.sendMessage("§cThis player is already banned.");
                    return;
                }
                boolean success = Main.getInstance().getBanManager().ban(uuid, new BanManager.BanInformation(uuid, sender.getName(), reason, System.currentTimeMillis(), time));
                if(!success) {
                    sender.sendMessage("§cThe player couldn't be banned.");
                    return;
                }
                sender.sendMessage("§cThe player was banned successfully.");
            });
        }
        return true;
    }
}
