package de.lordstave.sqlbansystem.database;

import com.zaxxer.hikari.HikariDataSource;
import de.lordstave.sqlbansystem.Main;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseHandler {

    private final String host;
    private final String user;
    private final String database;
    private final String password;
    private final int port;
    private final int threadPoolSize;
    private final int connectionPoolSize;

    private final ExecutorService executorService;

    private HikariDataSource hikariDataSource;
    private Connection connection;

    public DatabaseHandler(String host, int port, String user, String database, String password, int threadPoolSize, int connectionPoolSize) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.database = database;
        this.password = password;
        this.threadPoolSize = threadPoolSize;
        this.connectionPoolSize = connectionPoolSize;
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public boolean openConnection() {
        try {
            hikariDataSource = new HikariDataSource();
            hikariDataSource.setMaximumPoolSize(connectionPoolSize);
            hikariDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
            hikariDataSource.addDataSourceProperty("serverName", host);
            hikariDataSource.addDataSourceProperty("port", "3306");
            hikariDataSource.addDataSourceProperty("databaseName", database);
            hikariDataSource.addDataSourceProperty("user", user);
            hikariDataSource.addDataSourceProperty("password", password);

            this.connection = hikariDataSource.getConnection();
            this.refreshConnection();
            return true;
        } catch(SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeAsync(Runnable runnable) {
        executorService.execute(runnable);
    }

    public void executeUpdate(PreparedStatement statement) {
        try {
            if(statement != null) {
                statement.executeUpdate();
                statement.close();
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void executeUpdate(String query) {
        try {
            if(query != null) {
                PreparedStatement statement = this.connection.prepareStatement(query);
                statement.executeUpdate();
                statement.close();
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void close(ResultSet resultSet, PreparedStatement... statements) {
        try {
            if(resultSet != null) {
                resultSet.close();
            }
            for(PreparedStatement statement : statements) statement.close();
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void refreshConnection() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getInstance(), () -> {
            try {
                if(!connection.isClosed()) {
                    PreparedStatement statement = this.getConnection().prepareStatement("/* ping */ SELECT 1");
                    ResultSet resultSet = statement.executeQuery();
                    this.close(resultSet, statement);
                }
            } catch(SQLException exception) {
                exception.printStackTrace();
            }
        }, 10L, 20L * 20L);
    }
}
