package com.commands.command;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pablo Barrenechea on 5/5/2016.
 */
public class UserCategoriesPreferences implements Command {

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);

    private final String queryUserChekins = "select * from user_checkins";
    private final String queryCategoryUserTotals = "select * from user_category_totals where id_user = _ID_";
    private final String insertUserCategoryPreference = "insert into user_categories_preferences (user_id, category_id, tfidf) values (_UID_, _CID_, _VALUE_)";
    private DbConnector db;

    HashMap<Long, Long> userChekins = new HashMap<Long, Long>();

    public UserCategoriesPreferences(){
        db = new PostgresConnector();
        db.connect();
        try {
            db.executeQuery(queryUserChekins);
            ResultSet userCheckinsRS = db.getLastResults();
            while( userCheckinsRS.next() ){
                Long id = Long.valueOf(userCheckinsRS.getLong("user"));
                Long totals = userCheckinsRS.getLong("sum");
                userChekins.put(id, totals);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void run() throws SQLException, UnirestException {
        for (Map.Entry<Long, Long> entry : userChekins.entrySet()) {
            Long user_id = entry.getKey();
            Long total = entry.getValue();
            String queryCategories = queryCategoryUserTotals.replace("_ID_", user_id.toString());
            db.executeQuery(queryCategories);
            ResultSet categoriesToCalculate = db.getLastResults();
            while( categoriesToCalculate.next() ){
                Long categoryTotal = categoriesToCalculate.getLong("total");
                Long category_id = categoriesToCalculate.getLong("id");
                double idf = Math.log( total/categoryTotal.longValue() );
                double tfidf = idf * categoryTotal.longValue();
                String qInsert = insertUserCategoryPreference.replace("_UID_", user_id.toString());
                qInsert = qInsert.replace("_CID_", category_id.toString());
                qInsert = qInsert.replace("_VALUE_",String.valueOf(tfidf));
                logger.debug(qInsert);
                db.executeUpdate(qInsert);
            }
        }
    }
}
