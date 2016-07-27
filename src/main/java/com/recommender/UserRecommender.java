package com.recommender;
import com.recommender.similarity.UserTreeComparison;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
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
 * Created by Usuario on 26/07/2016.
 */
public class UserRecommender implements CustomRecommender{

    public class UserRecommenderBuilder implements RecommenderBuilder {

        RecommenderIRStatsEvaluator evaluator = new GenericRecommenderIRStatsEvaluator();


        public Recommender buildRecommender(DataModel dataModel) throws TasteException {
            UserSimilarity similarity = new UserTreeComparison();
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, 0.5, similarity, dataModel,0.5);
            return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
        }
    }


    private IRStatistics stats;
    private RecommenderBuilder builder;
    private RecommenderIRStatsEvaluator evaluator;
    private DataModel dmodel;

    public UserRecommender(DataModel model){
        this.dmodel = model;
        this.builder = new UserRecommenderBuilder();
        this.evaluator = new GenericRecommenderIRStatsEvaluator();
    }

    public void evaluate() throws TasteException {
        this.stats = this.evaluator.evaluate(this.builder, null, this.dmodel, null, 5, 0.7, 0.8);
        System.out.format("The recommender precision is %f%n", stats.getPrecision());
        System.out.format("The recommender recall is %f%n", stats.getRecall());
        System.out.format("The recommender F1 is %f%n", stats.getF1Measure());
    }
}
