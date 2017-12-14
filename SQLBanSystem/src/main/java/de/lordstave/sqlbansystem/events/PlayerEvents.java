package de.lordstave.sqlbansystem.events;

import de.lordstave.sqlbansystem.Main;
import de.lordstave.sqlbansystem.punishment.BanManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        if(Main.getInstance().getBanManager().isBanned(uuid)) {
            BanManager.BanInformation information = Main.getInstance().getBanManager().getBanInformation(uuid);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            if(information.isPermanently()) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§4You are permanently banned!\n§cBanned until: §4PERMANENTLY");
                return;
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§4You are temporarily banned!\n§cBanned until: §6" + dateFormat.format(new Date(information.getTimestamp() + information.getDuration())));
                return;
            }
        }
    }
}
