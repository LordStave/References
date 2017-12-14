package de.lordstave.mongostats.cache;

import com.mongodb.MongoException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import de.lordstave.mongostats.Main;
import de.lordstave.mongostats.database.DatabaseUpdate;
import org.bson.Document;

import java.text.DecimalFormat;
import java.util.UUID;

public class PlayerStats extends DatabaseUpdate {

    private final UUID uuid;
    private int kills, deaths;
    private final double kd;

    public PlayerStats(UUID uuid) {
        this.uuid = uuid;
        this.kills = 0;
        this.deaths = 0;
        this.kd = 0;
        this.loadData();
    }

    public UUID getUUID() {
        return uuid;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        Main.getInstance().getDatabaseHandler().executeAsync(() -> {
            Document update = new Document("UUID", this.getUUID().toString());
            Document update_operation = new Document("$set", new Document("Kills", this.getKills()));

            Main.getInstance().getDatabaseHandler().getStatsCollection().findOneAndUpdate(update, update_operation);
            Main.getInstance().getDatabaseHandler().close(null, update, update_operation);
        });
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        Main.getInstance().getDatabaseHandler().executeAsync(() -> {
            Document update = new Document("UUID", this.getUUID().toString());
            Document update_operation = new Document("$set", new Document("Deaths", this.getDeaths()));

            Main.getInstance().getDatabaseHandler().getStatsCollection().findOneAndUpdate(update, update_operation);
            Main.getInstance().getDatabaseHandler().close(null, update, update_operation);
        });
    }

    public String getKD() {
        if(kills <= 0) {
            return "0";
        }
        if(deaths <= 0) {
            return String.valueOf(kills);
        }
        DecimalFormat decimal = new DecimalFormat("#.00");
        return decimal.format(kills / deaths);
    }

    private void loadData() {
        Main.getInstance().getDatabaseHandler().executeAsync(() -> {
            try {
                MongoCursor<Document> cursor = Main.getInstance().getDatabaseHandler().getStatsCollection().find(Filters.eq("UUID", this.getUUID().toString())).iterator();
                Document values = null;
                Document insert = new Document("UUID", this.getUUID().toString())
                        .append("Kills", this.getKills())
                        .append("Deaths", this.getDeaths());

                if(cursor.hasNext()) {
                    values = cursor.next();
                    this.kills = values.getInteger("Kills");
                    this.deaths = values.getInteger("Deaths");
                } else {
                    Main.getInstance().getDatabaseHandler().getStatsCollection().insertOne(insert);
                }
                Main.getInstance().getDatabaseHandler().close(null, values, insert);
                this.setReady(true);
            } catch(MongoException exception) {
                exception.printStackTrace();
            }
        });
    }
}
