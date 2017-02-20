package com.rest;
import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;
import com.recommender.CustomRecommender;
import com.recommender.RecommenderFactory;
import com.recommender.dataStructures.TreesLoader;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/recommender")
public class RecommenderService {

    @Context ServletContext context;

    private static final String queryVenues = "select v.id, v.name, c.name as category, v.latitude, v.longitude  from venue v\n" +
            "inner join venue_category vc on vc.venue_id = v.id\n" +
            "inner join category c on c.id = vc.category\n" +
            "where v.id in (_IN_);" ;

    private DbConnector db;

    public RecommenderService() throws ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        System.out.println("Constructor " + context);
    }

    @GET
    @Path("/{user}/{file}/{method}")
    public List<VenueRest> getMsg(@PathParam("user") String userId,
                                  @PathParam("file") String fileName,
                                  @PathParam("method") String method) throws TasteException, IOException, SQLException {
        String output = "Received : " + userId;
        TreesLoader.getInstance().load("Los Angeles");
        DataModel model = new FileDataModel(new File(context.getRealPath("static/" + fileName + ".csv")));
        CustomRecommender myRecommender = RecommenderFactory.getInstance().create(method, model);
        List<RecommendedItem> results = myRecommender.recommend(Long.parseLong(userId));
        String inReplacement = "";
        List<VenueRest> venuesResults = new ArrayList<VenueRest>();
        if(results.size() > 0) {
            for (RecommendedItem recommendation : results) {
                inReplacement +=  recommendation.getItemID() + ",";
            }
            String qResultingVenues = queryVenues.replace("_IN_",inReplacement.substring(0, inReplacement.length() - 1));
            db = new PostgresConnector();
            db.connect();
            System.out.println(qResultingVenues);
            db.executeQuery(qResultingVenues);
            ResultSet rs = db.getLastResults();

            while (rs.next()) {
                VenueRest v = new VenueRest();
                v.setId(rs.getLong("id"));
                v.setLatitude( rs.getDouble("latitude"));
                v.setLongitude( rs.getDouble("longitude"));
                v.setName(rs.getString("name"));
                v.setCategory(rs.getString("category"));
                venuesResults.add(v);
            }
        }
        return venuesResults;
    }

}