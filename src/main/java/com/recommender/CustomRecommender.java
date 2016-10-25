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
		System.out.println("RMSE metrics: " + result);
		RecommenderEvaluator mae = new AverageAbsoluteDifferenceRecommenderEvaluator();
		result = mae.evaluate(this.builder, null, this.dmodel, 0.7, 0.3);
		System.out.println("MAE metrics: " + result);
	}
	
	private int iterations=10;

	public void evaluateAVG() throws TasteException {
		double rmseAcum=0;
		double maeAcum=0;
		double precisionAcum=0;
		double recallAcum=0;
		double rmseAVG=0;
		double maeAVG=0;
		double precisionAVG=0;
		double recallAVG=0;
		for (int i = 1; i <= iterations; i++) {
			this.stats = this.evaluator.evaluate(this.builder, null, this.dmodel, null, 5, 0.7, 0.8);
			//System.out.format("The recommender precision is %f%n", stats.getPrecision());
			precisionAcum+=stats.getPrecision();
			recallAcum+=stats.getRecall();
			//System.out.format("The recommender recall is %f%n", stats.getRecall());
			//System.out.format("The recommender F1 is %f%n", stats.getF1Measure());
			RecommenderEvaluator rmse = new RMSRecommenderEvaluator();
			double result = rmse.evaluate(this.builder, null, this.dmodel, 0.7, 0.3);
			//System.out.println("RMSE metrics: " + result);
			rmseAcum+=result;
			RecommenderEvaluator mae = new AverageAbsoluteDifferenceRecommenderEvaluator();
			result = mae.evaluate(this.builder, null, this.dmodel, 0.7, 0.3);
			//System.out.println("MAE metrics: " + result);
			maeAcum+=result;
		}
		rmseAVG=rmseAcum/iterations;
		maeAVG=maeAcum/iterations;
		precisionAVG=precisionAcum/iterations;
		recallAVG=recallAcum/iterations;
		System.out.println("MAE AVG: " + maeAVG);
		System.out.println("RMSE AVG: " + rmseAVG);
		System.out.println("Precision AVG: " + precisionAVG);
		System.out.println("Recall AVG: " + recallAVG);
	}
}
