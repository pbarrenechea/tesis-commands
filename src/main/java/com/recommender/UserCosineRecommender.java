package com.recommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 * Created by Usuario on 04/01/2017.
 */
public class UserCosineRecommender extends UserRecommender{

    public class CosineRecommenderBuilder implements RecommenderBuilder {

        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();

        public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(1000, 0.5, similarity, dataModel,0.5);
            return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        }
    }

    public UserCosineRecommender(DataModel model){
        super(model);
        this.builder = new CosineRecommenderBuilder();
        this.evaluator = new GenericRecommenderIRStatsEvaluator();
    }

}
