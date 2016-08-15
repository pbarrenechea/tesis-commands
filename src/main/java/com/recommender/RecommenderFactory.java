package com.recommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;

/**
 * Created by Usuario on 27/07/2016.
 */
public class RecommenderFactory {

    private static RecommenderFactory instance;
    protected RecommenderFactory(){}

    public static RecommenderFactory getInstance(){
        if( instance  == null ){
            instance = new RecommenderFactory();
        }
        return instance;
    }

    public CustomRecommender create(String str, DataModel data) throws TasteException {
        CustomRecommender newCustomRecommender = null;
        if (str.equals("User")) {
            newCustomRecommender = new UserRecommender(data);
        } else if (str.equals("Matrix")) {
            newCustomRecommender = new MatrixFactorizationRecommender(data);
        }
        return newCustomRecommender;
    }
}
