package de.lordstave.mongostats.cache;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsCache {

    private final Map<UUID, PlayerStats> statsMap;

    public StatsCache() {
        this.statsMap = new ConcurrentHashMap<>();
    }

    public Map<UUID, PlayerStats> getStatsCache() {
        return statsMap;
    }

    public void addPlayerToCache(UUID uuid) {
        if(uuid == null) {
            return;
        }
        if(statsMap.containsKey(uuid)) {
            return;
        }
        this.statsMap.put(uuid, new PlayerStats(uuid));
    }

    public void removePlayerFromCache(UUID uuid) {
        if(uuid == null) {
            return;
        }
        this.statsMap.entrySet().removeIf((Map.Entry<UUID, PlayerStats> current) -> current.getKey().equals(uuid));
    }

    public PlayerStats getPlayerFromCache(UUID uuid) {
        if(uuid == null) {
            return null;
        }
        if(!statsMap.containsKey(uuid)) {
            return null;
        }
        return statsMap.get(uuid);
    }
}
