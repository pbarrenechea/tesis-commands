package com.recommender;
import com.commands.command.Command;
import com.commands.command.CommandFactory;
import com.recommender.dataStructures.TreesLoader;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class SampleRecommender {

	private static final Logger logger = LogManager.getLogger(SampleRecommender.class);
	private static Scanner user_input;
	private static CustomRecommender recommender;

	public static void main(String[] args) throws IOException, TasteException, SQLException {
		System.out.println("Enter recommender: ");
		user_input = new Scanner( System.in ); 

		SampleRecommender sample = new SampleRecommender();
		//Baseline
		//sample.runRecommenderAndEvaluation("New York","newyorkbaseline.csv");

		/***** Sentiment tests *****/
		//sample.runRecommenderAndEvaluation("Los Angeles", "los_angeles_sentiment.csv");
        sample.runRecommenderAndEvaluation("New York", "newyork_inferred_sentiment.csv");
    }
	
	public void runRecommenderAndEvaluation(String city,String file) throws SQLException, IOException, TasteException{
		TreesLoader.getInstance().load(city);
		BasicConfigurator.configure();
		DataModel model = new FileDataModel(new File(file));
		String rec;
		rec = user_input.next( );
		recommender = RecommenderFactory.getInstance().create(rec, model);
		recommender.evaluateAVG();
	}

}
