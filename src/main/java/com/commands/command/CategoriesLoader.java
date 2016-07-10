package com.commands.command;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.sql.SQLException;

/**
 * Created by Pablo on 5/1/2016.
 */
public class CategoriesLoader implements Command {


    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);
    private String JSON_PATH = "categories.json";
    private JSONObject entryData;
    private final String insertQuery = "INSERT INTO CATEGORY (name, hash_id,parent_hash_id, level) values ('_NAME_', '_HASH_', '_PARENT_HASH_', _LEVEL_ )";
    private DbConnector db;

    public CategoriesLoader() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        FileInputStream fis = new FileInputStream( new File(classLoader.getResource(JSON_PATH).getFile()));
        InputStreamReader f = new InputStreamReader(fis);
        JSONTokener tokener = new JSONTokener(f);
        entryData = new JSONObject(tokener);
    }

    private void parseCategories(JSONArray categories, String parentId, int level){
        for( int i = 0; i < categories.length(); i++ ){
            String qInsert = insertQuery.replace("_NAME_", (String)((JSONObject) categories.get(i)).get("name"));
            qInsert = qInsert.replace("_PARENT_HASH_", parentId);
            qInsert = qInsert.replace("_HASH_", (String)((JSONObject) categories.get(i)).get("id"));
            qInsert = qInsert.replace("_LEVEL_", String.valueOf(level));
           logger.info("Executing query: " + qInsert);
            try {
                db.executeUpdate(qInsert);
                if( ((JSONObject)categories.get(i)).has("categories") ){
                    logger.debug("Has subcategories, going in depth");
                    JSONArray subCategories = ((JSONObject)categories.get(i)).getJSONArray("categories");
                    String subCategoryParentId = (String)((JSONObject) categories.get(i)).get("id");
                    this.parseCategories(subCategories, subCategoryParentId, level + 1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() throws SQLException, UnirestException {
        logger.debug("Executing categories loader");
        JSONArray categories = entryData.getJSONObject("response").getJSONArray("categories");
        db = new PostgresConnector();
        db.connect();
        this.parseCategories(categories,"", 0);
    }
}
