package com.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.util.List;

/**
 * Created by Usuario on 26/07/2016.
 */
public class MatrixFactorizationRecommender extends CustomRecommender {

    private SVDPlusPlusFactorizer factorizer;

    public MatrixFactorizationRecommender(DataModel model) throws TasteException {
        this.dmodel = model;
        this.factorizer =  new SVDPlusPlusFactorizer(model, 10, 5);
        this.builder = new RecommenderBuilder(){
            public Recommender buildRecommender(DataModel model) throws TasteException {
                return new SVDRecommender(model,factorizer);
            }
        };
        this.evaluator = new GenericRecommenderIRStatsEvaluator();
    }


    @Override
    public List<RecommendedItem> recommend(long userId) throws TasteException {
        return null;
    }
}
