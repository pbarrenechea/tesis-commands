package com.recommender;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

public class MyNeighborhood implements UserNeighborhood {
	
	private UserSimilarity similarity;
	private DataModel model;
	JDBCExample database;

	public MyNeighborhood(UserSimilarity similarity, DataModel model) throws IOException {
		this.similarity=similarity;
		this.model=model;
		try {
			this.database = new JDBCExample();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		// TODO Auto-generated method stub

	}

	public long[] getUserNeighborhood(long userID) throws TasteException {
		long[] neighborhood=null;
		try {
			//neighborhood = this.database.friendsFriendsList(userID);
			neighborhood=this.getFriends(userID);
			//neighborhood=this.getExpertCandidates(userID);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			
			e.printStackTrace();
		}
		return neighborhood;
	}
	
	private long[] getExpertCandidates(long[] users,long[] places) {
		
		return null;
	}

	public long[] getFriends(long userID) throws SQLException{
		long[] neighborhood=null;
		neighborhood = this.database.friendsFriendsList(userID);
		return neighborhood;
	}

}
