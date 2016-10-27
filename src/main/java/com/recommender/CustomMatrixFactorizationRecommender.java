package com.recommender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDPlusPlusFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import com.recommender.dataStructures.UsersFeaturesMatrix;
import com.recommender.dataStructures.VenuesFeaturesMatrix;

/**
 * Created by Stroccoli on 26/07/2016.
 */
public class CustomMatrixFactorizationRecommender extends CustomRecommender {

	private MySVDPlusPlusFactorizer factorizer;

	private VenuesFeaturesMatrix vfm;
	private UsersFeaturesMatrix ufm;
	private String city="New York";
	private int level=5;

	public CustomMatrixFactorizationRecommender(DataModel model) throws TasteException {
		this.dmodel = model;
		
		ufm=new UsersFeaturesMatrix(city,level);
		vfm=new VenuesFeaturesMatrix(city,level);
		ufm.initializeUserFeatureMatrix();
		vfm.initializeVenueFeatureMatrix();
		
		ArrayList categories=union(ufm.getCategories(),vfm.getCategories());
		
		this.factorizer = new MySVDPlusPlusFactorizer(model, categories.size(), 5,ufm,vfm,categories);
		
		this.builder = new RecommenderBuilder() {
			public Recommender buildRecommender(DataModel model) throws TasteException {
				return new SVDRecommender(model, factorizer);
			}
		};
		this.evaluator = new GenericRecommenderIRStatsEvaluator();
	}
	
	public ArrayList<Integer> union(ArrayList<Integer> list1, ArrayList<Integer> list2) {
        Set<Integer> set = new HashSet<Integer>();

        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<Integer>(set);
    }

}
