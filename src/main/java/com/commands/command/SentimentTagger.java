package com.commands.command;

import com.commands.dbConnector.DbConnector;
import com.commands.dbConnector.PostgresConnector;
import com.commands.sentiment.SentimentCalculator;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Pablo on 3/20/2016.
 */
public class SentimentTagger implements Command {

    private static final Logger logger = LogManager.getLogger(SentimentTagger.class);

    private final String uncalculatedQuery = "SELECT * FROM proccessed_text WHERE sentiment = 99 ";

    private final String updateSentimentQuery = "UPDATE proccessed_text SET sentiment = __SENTIMENT__ where id = __ID__  ";

    private final int itemWindow = 5;

    private HashMap<String, Integer> sentimentMap = new HashMap<String, Integer>();

    public SentimentTagger(){
        sentimentMap.put("Positive", 1);
        sentimentMap.put("Negative", -1);
        sentimentMap.put("Neutral", 0);
    }

    public void run() {
        DbConnector db = new PostgresConnector();
        db.connect();
        try {
            db.executeQuery(uncalculatedQuery);
            ResultSet itemsToCalculate = db.getLastResults();
            int calculatedItems = 0;
            while( itemsToCalculate.next() ){
                logger.info( "Calculating sentiment for: " + itemsToCalculate.getString("entry_text") );
                String qUpdate = updateSentimentQuery.replace("__ID__", Long.toString(itemsToCalculate.getLong("id")));
                try {
                    String resultSentiment = SentimentCalculator.calculate(itemsToCalculate.getString("entry_text"));
                    qUpdate = qUpdate.replace("__SENTIMENT__", sentimentMap.get(resultSentiment).toString());
                    logger.info("Executing query: " + qUpdate);
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

    }
}
