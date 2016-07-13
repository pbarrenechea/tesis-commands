package com.recommender;
import com.recommender.similarity.UserTreeComparison;
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
		UserSimilarity similarity = new UserTreeComparison();
		//PearsonCorrelationSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
		UserNeighborhood neighborhood;
		 //neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
		neighborhood = new NearestNUserNeighborhood(15, 0.7, similarity, dataModel,0.9);
		/*try {
			neighborhood = new MyNeighborhood(similarity, dataModel);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.exit(0);
			e.printStackTrace();
		}*/
		
		return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
	}

}
