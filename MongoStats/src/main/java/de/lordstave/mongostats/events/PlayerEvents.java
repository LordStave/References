package de.lordstave.mongostats.events;

import de.lordstave.mongostats.Main;
import de.lordstave.mongostats.cache.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerEvents implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().getStatsCache().addPlayerToCache(player.getUniqueId());
        PlayerStats stats =  Main.getInstance().getStatsCache().getPlayerFromCache(player.getUniqueId());
        stats.executeIfReady(() -> stats.setKills(12324 + ThreadLocalRandom.current().nextInt(1024)));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Main.getInstance().getStatsCache().removePlayerFromCache(player.getUniqueId());
    }
}
