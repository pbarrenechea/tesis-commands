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
 * Created by Usuario on 11/07/2016.
 */
public class PopulateRatings implements Command {

    private static final Logger logger = LogManager.getLogger(PopulateRatings.class);

   private final String queryRatings = "select t.id_user,v.id, (case when count(1) > 5.0 then 5.0 else count(1)  END) as total from tips t\n" +
           "inner join venue v on (t.id_venue=v.venue_id)\n" +
           "where v.city = '_CITY_' " +
           "group by t.id_user, v.id;";

   private DbConnector db;

   private String city = "";

   public PopulateRatings(String param){
       this.db = new PostgresConnector();
       this.city = param;
   }

   public void run() throws SQLException, UnirestException, IOException {
       FileWriter csv = new FileWriter(this.city.toLowerCase().replace(" ","_") + ".csv", true);
       String selectQuery = queryRatings.replace("_CITY_", this.city);
        db.connect();
        db.executeQuery(selectQuery);
        ResultSet ratings = db.getLastResults();
        while( ratings.next() ){
            long userId = ratings.getLong("id_user");
            long venueId = ratings.getLong("id");
            int rating = ratings.getInt("total");
            String line = "";
            line += userId + "," + venueId + ",";
            line += (rating > 5) ? 5 : rating;
            csv.write(line + "\n");
            csv.flush();
        }
       csv.close();
   }
}
