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

public class UsersFeaturesMatrix {

	private static final Logger logger = LogManager.getLogger(UsersFeaturesMatrix.class);

	private static final String queryUserPreference = "select * from user_city_category_scores uccs\n"
			+ "where uccs.city = '_CITY_'";
	
	//private static final String queryCategories = "select distinct category_id as category from user_city_category_scores uccs\n"
		//	+ "where uccs.city = '_CITY_'";
	
	private static final String queryCategories="select category_id as category from (select distinct category_id as category_id from user_city_category_scores uccs\n"+
			"where uccs.city = '_CITY_') res\n"+
			"inner join category c on (c.id=res.category_id)\n"+
			"where level<_LEVEL_";
	
	private ArrayList <Integer> categories;
	private DbConnector db;
	private String city;
	HashMap<Long, HashMap<Long, Double>> userFeatureMatrix;
	private int level;

	public UsersFeaturesMatrix(String city, int level) {
		db = new PostgresConnector();
		this.city = city;
		this.level = level;
		db.connect();
	}
	
	public void initializeCategories(){
		try {
			String query = queryCategories.replace("_CITY_", city);
			query = query.replace("_LEVEL_", ""+level);
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

	public void initializeUserFeatureMatrix() {
		HashMap<Long, HashMap<Long, Double>> userFeatureMatrixAux = new HashMap<Long, HashMap<Long, Double>>();
		initializeCategories();
		try {
			String query = queryUserPreference.replace("_CITY_", city);
			db.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet userCategoryCitySet = db.getLastResults();
		try {
			while (userCategoryCitySet.next()) {
				Long idUser = new Long(userCategoryCitySet.getLong("user_id"));
				long idCategory = userCategoryCitySet.getLong("category_id");
				Double score = new Double(userCategoryCitySet.getDouble("score"));
				// String city = userCategoryCitySet.getString("city");
				if (!userFeatureMatrixAux.containsKey(idUser))
					userFeatureMatrixAux.put(idUser, new HashMap<Long, Double>());
				HashMap<Long, Double> userFeaturesVector = userFeatureMatrixAux.get(idUser);
				userFeaturesVector.put(idCategory, score);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.userFeatureMatrix = userFeatureMatrixAux;
	}

	public double getPreference(Long idUser, Long idCategory) {
		if (userFeatureMatrix.containsKey(idUser)) {
			HashMap<Long, Double> userFeaturesVector = userFeatureMatrix.get(idUser);
			if (userFeaturesVector.containsKey(idCategory))
				return userFeaturesVector.get(idCategory);
		}
		return 0;
	}

	public static void main(String[] args) throws IOException, TasteException, SQLException {
		UsersFeaturesMatrix u = new UsersFeaturesMatrix("Los Angeles",1);
		u.initializeUserFeatureMatrix();
		double d=u.getPreference(new Long(2015262),new Long(419));
		System.out.println(d);
		// HashMap<Long,HashMap<Long,Double>> vector=u.getUserFeatureMatrix();
		// System.out.println(vector);2015262={384=1.7646895381296661, 418=0.7838735539822874, 436=1.0357836815978831, 357=0.8664339756999316}
	}

	public ArrayList <Integer> getCategories() {
		return categories;
	}

	public void setCategories(ArrayList <Integer> categories) {
		this.categories = categories;
	}
	
}
