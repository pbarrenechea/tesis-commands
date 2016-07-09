package com.recommender;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
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
		
		BasicConfigurator.configure();

		//DataModel model = new FileDataModel(new File("C:/ratings2_fromnyc.csv"));
		DataModel model = new FileDataModel(new File("C:/ratings.csv"));
		RandomUtils.useTestSeed();
		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		 //UserSimilarity similarity = new UncenteredCosineSimilarity(model);
		//UserNeighborhood neighborhood = new NearestNUserNeighborhood(500, similarity, model);
		/*System.out.println(neighborhood.getUserNeighborhood(45).length);
		  UserBasedRecommender recommender = new
		  GenericUserBasedRecommender(model, neighborhood, similarity);
		  List<RecommendedItem> nuevo = recommender.recommend(45, 10); for
		  (RecommendedItem recommendation : nuevo) {
		  System.out.println(recommendation); } System.out.println("END");
		  System.exit(0);*/
		 
		RecommenderBuilder builder = new MyRecommenderBuilder();

		RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();
		IRStatistics stats = evaluator.evaluate(builder, null, model, null, 10, 0.9, 0.8);
		System.out.format("The recommender precision is %f%n", stats.getPrecision());
		System.out.format("The recommender recall is %f%n", stats.getRecall());
		System.out.format("The recommender F1 is %f%n", stats.getF1Measure());
		/*
		 * RecommenderEvaluator evaluator = new
		 * AverageAbsoluteDifferenceRecommenderEvaluator(); RecommenderBuilder
		 * builder = new MyRecommenderBuilder(); double result =
		 * evaluator.evaluate(builder, null, model, 0.8, 1.0); logger.debug(
		 * "Recommendation metrics: " + result); logger.debug("Presicion: " );
		 * 
		 * RecommenderEvaluator rmse = new RMSRecommenderEvaluator(); result =
		 * rmse.evaluate(builder, null, model, 0.8, 1.0); logger.debug(
		 * "RMSE metrics: " + result);
		 */

		/*
		 * RecommenderEvaluator evaluator = new
		 * AverageAbsoluteDifferenceRecommenderEvaluator(); RecommenderBuilder
		 * builder = new MyRecommenderBuilder(); double result =
		 * evaluator.evaluate(builder, null, model, 0.9, 1.0);
		 * System.out.println(result);
		 */

	}

}
