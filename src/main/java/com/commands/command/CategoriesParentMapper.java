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
import java.util.Map;

/**
 * Created by Pablo on 5/1/2016.
 */
public class CategoriesParentMapper implements Command{

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);
    private DbConnector db;
    private final String updateQuery = "UPDATE category SET parent_category = _PARENT_ID_ where id = _ID_";
    private HashMap<String, CategoryItem> parentMap = new HashMap<String, CategoryItem>();

    public CategoriesParentMapper(){
        db = new PostgresConnector();
        db.connect();
        try {
            db.executeQuery("SELECT * FROM category");
            ResultSet itemsToCalculate = db.getLastResults();
            while( itemsToCalculate.next() ){
                Long id = Long.valueOf(itemsToCalculate.getLong("id"));
                String hash_id = itemsToCalculate.getString("hash_id");
                String parent_hash_id = itemsToCalculate.getString("parent_hash_id");
                CategoryItem ci = new CategoryItem();
                ci.setId(id);
                ci.setParentHashId(parent_hash_id);
                parentMap.put(hash_id, ci);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void run() throws SQLException, UnirestException {
        for (Map.Entry<String, CategoryItem> entry : parentMap.entrySet()) {
            String key = entry.getKey();
            CategoryItem value = (CategoryItem) entry.getValue();
            CategoryItem parent = parentMap.get(value.getParentHashId());
            if( parent != null ){
                String uParentQuery = updateQuery.replace("_PARENT_ID_", parent.getId().toString());
                uParentQuery = uParentQuery.replace("_ID_", value.getId().toString());
                logger.debug(uParentQuery);
                db.executeUpdate(uParentQuery);
            }
        }
    }
}
