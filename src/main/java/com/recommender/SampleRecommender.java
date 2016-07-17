package com.recommender;

import com.recommender.dataStructures.TreesLoader;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class SampleRecommender {

	private static final Logger logger = LogManager.getLogger(SampleRecommender.class);

	public static void main(String[] args) throws IOException, TasteException, SQLException {
        TreesLoader.getInstance().load("Los Angeles");
		BasicConfigurator.configure();
		DataModel model = new FileDataModel(new File("ratings.csv"));
		RecommenderBuilder builder = new MyRecommenderBuilder();
		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
		IRStatistics stats = evaluator.evaluate(builder, null, model, null, 5, 0.7, 0.8);
		System.out.format("The recommender precision is %f%n", stats.getPrecision());
		System.out.format("The recommender recall is %f%n", stats.getRecall());
		System.out.format("The recommender F1 is %f%n", stats.getF1Measure());
	}

}
