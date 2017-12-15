package de.lordstave.sqlbansystem.commands;

import de.lordstave.sqlbansystem.Main;
import de.lordstave.sqlbansystem.punishment.BanManager;
import de.lordstave.sqlbansystem.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandBan implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("system.ban")) {
            sender.sendMessage("§cYou have not enough permissions.");
            return true;
        }
        if(args.length < 2) {
            sender.sendMessage("§cSyntax: /ban <player> <reason>");
            return true;
        }
        StringBuilder builder = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }
        String reason = builder.substring(0, builder.length() - 1);
        Player target = Bukkit.getPlayer(args[0]);
        if(target != null) {
            Main.getInstance().getBanManager().ban(target.getUniqueId(), new BanManager.BanInformation(target.getUniqueId(), sender.getName(), reason, System.currentTimeMillis(), -1L), (Boolean success) -> {
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
                boolean success = Main.getInstance().getBanManager().ban(uuid, new BanManager.BanInformation(uuid, sender.getName(), reason, System.currentTimeMillis(), -1L));
                if(!success) {
                    sender.sendMessage("§cThe player couldn't be banned.");
                    return;
                }
                sender.sendMessage("§cThe player was banned successfully.");
            });
        }
        return true;
    }

    private void ban(CommandSender sender, BanManager.BanInformation information) {

    }
}
