package de.lordstave.sqlbansystem.punishment;

import de.lordstave.sqlbansystem.Main;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.function.Consumer;

public class BanManager {

    public BanManager() {
        Main.getInstance().getDatabaseHandler().executeUpdate("CREATE TABLE IF NOT EXISTS `Bans` (" +
                " `UUID` VARCHAR(36) NOT NULL," +
                " `BannedBy` VARCHAR(36) NOT NULL," +
                " `Reason` TEXT," +
                " `Timestamp` BIGINT NOT NULL," +
                " `Duration` BIGINT NOT NULL," +
                " PRIMARY KEY (`UUID`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        Main.getInstance().getDatabaseHandler().executeUpdate("CREATE TABLE IF NOT EXISTS `BansArchive` (" +
                " `UUID` VARCHAR(36) NOT NULL," +
                " `BannedBy` VARCHAR(36) NOT NULL," +
                " `Reason` TEXT NOT NULL," +
                " `Timestamp` BIGINT NOT NULL," +
                " `Duration` BIGINT NOT NULL," +
                " KEY (`UUID`)," +
                " KEY (`BannedBy`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
    }
    
    public boolean ban(UUID uuid, BanInformation information) {
        try {
            if(!isBanned(uuid)) {
                PreparedStatement statement = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("INSERT INTO `Bans` VALUES (?, ?, ?, ?, ?)");
                statement.setString(1, uuid.toString());
                statement.setString(2, information.getBannedBy());
                statement.setString(3, information.getReason());
                statement.setLong(4, information.getTimestamp());
                statement.setLong(5, information.getDuration());
                Main.getInstance().getDatabaseHandler().executeUpdate(statement);
                return true;
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public void ban(UUID uuid, BanInformation information, Consumer<Boolean> consumer) {
        Main.getInstance().getDatabaseHandler().executeAsync(() -> consumer.accept(ban(uuid, information)));
    }

    public boolean isBanned(UUID uuid) {
        BanInformation information = this.getBanInformation(uuid);
        if(information == null) {
            return false;
        }
        return information.getDuration() == -1L ? true : (information.getTimestamp() + information.getDuration() >= System.currentTimeMillis());
    }

    public void isBanned(UUID uuid, Consumer<Boolean> consumer) {
        Main.getInstance().getDatabaseHandler().executeAsync(() -> consumer.accept(isBanned(uuid)));
    }

    public boolean unban(UUID uuid) {
        try {
            BanInformation information = this.getBanInformation(uuid);
            BanLog log = new BanLog(information.getUUID());
            log.setBannedBy(information.getBannedBy());
            log.setReason(information.getReason());
            log.setTimestamp(information.getTimestamp());
            log.setDuration(information.getDuration());
            log.push();

            PreparedStatement statement = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("DELETE FROM `Bans` WHERE `UUID` = ?");
            statement.setString(1, uuid.toString());

            Main.getInstance().getDatabaseHandler().executeUpdate(statement);
            return true;
        } catch(SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void unban(UUID uuid, Consumer<Boolean> consumer) {
        Main.getInstance().getDatabaseHandler().executeAsync(() -> consumer.accept(unban(uuid)));
    }

    public BanInformation getBanInformation(UUID uuid) {
        try {
            PreparedStatement statement = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("SELECT `BannedBy`, `Reason`, `Timestamp`, `Duration` FROM `Bans` WHERE `UUID` = ?");
            statement.setString(1, uuid.toString());

            ResultSet resultSet = statement.executeQuery();
            BanInformation information = null;
            if(resultSet.next()) {
                information = new BanInformation(uuid, resultSet.getString("BannedBy"), resultSet.getString("Reason"), resultSet.getLong("Timestamp"), resultSet.getLong("Duration"));
            }
            Main.getInstance().getDatabaseHandler().close(resultSet, statement);
            return information;
        } catch(SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void getBanInformation(UUID uuid, Consumer<BanInformation> consumer) {
        Main.getInstance().getDatabaseHandler().executeAsync(() -> consumer.accept(getBanInformation(uuid)));
    }

    public boolean clearBanArchive(UUID uuid) {
        try {
            PreparedStatement statement = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("DELETE FROM `BansArchive` WHERE `UUID` = ?");
            statement.setString(1, uuid.toString());
            
            Main.getInstance().getDatabaseHandler().executeUpdate(statement);
            return true;
        } catch(SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public void clearBanArchive(UUID uuid, Consumer<Boolean> consumer) {
        Main.getInstance().getDatabaseHandler().executeAsync(() -> consumer.accept(clearBanArchive(uuid)));
    }

    public static class BanLog {

        private UUID uuid;
        private String bannedBy;
        private String reason;
        private long timestamp;
        private long duration;

        public BanLog(UUID uuid) {
            this.uuid = uuid;
            this.bannedBy = "";
            this.reason = "";
            this.timestamp = 0L;
            this.duration = 0L;
        }

        public UUID getUUID() {
            return uuid;
        }

        public String getBannedBy() {
            return bannedBy;
        }

        public void setBannedBy(String bannedBy) {
            this.bannedBy = bannedBy;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public void push() throws SQLException {
            PreparedStatement statement = Main.getInstance().getDatabaseHandler().getConnection().prepareStatement("INSERT INTO `BansArchive` VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, this.getUUID().toString());
            statement.setString(2, this.getBannedBy());
            statement.setString(3, this.getReason());
            statement.setLong(4, this.getTimestamp());
            statement.setLong(5, this.getDuration());
            Main.getInstance().getDatabaseHandler().executeUpdate(statement);
        }
    }

    public static class BanInformation {

        private UUID uuid;
        private String bannedBy;
        private String reason;
        private long timestamp;
        private long duration;
        private boolean permanently;

        public BanInformation(UUID uuid, String bannedBy, String reason, long timestamp, long duration) {
            this.uuid = uuid;
            this.bannedBy = bannedBy;
            this.reason = reason;
            this.timestamp = timestamp;
            this.duration = duration;
            this.permanently = duration == -1L ? true : false;
        }

        public UUID getUUID() {
            return uuid;
        }

        public String getBannedBy() {
            return bannedBy;
        }

        public String getReason() {
            return reason;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getDuration() {
            return duration;
        }

        public boolean isPermanently() {
            return permanently;
        }
    }
}
