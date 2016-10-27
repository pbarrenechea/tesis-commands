package com.recommender.dataStructures;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mahout.cf.taste.common.TasteException;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;

public class VenuesFeaturesMatrix {

	private static final Logger logger = LogManager.getLogger(UsersFeaturesMatrix.class);

	private static final String queryVenuePreference = "select vc.venue_id as venue_id, vc.category from venue_category vc \n"+
			"inner join venue v on (v.id=vc.venue_id)"
			+ "where v.city = '_CITY_'";
	private static final String queryCategories = "select distinct vc.category as category from venue_category vc\n"
			+"inner join venue v on (v.id=vc.venue_id)"
			+ "where v.city = '_CITY_'";
	private int maxFeatures=0;

	private DbConnector db;
	private String city;
	HashMap<Long, HashMap<Long, Double>> venueFeatureMatrix;
	private ArrayList <Integer> categories;
	
	public VenuesFeaturesMatrix(String city) {
		db = new PostgresConnector();
		this.city = city;
	}

	public void initializeVenueFeatureMatrix() {
		HashMap<Long, HashMap<Long, Double>> venueFeatureMatrixAux = new HashMap<Long, HashMap<Long, Double>>();
		db.connect();
		try {
			String query = queryVenuePreference.replace("_CITY_", city);
			db.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet userCategoryCitySet = db.getLastResults();
		try {
			while (userCategoryCitySet.next()) {
				Long idVenue = new Long(userCategoryCitySet.getLong("venue_id"));
				long idCategory = userCategoryCitySet.getLong("category");
				Double score = new Double(1);
				// String city = userCategoryCitySet.getString("city");
				if (!venueFeatureMatrixAux.containsKey(idVenue))
					venueFeatureMatrixAux.put(idVenue, new HashMap<Long, Double>());
				HashMap<Long, Double> venueFeaturesVector = venueFeatureMatrixAux.get(idVenue);
				venueFeaturesVector.put(idCategory, score);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initializeCategories();
		
		this.venueFeatureMatrix = venueFeatureMatrixAux;
	}
	
	public void initializeCategories(){
		try {
			String query = queryCategories.replace("_CITY_", city);
			db.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet distinctCategories = db.getLastResults();
		ArrayList<Integer> categories =new ArrayList<Integer>();
		try {
			while (distinctCategories.next()) {
				categories.add(distinctCategories.getInt("category"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setCategories(categories);
	}

	public double getPreference(Long idUser, Long idCategory) {
		if (venueFeatureMatrix.containsKey(idUser)) {
			HashMap<Long, Double> venueFeaturesVector = venueFeatureMatrix.get(idUser);
			if (venueFeaturesVector.containsKey(idCategory))
				return venueFeaturesVector.get(idCategory);
		}
		return 0;
	}

	public static void main(String[] args) throws IOException, TasteException, SQLException {
		VenuesFeaturesMatrix u = new VenuesFeaturesMatrix("Los Angeles");
		u.initializeVenueFeatureMatrix();
		double d=u.getPreference(new Long(106),new Long(187));
		System.out.println(d);
		// HashMap<Long,HashMap<Long,Double>> vector=u.getUserFeatureMatrix();
		// System.out.println(vector);2015262={384=1.7646895381296661, 418=0.7838735539822874, 436=1.0357836815978831, 357=0.8664339756999316}
	}

	public int getMaxFeatures() {
		return maxFeatures;
	}

	public ArrayList <Integer> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList <Integer> categories) {
		this.categories = categories;
	}

}