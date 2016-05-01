package com.commands.command;

import com.commands.categories.CategoryItem;
import com.commands.dbConnector.DbConnector;
import com.commands.dbConnector.PostgresConnector;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Pablo on 5/1/2016.
 */
public class CategoryVenueMapper implements Command{

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);
    private DbConnector db;
    private HashMap<String, Long> categoriesMap = new HashMap<String, Long>();
    private final String iCategoryVenue = "INSERT INTO venue_category (venue_id, category) values(_VENUE_, _CATEGORY_)";

    public CategoryVenueMapper(){
        db = new PostgresConnector();
        db.connect();
        try {
            db.executeQuery("SELECT * FROM category");
            ResultSet itemsToCalculate = db.getLastResults();
            while( itemsToCalculate.next() ){
                Long id = Long.valueOf(itemsToCalculate.getLong("id"));
                String hash_id = itemsToCalculate.getString("hash_id");
                categoriesMap.put(hash_id, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void run() throws SQLException, UnirestException {
        db.executeQuery("SELECT * FROM venue");
        ResultSet venues = db.getLastResults();
        while( venues.next() ){
            String rawCategories = venues.getString("category_id");
            long idVenue = venues.getLong("id");
            if( rawCategories != null && !rawCategories.isEmpty()  ){
                String[] arrCategories = rawCategories.split(",");
                for( int i = 0; i < arrCategories.length; i++ ){
                    Long numericId = categoriesMap.get(arrCategories[i]);
                    String insertVenueCategory = iCategoryVenue.replace("_VENUE_",String.valueOf(idVenue));
                    insertVenueCategory = insertVenueCategory.replace("_CATEGORY_", numericId.toString());
                    logger.debug(insertVenueCategory);
                    db.executeUpdate(insertVenueCategory);
                }
            }
        }
    }
}
