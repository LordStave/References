package de.lordstave.sqlbansystem;

import de.lordstave.sqlbansystem.commands.CommandBan;
import de.lordstave.sqlbansystem.commands.CommandBanInfo;
import de.lordstave.sqlbansystem.commands.CommandTempban;
import de.lordstave.sqlbansystem.commands.CommandUnban;
import de.lordstave.sqlbansystem.database.DatabaseHandler;
import de.lordstave.sqlbansystem.events.PlayerEvents;
import de.lordstave.sqlbansystem.punishment.BanManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseHandler databaseHandler;
    private BanManager banManager;

    @Override
    public void onEnable() {
        this.initAll();
    }

    @Override
    public void onDisable() {
        this.databaseHandler.closeConnection();
    }

    public static Main getInstance() {
        return instance;
    }

    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }

    public BanManager getBanManager() {
        return banManager;
    }

    private void initAll() {
        instance = this;

        this.writeDefaults();
        if(!setupDatabase()) {
            this.getServer().shutdown();
            return;
        }
        this.banManager = new BanManager();

        Bukkit.getPluginManager().registerEvents(new PlayerEvents(), this);
        this.getCommand("ban").setExecutor(new CommandBan());
        this.getCommand("tempban").setExecutor(new CommandTempban());
        this.getCommand("unban").setExecutor(new CommandUnban());
        this.getCommand("baninfo").setExecutor(new CommandBanInfo());
    }

    private void writeDefaults() {
        this.getConfig().addDefault("MySQL.Host", "127.0.0.1");
        this.getConfig().addDefault("MySQL.Port", 3306);
        this.getConfig().addDefault("MySQL.User", "user");
        this.getConfig().addDefault("MySQL.Database", "database");
        this.getConfig().addDefault("MySQL.Password", "password");
        this.getConfig().addDefault("MySQL.Connection-Pool-Size", 2);
        this.getConfig().addDefault("Settings.Thread-Pool-Size", 2);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    private boolean setupDatabase() {
        String host = this.getConfig().getString("MySQL.Host");
        String user = this.getConfig().getString("MySQL.User");
        String database = this.getConfig().getString("MySQL.Database");
        String password = this.getConfig().getString("MySQL.Password");

        int port = this.getConfig().getInt("MySQL.Port");
        int connectionPoolSize = this.getConfig().getInt("MySQL.Connection-Pool-Size");
        int threadPoolSize = this.getConfig().getInt("Settings.Thread-Pool-Size");

        this.databaseHandler = new DatabaseHandler(host, port, user, database, password, threadPoolSize, connectionPoolSize);
        return databaseHandler.openConnection();
    }
}
