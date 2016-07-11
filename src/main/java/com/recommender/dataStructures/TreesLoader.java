package com.recommender.dataStructures;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Usuario on 10/07/2016.
 */
public class TreesLoader {

    private static TreesLoader instance;

    private static final String queryChekins = "select * from user_category_city_chekins ucc\n" +
            "inner join user_city_category_scores uccs on\n" +
            "( ucc.id_user = uccs.user_id and ucc.category = uccs.category_id and ucc.city = uccs.city )\n" +
            "inner join category c on ( ucc.category = c.id )\n" +
            "where  ucc.city = '_CITY_'";

    private static final String queryTotalCheckinsByUser = "select id_user,count(*) as total from tips t\n" +
            "  inner join venue v on (t.id_venue=v.venue_id)\n" +
            "  where v.city= '_CITY_'\n" +
            "  group by t.id_user";

    private DbConnector db;

    HashMap<Long, UserCategoryTree> userTrees;

    public static TreesLoader getInstance(){
        if( instance  == null ){
            instance = new TreesLoader();
        }
        return instance;
    }
    private TreesLoader(){
        this.userTrees = new HashMap<Long, UserCategoryTree>();
        db = new PostgresConnector();
    }

    public void load(String city) throws SQLException {
        String queryChekinsToLoad = queryChekins.replace("_CITY_", city);
        String queryTotalCheckinsByUser = queryChekins.replace("_CITY_", city);
        db.connect();
        db.executeQuery(queryChekinsToLoad);
        ResultSet userCategoryCitySet = db.getLastResults();
        while( userCategoryCitySet.next() ){
            Long idUser = new Long(userCategoryCitySet.getLong("id_user"));
            long idCategory = userCategoryCitySet.getLong("category");
            long checkins = userCategoryCitySet.getLong("total");
            double score = userCategoryCitySet.getDouble("score");
            String categoryName = userCategoryCitySet.getString("name");
            int level = userCategoryCitySet.getInt("level");
            UserCategoryNode node = new UserCategoryNode(idCategory, categoryName, score, checkins );
            UserCategoryTree currentUserTree = userTrees.get(idUser);
            if( currentUserTree == null ){
                currentUserTree = new UserCategoryTree();
                userTrees.put(idUser, currentUserTree);
            }
            currentUserTree.add(node, level);

        }
        //Load total checkins by user
        db.executeQuery(queryTotalCheckinsByUser);

        ResultSet checkinsByUser = db.getLastResults();
        while( checkinsByUser.next() ){
            Long idUser = new Long(checkinsByUser.getLong("id_user"));
            long count = checkinsByUser.getLong("total");
            UserCategoryTree currentUserTree = userTrees.get(idUser);
            if (currentUserTree!=null)
            currentUserTree.setTotalChekins(count);
        }

    }

    public UserCategoryTree getUserTree(Long idUser){
        return this.userTrees.get(idUser);
    }

}
