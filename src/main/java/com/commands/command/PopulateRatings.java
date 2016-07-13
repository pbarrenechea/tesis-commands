package com.commands.command;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Usuario on 11/07/2016.
 */
public class PopulateRatings implements Command {

    private static final Logger logger = LogManager.getLogger(PopulateRatings.class);

   private final String queryRatings = "select t.id_user, v.id, count(1) as total\n" +
           "from tips t\n" +
           "inner join venue v on ( v.id = t.venue_id )\n" +
           "group by t.id_user, v.id\n" +
           "order by total DESC;";

   private final String qInsertRating = "insert into ratings2 (user_id, venue_id, rating) values ";

   private DbConnector db;

   public PopulateRatings(){
       this.db = new PostgresConnector();
   }

    public void run() throws SQLException, UnirestException {
        db.connect();
        db.executeQuery(queryRatings);
        ResultSet ratings = db.getLastResults();
        while( ratings.next() ){
            long userId = ratings.getLong("id_user");
            long venueId = ratings.getLong("id");
            int rating = ratings.getInt("total");
            String qInsert = qInsertRating + "( ";
            qInsert += userId + ", ";
            qInsert += venueId + ", ";
            qInsert += rating + " );";
            logger.debug(qInsert);
            db.executeUpdate(qInsert);
        }
    }
}
