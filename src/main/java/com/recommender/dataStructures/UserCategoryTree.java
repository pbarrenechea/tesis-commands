package com.recommender.dataStructures;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Usuario on 09/07/2016.
 */
public class UserCategoryTree {

    private static final Logger logger = LogManager.getLogger(UserCategoryTree.class);

    private HashMap<Integer, List<UserCategoryNode>> levelNodes;

    private long totalChekins = 0;

    public long getTotalChekins() {
        return totalChekins;
    }

    public void setTotalChekins(long totalChekins) {
        this.totalChekins = totalChekins;
    }

    public UserCategoryTree(){
        this.levelNodes = new HashMap<Integer, List<UserCategoryNode>>();
    }

    public void add( UserCategoryNode node, int level ){
        List<UserCategoryNode> currentLevelNodes = this.levelNodes.get(new Integer(level));
        if( currentLevelNodes == null ){
            currentLevelNodes = new ArrayList<UserCategoryNode>();
            this.levelNodes.put(new Integer(level), currentLevelNodes);
        }
        currentLevelNodes.add(node);
    }

    public long getLevels(){
        return levelNodes.size();
    }


    public List<UserCategoryNode> getLevel(int level){
        List<UserCategoryNode> result = this.levelNodes.get(new Integer(level));
        if (result != null){
            return result;
        }
        return new ArrayList<UserCategoryNode>();
    }

    private long getLevelTotals(int level){
        List<UserCategoryNode> currentLevelNodes = levelNodes.get(new Integer(level));
        long result = 0;
        if( currentLevelNodes != null ){
            for( UserCategoryNode tmp : currentLevelNodes ){
                result += tmp.getNumberChekins();
            }
        }
        return result;
    }

    public double getLevelEntrophy(int level){
        double entropy = 0.0;
        List<UserCategoryNode> currentLevel = levelNodes.get(new Integer(level));
        if( currentLevel != null ){
            for( UserCategoryNode node: currentLevel ){
                if (totalChekins==0)
                    logger.error("DIVISION ZERO");

                double likelihood = (double) node.getNumberChekins() / (double) totalChekins;
                //logger.error( node.getNumberChekins());
                entropy += likelihood * Math.log(likelihood);
            }
        }
        return entropy*(-1);
    }

    public void printTree(){
        for( int i = 0; i < this.levelNodes.size(); i++ ){
            List<UserCategoryNode> currentLevelNodes = this.levelNodes.get(new Integer(i));
            logger.debug("Level -> " + i);
            logger.debug("Categories: ");
            if( currentLevelNodes != null ){
                for( UserCategoryNode tmp : currentLevelNodes  ){
                    logger.debug( tmp.getId() + " :: " + tmp.getName() + " :: " + tmp.getNumberChekins() );
                }
            }
        }
    }
}
