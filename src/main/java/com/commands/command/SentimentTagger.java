package com.commands.command;

import com.commands.dbConnector.DbConnector;
import com.commands.dbConnector.PostgresConnector;
import com.commands.sentiment.SentimentCalculator;
import com.commands.sentiment.SentimentItem;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pablo on 3/20/2016.
 */
public class SentimentTagger implements Command {

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);

    private final String uncalculatedQuery = "SELECT * FROM tips WHERE sentiment = 99 ";

    private final String updateSentimentQuery = "UPDATE tips SET sentiment = __SENTIMENT__, sentiment_value = __CONFIDENCE__ where id_user = __USERID__  and  id_venue = '__VENUEID__'";

    private final int itemWindow = 30;

    private HashMap<String, Integer> sentimentMap = new HashMap<String, Integer>();

    public SentimentTagger(){
        sentimentMap.put("Positive", 1);
        sentimentMap.put("Negative", -1);
        sentimentMap.put("Neutral", 0);
    }

    public void run() {
        DbConnector db = new PostgresConnector();
        db.connect();
        FileWriter updateBulkQueries = null;
        try {
            updateBulkQueries = new FileWriter("updateScript.sql", true);
            try {
                db.executeQuery(uncalculatedQuery);
                ResultSet itemsToCalculate = db.getLastResults();
                int calculatedItems = 0;
                while( itemsToCalculate.next() ){
                    logger.info( "Calculating sentiment for: " + itemsToCalculate.getString("tip_text") );
                    String qUpdate = updateSentimentQuery.replace("__USERID__", Long.toString(itemsToCalculate.getLong("id_user")));
                    qUpdate = qUpdate.replace("__VENUEID__", itemsToCalculate.getString("id_venue") );
                    try {
                        SentimentItem resultSentiment = SentimentCalculator.calculate(itemsToCalculate.getString("tip_text"));
                        qUpdate = qUpdate.replace("__SENTIMENT__",  String.valueOf(sentimentMap.get(resultSentiment.getSentiment())));
                        qUpdate = qUpdate.replace("__CONFIDENCE__" , String.valueOf(resultSentiment.getAccuracy()));
                        logger.info("Executing query: " + qUpdate);
                        updateBulkQueries.write(qUpdate + ";\n");
                        updateBulkQueries.flush();
                        db.executeUpdate(qUpdate);
                    } catch (UnirestException e) {
                        e.printStackTrace();
                    }
                    calculatedItems++;
                    /**
                     * Sleep the application to avoid API congestion
                     */
                    if( calculatedItems % itemWindow == 0 ){
                        try {
                            TimeUnit.MILLISECONDS.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            updateBulkQueries.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}
