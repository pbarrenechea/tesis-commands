package com.recommender;

import com.recommender.dataStructures.TreesLoader;
import com.recommender.dataStructures.UserCategoryTree;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;

import java.sql.SQLException;

/**
 * Created by Usuario on 10/07/2016.
 */
public class TreesTester {

    private static final Logger logger = LogManager.getLogger(TreesTester.class);

    public static void main(String[] args) throws SQLException, TasteException {
        TreesLoader.getInstance().load("New York");
        UserTreeComparison uc = new UserTreeComparison();
        logger.debug("Similarity -> " + uc.userSimilarity(483731,921687));
        logger.debug("Similarity -> " + uc.userSimilarity(483731,7998813));
        logger.debug("Similarity -> " + uc.userSimilarity(483731,41414));
        logger.debug("Similarity -> " + uc.userSimilarity(483731,728996));
    }

}
