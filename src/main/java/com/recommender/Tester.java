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
        FileWriter ratings = new FileWriter("ratings.csv", true);
        DataCenter.getInstance().load("Los Angeles", 30,34.0554061333333,-118.245262816667);
        HashMap<Long, User> users = DataCenter.getInstance().getUsers();
        for( Map.Entry<Long, User> entry : users.entrySet()  ){
            PreferenceAwareCandidateSelection pacs = new PreferenceAwareCandidateSelection("Los Angeles",34.0554061333333,-118.245262816667, 15, entry.getKey() );
            pacs.calculateRatings();
            HashMap<Long, Double> hRatings = pacs.getUserRatings();
            for(  Map.Entry<Long, Double> rEntry : hRatings.entrySet() ){
                String line = entry.getKey() + "," + rEntry.getKey() + "," + rEntry.getValue();
                ratings.write(line + "\n");
                ratings.flush();
            }

        }
        logger.debug("FINISHED!!!");
        ratings.close();
    }

}
