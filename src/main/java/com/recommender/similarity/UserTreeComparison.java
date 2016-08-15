package com.recommender.similarity;

import com.recommender.dataStructures.TreesLoader;
import com.recommender.dataStructures.UserCategoryNode;
import com.recommender.dataStructures.UserCategoryTree;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.similarity.PreferenceInferrer;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.util.Collection;
import java.util.List;

/**
 * Created by Usuario on 10/07/2016.
 */
public class UserTreeComparison implements UserSimilarity{

    private static final Logger logger = LogManager.getLogger(UserTreeComparison.class);

    private double levelSimilarity(int level, UserCategoryTree user1Tree, UserCategoryTree user2Tree ){
        double result = 0.0;
        List<UserCategoryNode> user1LevelNodes = user1Tree.getLevel(level);
        List<UserCategoryNode> user2LevelNodes = user2Tree.getLevel(level);
        for(UserCategoryNode tmpUser1 : user1LevelNodes  ){
            for( UserCategoryNode tmpUser2 : user2LevelNodes ){
                if( tmpUser1.getId() == tmpUser2.getId() ){//check if the categories are the same
                    result += Math.min(tmpUser1.getScore(), tmpUser2.getScore() );
                }
            }
        }
        return result;
    }

    public double userSimilarity(long user1, long user2) throws TasteException {
        UserCategoryTree user1Tree = TreesLoader.getInstance().getUserTree(new Long(user1));
        UserCategoryTree user2Tree = TreesLoader.getInstance().getUserTree(new Long(user2));
        long levels = ( user1Tree != null ) ? user1Tree.getLevels() : 0 ;
        double result = 0.0;
        for( int i = 0 ; i < levels; i++  ){
            if( user1Tree != null && user2Tree != null ){
                double beta = Math.pow(2, i+1);
                double currentLevelSimilarity = this.levelSimilarity(i, user1Tree, user2Tree);
                double entropyDistance = 1 + Math.abs(user1Tree.getLevelEntrophy(i) - user2Tree.getLevelEntrophy(i));
                result += beta * (  currentLevelSimilarity / entropyDistance );
            }
        }
        //logger.debug("Similarity between "+ user1 + " and " + user2 +  " is " + result);
        return result;
    }

    public void setPreferenceInferrer(PreferenceInferrer preferenceInferrer) {

    }

    public void refresh(Collection<Refreshable> collection) {

    }

    public UserTreeComparison(){}
}
