package de.lordstave.sqlbansystem.commands;

import de.lordstave.sqlbansystem.Main;
import de.lordstave.sqlbansystem.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandUnban implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("system.tempban")) {
            sender.sendMessage("You have not enough permissions.");
            return true;
        }
        if(args.length != 1) {
            sender.sendMessage("Syntax: /unban <player>");
            return true;
        }
        String target = args[0];
        UUIDFetcher.getUUID(target, (UUID uuid) -> {
            if (uuid == null) {
                sender.sendMessage("This player doesn't exists.");
                return;
            }
            boolean banned = Main.getInstance().getBanManager().isBanned(uuid);
            if(!banned) {
                sender.sendMessage("This player is not banned!");
                return;
            }
            Main.getInstance().getBanManager().unban(uuid);
            sender.sendMessage("The player was pardoned.");
        });
        return true;
    }
}
