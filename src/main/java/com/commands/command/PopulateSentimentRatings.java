package com.commands.command;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Usuario on 20/12/2016.
 */
public class PopulateSentimentRatings implements Command {
    private static final Logger logger = LogManager.getLogger(PopulateRatings.class);

    private final String queryRatings = "select t.id_user,v.id, (case when count(1) > 5.0 then 5.0 else count(1)  END) as total,\n" +
            "  sum(((t.sentiment * (t.sentiment_value/10 - 5)) + 5) / 2)/count(1) as sent\n" +
            "from tips t\n" +
            "inner join venue v on (t.id_venue=v.venue_id)\n" +
            "where v.city = '_CITY_'\n" +
            "group by t.id_user, v.id;";

    private final String insertRatings = "insert into sentiment_ratings (user_id, venue_id, sentiment) values (_UID_, _VID_, _SENT_)";

    private DbConnector db;

    private String city = "";

    public PopulateSentimentRatings(String param){
        this.db = new PostgresConnector();
        this.city = param;
    }

    public void run() throws SQLException, UnirestException, IOException {
        //FileWriter csv = new FileWriter(this.city.toLowerCase().replace(" ","_") + "_sentiment.csv", true);
        String selectQuery = queryRatings.replace("_CITY_", this.city);
        db.connect();
        db.executeQuery(selectQuery);
        ResultSet ratings = db.getLastResults();
        while( ratings.next() ){
            long userId = ratings.getLong("id_user");
            long venueId = ratings.getLong("id");
            int totalCheckins = ratings.getInt("total");
            double sent = ratings.getDouble("sent");
          //  String line = "";
          //  line += userId + "," + venueId + ",";
            double sign = Math.signum((double)totalCheckins - sent);
            double heaviside = (Math.abs((double)totalCheckins - sent) - 2 >= 0) ? 1.0 : 0.0;
            double finalRating = totalCheckins - sign * heaviside;
            String valuesToInsert = insertRatings.replace("_UID_", Long.toString(userId));
            valuesToInsert = valuesToInsert.replace("_VID_", Long.toString(venueId));
            valuesToInsert = valuesToInsert.replace("_SENT_", Double.toString(finalRating));
            db.executeUpdate(valuesToInsert);
            //csv.write( line + finalRating  + "\n");
            //csv.flush();
        }
        //csv.close();
    }
}