package com.recommender;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class MyRecommenderBuilder implements RecommenderBuilder {


	public Recommender buildRecommender(DataModel dataModel) throws TasteException {
		UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
		UserNeighborhood neighborhood=null;
		 //neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
		neighborhood =new NearestNUserNeighborhood(100, similarity, dataModel);
		
		/*try {
			neighborhood = new MyNeighborhood(similarity, dataModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.exit(0);
			e.printStackTrace();
		}*/
		
		return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
		//return new MyUserBasedRecommender(dataModel, neighborhood, similarity);
	}

}
