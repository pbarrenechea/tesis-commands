package com.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

/**
 * Created by Usuario on 26/07/2016.
 */
public class MatrixFactorizationRecommender implements CustomRecommender {

    /**
     * Inner class with the implementation of the recommender builder
     */
    public class MatrixFactorizationRecommenderBuilder implements RecommenderBuilder {


        public MatrixFactorizationRecommenderBuilder(){}

        public Recommender buildRecommender(DataModel model) throws TasteException {
            ALSWRFactorizer factorizer = new ALSWRFactorizer(model, 5, 0.05, 10);
            SVDPlusPlusFactorizer factorizer2 = new SVDPlusPlusFactorizer(model, 10, 5);
            return new SVDRecommender(model,factorizer2);
        }
    }

    private RecommenderEvaluator evaluator;
    private DataModel dmodel;
    private RecommenderBuilder builder;

    public MatrixFactorizationRecommender(DataModel model){
        this.dmodel = model;
        this.builder = new MatrixFactorizationRecommenderBuilder();
        this.evaluator = new RMSRecommenderEvaluator();
    }

    public void evaluate() throws TasteException {

        double result = evaluator.evaluate(this.builder, null, this.dmodel, 0.8, 0.2);
        System.out.println( "RMSE metrics: " + result);
    }
}
