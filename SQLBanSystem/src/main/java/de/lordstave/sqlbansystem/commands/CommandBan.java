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
        if(target == null) {
            UUIDFetcher.getUUID(args[0], (UUID uuid) -> this.ban(sender, uuid, reason));
            return true;
        }
        Main.getInstance().getDatabaseHandler().executeAsync(() -> this.ban(sender, target.getUniqueId(), reason));
        return true;
    }

    private void ban(CommandSender sender, UUID target, String reason) {
        if(target == null) {
            sender.sendMessage("§cThis player doesn't exists.");
            return;
        }
        boolean banned = Main.getInstance().getBanManager().isBanned(target);
        if(banned) {
            sender.sendMessage("§cThis player is already banned.");
            return;
        }
        boolean success = Main.getInstance().getBanManager().ban(target, new BanManager.BanInformation(target, sender.getName(), reason, System.currentTimeMillis(), -1L));
        if(!success) {
            sender.sendMessage("§cThe player couldn't be banned.");
            return;
        }
        sender.sendMessage("§cThe player was banned successfully.");
    }
}
