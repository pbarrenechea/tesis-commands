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
 * Created by Usuario on 08/07/2016.
 */
public class CategoryCityWeight  implements Command{

    private static final Logger logger = LogManager.getLogger(CategoryCityWeight.class);

    private final String queryCategoryCityChekins = "select * from city_category_total where city = 'New York'";
    private final String queryUserCategoryCityTotals = "select * from user_city_category where city = '_CITY_' AND category = _CID_";
    private final String queryUserCategoryCitySentiment = "select * from user_city_category_sent where city = '_CITY_' AND category = _CID_";

    private final String insertCityCategoryUserPreference = "insert into user_city_category_scores (city, category_id,user_id, score) values ('_CITY_', _CID_, _UID_, _VALUE_)";
    private final String insertCityCategoryUserSentPrefrence = "insert into user_city_category_sentiment (city, category,id_user, score) values ('_CITY_', _CID_, _UID_, _VALUE_)";

    private static final String usersByCityCheckins="select v.city,count(1) from tips t\n" +
            "  inner join venue v on (t.id_venue=v.venue_id)\n" +
            "  where v.city = 'New York' " +
            "            group by v.city";
    private static final String queryTotalCheckinsByUserByCity = "select id_user,city,sum(total) as total from user_city_category where  city = 'New York' group by id_user,city ";

    private boolean sentiment = false;

    private DbConnector db;

    HashMap<String, Long> cityChekins = new HashMap<String, Long>();

    HashMap<String, Long> totalCityCheckins = new HashMap<String, Long>();
    HashMap<String, Long> totalCheckinsByUserByCity = new HashMap<String, Long>();

    public HashMap<String,Long> loadCityCheckinsHashMap(){
        try {
            db.executeQuery(usersByCityCheckins);
            ResultSet cityCheckins = db.getLastResults();
            while (cityCheckins.next()) {
                String city = cityCheckins.getString("city");
                Long totals = cityCheckins.getLong("count");
                totalCityCheckins.put(city, totals);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCityCheckins;
    }

    public HashMap<String,Long> loadCheckinsByUserByCityHashMap(){
        try {
            db.executeQuery(queryTotalCheckinsByUserByCity);
            ResultSet cityUsersCheckins = db.getLastResults();
            while (cityUsersCheckins.next()) {
                String id_user = cityUsersCheckins.getString("id_user");
                String city = cityUsersCheckins.getString("city");
                Long totals = cityUsersCheckins.getLong("total");
                totalCheckinsByUserByCity.put(id_user+"_"+city, totals);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCheckinsByUserByCity;
    }

    public CategoryCityWeight(boolean s){
        db = new PostgresConnector();
        db.connect();
        this.sentiment = s;
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
        this.loadCheckinsByUserByCityHashMap();
        this.loadCityCheckinsHashMap();

    }

    public void run() throws SQLException, UnirestException {
        for (Map.Entry<String, Long> entry : cityChekins.entrySet()) {
            String key = entry.getKey();//composed value city + category
            Long total = entry.getValue();
            String[] keyParts = key.split("__");
            String city = keyParts[0];
            String category = keyParts[1];
            String queryCityCategories = ( !this.sentiment ) ? queryUserCategoryCityTotals.replace("_CITY_", city) : queryUserCategoryCitySentiment.replace("_CITY_", city) ;
            queryCityCategories = queryCityCategories.replace("_CID_", category);
            db.executeQuery(queryCityCategories);
            ResultSet cityCategoriesToCalculate = db.getLastResults();
            while( cityCategoriesToCalculate.next() ){
                Long categoryCityTotal = (!this.sentiment) ? cityCategoriesToCalculate.getLong("total"): cityCategoriesToCalculate.getLong("cat_sent");
                Long user_id = (!this.sentiment) ? cityCategoriesToCalculate.getLong("id_user") : cityCategoriesToCalculate.getLong("user_id");
                double tf = (double) categoryCityTotal.longValue()/ (double)totalCheckinsByUserByCity.get(user_id + "_" + city);
                double idf = Math.log( totalCityCheckins.get(city)/total );
                double tfidf = tf*idf ;
                String qInsert = (!this.sentiment) ? insertCityCategoryUserPreference.replace("_UID_", user_id.toString()) : insertCityCategoryUserSentPrefrence.replace("_UID_", user_id.toString()) ;
                qInsert = qInsert.replace("_CID_", category.toString());
                qInsert = qInsert.replace("_CITY_", city);
                qInsert = qInsert.replace("_VALUE_",String.valueOf(tfidf));
                System.out.println(qInsert);
                db.executeUpdate(qInsert);
            }
        }
    }
}
