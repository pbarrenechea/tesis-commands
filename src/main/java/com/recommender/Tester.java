package com.recommender;

import com.recommender.dataStructures.DataCenter;
import com.recommender.dataStructures.User;
import com.recommender.selection.PreferenceAwareCandidateSelection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Usuario on 10/07/2016.
 */
public class Tester {

    private static final Logger logger = LogManager.getLogger(Tester.class);

    public static void main(String[] args) throws SQLException, TasteException, IOException {
        FileWriter ratings = new FileWriter("newyork_inferred_sentiment.csv", true);
        DataCenter.getInstance().load("New York", 40,40.724494,-73.994401);
        HashMap<Long, User> users = DataCenter.getInstance().getUsers();
        for( Map.Entry<Long, User> entry : users.entrySet()  ){
            PreferenceAwareCandidateSelection pacs = new PreferenceAwareCandidateSelection("New York",40.724494,-73.994401,40, entry.getKey() );
            pacs.calculateRatings(true);
            HashMap<Long, Double> hRatings = pacs.getUserRatings();
            for(  Map.Entry<Long, Double> rEntry : hRatings.entrySet() ){
                String line;
                if( rEntry.getValue() > 5.0 ){
                    line = entry.getKey() + "," + rEntry.getKey() + ",5.0";
                }else{
                    line = entry.getKey() + "," + rEntry.getKey() + "," + rEntry.getValue();
                }
                ratings.write(line + "\n");
                ratings.flush();
            }

        }
        logger.debug("FINISHED!!!");
        ratings.close();
    }

}
