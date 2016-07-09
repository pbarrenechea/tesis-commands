package com.recommender.dataset;

import com.dbConnector.PostgresConnector;
import com.recommender.dataset.entities.Checkin;
import com.recommender.dataset.entities.User;
import com.recommender.dataset.entities.Venue;
import com.recommender.filter.QueryFilter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pablo Barrenechea on 05/10/2015.
 */
public class PostgresDataset implements Dataset{

    private static final Logger logger = LogManager.getLogger(PostgresDataset.class);

    private PostgresConnector connector;

    public PostgresDataset(){
        logger.debug("Connecting to Postgresql server");
        connector = new PostgresConnector();
        connector.connect();
    }

    public List<Checkin> getCheckins(QueryFilter f) throws SQLException {
        String sql = "SELECT id, user_id, venue_id, latitude, ";
        sql += "longitude, created_at FROM checkins ";
        String queryFilter = f.getQueryFilter();
        if( queryFilter != null && queryFilter != "" ){
            sql += " WHERE " + queryFilter;
        }
        logger.debug("Executing query: " + sql);
        connector.executeQuery(sql);
        ResultSet tmpResults = connector.getLastResults();
        ArrayList<Checkin> results =  new ArrayList<Checkin>();
        if(!tmpResults.isClosed() ){
            while ( tmpResults.next() ){
                Checkin ci = new Checkin();
                ci.setId(tmpResults.getLong("id"));
                ci.setUserId(tmpResults.getLong("user_id"));
                ci.setVenueId(tmpResults.getLong("venue_id"));
                ci.setLatitude(tmpResults.getFloat("latitude"));
                ci.setLongitude(tmpResults.getFloat("longitude"));
                results.add(ci);
            }
        }
        return results;
    }
    
    public List<Venue> getVenues(QueryFilter f) throws SQLException {
        String sql = "SELECT * ";
        sql += " FROM venue ";
        String queryFilter = f.getQueryFilter();
        if( queryFilter != null && queryFilter != "" ){
            sql += " WHERE " + queryFilter;
        }
        logger.debug("Executing query: " + sql);
        connector.executeQuery(sql);
        ResultSet tmpResults = connector.getLastResults();
        ArrayList<Venue> results =  new ArrayList<Venue>();
        if(!tmpResults.isClosed() ){
            while ( tmpResults.next() ){
                Venue ci = new Venue();
                ci.setId(tmpResults.getLong("id"));
                ci.setVenueId(tmpResults.getString("venue_id"));
                ci.setLatitude(tmpResults.getFloat("latitude"));
                ci.setLongitude(tmpResults.getFloat("longitude"));
                results.add(ci);
            }
        }
        return results;
    }
    
    //Devuelve todos los usuarios que han hecho checkin en un lugar, dado una lista de lugares
    public List<User> getUsersWithVenue(List<Venue> venues) throws SQLException {
        String sql = "SELECT distinct id_user ";
        String venuesList="";
        for (Venue venue:venues)
        	venuesList+="'"+venue.getVenueId()+"',";
        venuesList = venuesList.substring(0, venuesList.length()-1);
        sql += " FROM tips where id_venue in ("+venuesList+")";
        
        logger.debug("Executing query: " + sql);
        connector.executeQuery(sql);
        ResultSet tmpResults = connector.getLastResults();
        List<User> result=new ArrayList<User>();
        if(!tmpResults.isClosed() ){
            while ( tmpResults.next() ){
            	User u=new User();
                u.setUserId(tmpResults.getLong("id_user"));
                result.add(u);
            }
        }
        return result;
    }
}
