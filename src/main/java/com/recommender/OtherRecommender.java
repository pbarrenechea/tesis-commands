package com.recommender;

import org.apache.log4j.BasicConfigurator;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.File;
import java.io.IOException;

public class OtherRecommender {

    public static void main(String[] args) throws TasteException, IOException {
        BasicConfigurator.configure();

        DataModel model = new FileDataModel(new File("losangelesbaseline.csv"));

        RandomUtils.useTestSeed();

        UserSimilarity similarity;
        similarity = new PearsonCorrelationSimilarity(model);
        RecommenderBuilder builder = new MyRecommenderBuilder();
        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
        IRStatistics stats = evaluator.evaluate(builder, null, model, null, 10, 0.9, 0.8);
        System.out.format("The recommender precision is %f%n", stats.getPrecision());
        System.out.format("The recommender recall is %f%n", stats.getRecall());
        System.out.format("The recommender F1 is %f%n", stats.getF1Measure());
        RecommenderEvaluator rmse = new RMSRecommenderEvaluator();
        double result = rmse.evaluate(builder, null, model, 0.7, 0.3);
        System.out.println( "RMSE metrics: " + result);
        RecommenderEvaluator mae = new AverageAbsoluteDifferenceRecommenderEvaluator();
        result = mae.evaluate(builder, null, model, 0.7, 0.3);
        System.out.println("MAE metrics: " + result);
    }
}
