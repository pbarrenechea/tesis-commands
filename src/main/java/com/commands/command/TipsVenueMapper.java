package com.commands.command;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Pablo on 5/1/2016.
 */
public class TipsVenueMapper implements Command {

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);
    private DbConnector db;
    private HashMap<String, Long> venuesMap = new HashMap<String, Long>();
    private final String uVenueTips = "UPDATE tips set venue_id = _VENUE_ where id = _ID_";


    public TipsVenueMapper(){
        db = new PostgresConnector();
        db.connect();
        try {
            db.executeQuery("SELECT * FROM venue");
            ResultSet itemsToCalculate = db.getLastResults();
            while( itemsToCalculate.next() ){
                Long id = Long.valueOf(itemsToCalculate.getLong("id"));
                String hash_id = itemsToCalculate.getString("venue_id");
                venuesMap.put(hash_id, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void run() throws SQLException, UnirestException {
        db.executeQuery("SELECT * FROM tips where venue_id IS NULL");
        ResultSet tips = db.getLastResults();
        while( tips.next() ){
            String oldVenueId = tips.getString("id_venue");
            Long newVenueId = venuesMap.get(oldVenueId);
            Long currentTipId = tips.getLong("id");
            String uUpdateTip = uVenueTips.replace("_VENUE_", newVenueId.toString());
            uUpdateTip = uUpdateTip.replace("_ID_", currentTipId.toString());
            logger.debug(uUpdateTip);
            db.executeUpdate(uUpdateTip);
        }
    }
}
