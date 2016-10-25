package com.recommender;
import com.recommender.similarity.UserTreeComparison;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Created by Usuario on 26/07/2016.
 */
public class UserRecommender extends CustomRecommender{

    public class UserRecommenderBuilder implements RecommenderBuilder {

        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();

        public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            //UserSimilarity similarity = new UserTreeComparison();
        	UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(1000, 0.5, similarity, dataModel,0.5);
            return new  GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        }
    }

    public UserRecommender(DataModel model){
        this.dmodel = model;
        this.builder = new UserRecommenderBuilder();
        this.evaluator = new GenericRecommenderIRStatsEvaluator();
    }

}
