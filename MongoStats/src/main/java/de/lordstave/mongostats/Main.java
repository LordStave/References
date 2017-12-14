package de.lordstave.mongostats;

import de.lordstave.mongostats.cache.StatsCache;
import de.lordstave.mongostats.commands.CommandStats;
import de.lordstave.mongostats.database.DatabaseHandler;
import de.lordstave.mongostats.events.PlayerEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseHandler databaseHandler;
    private StatsCache statsCache;

    @Override
    public void onEnable() {
        this.initAll();
    }

    @Override
    public void onDisable() {
        //Empty
    }

    public static Main getInstance() {
        return instance;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public StatsCache getStatsCache() {
        return statsCache;
    }

    private void initAll() {
        instance = this;
        this.writeDefaults();
        if(!setupDatabase()) {
            this.getServer().shutdown();
            return;
        }
        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        this.getCommand("stats").setExecutor(new CommandStats());
    }

    private void writeDefaults() {
        this.getConfig().addDefault("MongoDB.Host", "127.0.0.1");
        this.getConfig().addDefault("MongoDB.Port", 27017);
        this.getConfig().addDefault("MongoDB.User", "user");
        this.getConfig().addDefault("MongoDB.Database", "database");
        this.getConfig().addDefault("MongoDB.Password", "password");
        this.getConfig().addDefault("MongoDB.Stats-Table", "playerstats");
        this.getConfig().addDefault("MongoDB.Connection-Pool-Size", 2);
        this.getConfig().addDefault("Settings.Thread-Pool-Size", 2);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    private boolean setupDatabase() {
        String host = this.getConfig().getString("MongoDB.Host");
        String user = this.getConfig().getString("MongoDB.User");
        String database = this.getConfig().getString("MongoDB.Database");
        String password = this.getConfig().getString("MongoDB.Password");
        String stats_table = this.getConfig().getString("MongoDB.Stats-Table");

        int port = this.getConfig().getInt("MongoDB.Port");
        int connectionPoolSize = this.getConfig().getInt("MongoDB.Connection-Pool-Size");
        int threadPoolSize = this.getConfig().getInt("Settings.Thread-Pool-Size");

        this.databaseHandler = new DatabaseHandler(host, port, user, database, password, stats_table, threadPoolSize, connectionPoolSize);
        this.statsCache = new StatsCache();
        return databaseHandler.openConnection();
    }
}
