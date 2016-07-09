package com.commands.command;

import com.commands.dbConnector.DbConnector;
import com.commands.dbConnector.PostgresConnector;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by Usuario on 08/07/2016.
 */
public class CategoryCityWeight  implements Command{

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);

    private final String queryCategoryCityChekins = "select * from city_category_total";
    private final String queryUserCategoryCityTotals = "select * from user_city_category where city = '_CITY_' AND category = _CID_";
    private final String insertCityCategoryUserPreference = "insert into user_city_category_scores (city, category_id,user_id, score) values ('_CITY_', _CID_, _UID_, _VALUE_)";
    private DbConnector db;

    HashMap<String, Long> cityChekins = new HashMap<String, Long>();

    public CategoryCityWeight(){
        db = new PostgresConnector();
        db.connect();
        try {
            db.executeQuery(queryCategoryCityChekins);
            ResultSet cityCheckinsRS = db.getLastResults();
            while( cityCheckinsRS.next() ){
                String city = cityCheckinsRS.getString("city");
                String category = String.valueOf(cityCheckinsRS.getLong("category"));
                Long totals = cityCheckinsRS.getLong("total");
                cityChekins.put(city + "__" + category, totals);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void run() throws SQLException, UnirestException {
        for (Map.Entry<String, Long> entry : cityChekins.entrySet()) {
            String key = entry.getKey();//composed value city + category
            Long total = entry.getValue();
            String[] keyParts = key.split("__");
            String city = keyParts[0];
            String category = keyParts[1];
            String queryCityCategories = queryUserCategoryCityTotals.replace("_CITY_", city);
            queryCityCategories = queryCityCategories.replace("_CID_", category);
            db.executeQuery(queryCityCategories);
            ResultSet cityCategoriesToCalculate = db.getLastResults();
            while( cityCategoriesToCalculate.next() ){
                Long categoryCityTotal = cityCategoriesToCalculate.getLong("total");
                Long user_id = cityCategoriesToCalculate.getLong("id_user");

                double idf = Math.log( total/categoryCityTotal.longValue() );
                double tfidf = idf * categoryCityTotal.longValue();
                String qInsert = insertCityCategoryUserPreference.replace("_UID_", user_id.toString());
                qInsert = qInsert.replace("_CID_", category.toString());
                qInsert = qInsert.replace("_CITY_", city);
                qInsert = qInsert.replace("_VALUE_",String.valueOf(tfidf));
                logger.debug(qInsert);
                db.executeUpdate(qInsert);
            }
        }
    }
}
