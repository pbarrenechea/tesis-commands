package com.recommender;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;

/**
 * Created by Usuario on 26/07/2016.
 */
public abstract class CustomRecommender {

    protected RecommenderIRStatsEvaluator evaluator;
    protected DataModel dmodel;
    protected IRStatistics stats;
    protected RecommenderBuilder builder;


    public void evaluate() throws TasteException {
        this.stats = this.evaluator.evaluate(this.builder, null, this.dmodel, null, 5, 0.7, 0.8);
        System.out.format("The recommender precision is %f%n", stats.getPrecision());
        System.out.format("The recommender recall is %f%n", stats.getRecall());
        System.out.format("The recommender F1 is %f%n", stats.getF1Measure());
       RecommenderEvaluator rmse = new RMSRecommenderEvaluator();
        double result = rmse.evaluate(this.builder, null, this.dmodel, 0.7, 0.3);
        System.out.println( "RMSE metrics: " + result);
        RecommenderEvaluator mae = new AverageAbsoluteDifferenceRecommenderEvaluator();
        result = mae.evaluate(this.builder, null, this.dmodel, 0.7, 0.3);
        System.out.println("MAE metrics: " + result);
    }
}
