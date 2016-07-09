package com.recommender.dataset;

import com.recommender.dataset.entities.Checkin;
import com.recommender.dataset.entities.User;
import com.recommender.dataset.entities.Venue;
import com.recommender.filter.QueryFilter;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Pablo Barrenechea on 12/10/2015.
 */
public interface Dataset {

    public List<Checkin> getCheckins(QueryFilter f) throws SQLException;
    
    public List<Venue> getVenues(QueryFilter f) throws SQLException;

	public List<User> getUsersWithVenue(List<Venue> result) throws SQLException;
}
