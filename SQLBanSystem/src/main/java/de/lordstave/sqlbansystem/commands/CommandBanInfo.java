package de.lordstave.sqlbansystem.commands;

import de.lordstave.sqlbansystem.Main;
import de.lordstave.sqlbansystem.punishment.BanManager;
import de.lordstave.sqlbansystem.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CommandBanInfo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("system.baninfo")) {
            sender.sendMessage("§cYou have not enough permissions.");
            return true;
        }
        if(args.length != 1) {
            sender.sendMessage("§cSyntax: /baninfo <player>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            UUIDFetcher.getUUID(args[0], (UUID uuid) -> {
                if(uuid == null) {
                    sender.sendMessage("§cThis player doesn't exists.");
                    return;
                }
                this.sendBanLogs(sender, uuid);
                return;
            });
            return true;
        }
        this.sendBanLogs(sender, target.getUniqueId());
        return false;
    }

    private void sendBanLogs(CommandSender sender, UUID target) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        List<BanManager.BanInformation> informationList = Main.getInstance().getBanManager().getArchiveInformations(target);
        if(informationList.isEmpty() || informationList == null) {
            sender.sendMessage("§4There is no Banlog available.");
            return;
        }
        for(BanManager.BanInformation information : informationList) {
            if(information.isPermanently()) {
                sender.sendMessage(" §7Type: " + "§4Permanently" + " §7At: §6" + dateFormat.format(new Date(information.getTimestamp())) + " §7Banned by: §6" + information.getBannedBy() + " §7Reason: §e" + information.getReason());
                continue;
            }
            sender.sendMessage(" §7Type: " + "§cTemporarily" + " §7At: §6" + dateFormat.format(new Date(information.getTimestamp())) + " §7Duration: §6" + dateFormat.format(new Date(information.getDuration())) + " §7Banned by: §6" + information.getBannedBy() + " §7Reason: §e" + information.getReason());
        }
    }
}
