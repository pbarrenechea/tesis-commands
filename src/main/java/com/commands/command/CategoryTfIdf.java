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
 * Created by Pablo on 5/1/2016.
 */
public class CategoryTfIdf implements Command {

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);

    private static final String queryCategoriesTotals = "select  c.id, count(*) as totals\n" +
            "from category c\n" +
            "INNER JOIN venue_category vc on ( vc.category = c.id )\n" +
            "INNER JOIN tips t on ( t.venue_id = vc.venue_id)\n" +
            "group by c.id\n" +
            "order by totals desc";

    private static final String queryTotals = "select count(*) as totals\n" +
            "from category c\n" +
            "INNER JOIN venue_category vc on ( vc.category = c.id )\n" +
            "INNER JOIN tips t on ( t.venue_id = vc.venue_id)";

    private long categoriesTotals = 0;
    private DbConnector db;
    private HashMap<Long, Long> categoriesTotalsMap = new HashMap<Long, Long>();

    public CategoryTfIdf() throws SQLException {
        db = new PostgresConnector();
        db.connect();
        db.executeQuery(queryTotals);
        ResultSet totalsResultSet = db.getLastResults();
        while( totalsResultSet.next() ){
            categoriesTotals = totalsResultSet.getLong("totals");
        }
        db.executeQuery(queryCategoriesTotals);
        ResultSet categoriesTotalsResultSet = db.getLastResults();
        while( categoriesTotalsResultSet.next() ){
            Long categoryId = new Long( categoriesTotalsResultSet.getLong("id") );
            Long totalCategory = new Long(categoriesTotalsResultSet.getLong("totals"));
            categoriesTotalsMap.put(categoryId, totalCategory);
        }
    }

    public void run() throws SQLException, UnirestException {
        for (Map.Entry<Long, Long> entry : categoriesTotalsMap.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            double idf = Math.log(categoriesTotals / value.longValue()  );
            double tfIdf = value.longValue() * idf;
            String updateCategory = "UPDATE category set global_weight = " + tfIdf + " where id = " + key;
            logger.debug(updateCategory);
            db.executeUpdate(updateCategory);
        }
    }
}
