package com.recommender.dataStructures;

import com.dbConnector.DbConnector;
import com.dbConnector.PostgresConnector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Usuario on 13/07/2016.
 */
public class DataCenter {
    private  final String qNearestVeunes = "select t.id_user, t.venue_id, v.name, v.latitude, v.longitude, count(1) as total \n" +
            "from venue v\n" +
            "inner Join tips t on ( v.id = t.venue_id )\n" +
            "inner join venue_category vc on ( v.id = vc.venue_id )\n" +
            "where public.distance( _LAT_, _LON_,  v.latitude, v.longitude) < _RADIUS_\n" +
            " and v.city = '_CITY_' \n"+
            "group by (t.id_user, t.venue_id, v.name, v.latitude, v.longitude)";

    private final String querySentiment = "select * from sentiment_ratings";

    private DbConnector db;

    private static DataCenter instance;

    private HashMap<Long, Venue> venues;

    private HashMap<Long, User> users;

    private HashMap<String, Double> userSentiment;

    public static DataCenter getInstance(){
        if( instance  == null ){
            instance = new DataCenter();
        }
        return instance;
    }
    private DataCenter(){
        db = new PostgresConnector();
    }

    private void loadSentimenRatings() throws SQLException {
        this.userSentiment = new HashMap<String, Double>();
        db.executeQuery(querySentiment);
        ResultSet ratings = db.getLastResults();
        while( ratings.next() ){
            long userId = ratings.getLong("user_id");
            long venueId = ratings.getLong("venue_id");
            double sent = ratings.getDouble("sentiment");
            this.userSentiment.put(Long.toString(userId) + "_" + Long.toString(venueId), new Double(sent));
        }
    }

    public Double getUserVenueSent(long userId, long venueId){
        return this.userSentiment.get(Long.toString(userId) + "_" + Long.toString(venueId));
    }

    public void load(String city, double radius, double lat, double lon) throws SQLException {
        TreesLoader.getInstance().load(city, false);
        String queryNearestVenues = qNearestVeunes.replace("_RADIUS_", Double.toString(radius));
        queryNearestVenues = queryNearestVenues.replace("_LAT_", Double.toString(lat));
        queryNearestVenues = queryNearestVenues.replace("_LON_", Double.toString(lon));
        queryNearestVenues = queryNearestVenues.replace("_CITY_", city);
        venues = new HashMap<Long, Venue>();
        users = new HashMap<Long, User>();
        db.connect();
        db.executeQuery(queryNearestVenues);
        ResultSet venuesUser = db.getLastResults();
        while( venuesUser.next() ){
            this.addVenue(venuesUser);
            this.addUser(venuesUser);
        }
        this.loadSentimenRatings();
    }

    public HashMap<Long, Venue> getVenues(){ return this.venues; }

    public HashMap<Long, User> getUsers(){ return this.users; }

    public User getUser(Long key){
        return this.users.get(key);
    }

    public Venue getVenue(Long key){
        return this.venues.get(key);
    }

    public UserCategoryTree getUserTree(long userId){
        return TreesLoader.getInstance().getUserTree(new Long(userId));
    }

    private void addUser(ResultSet venuesUser)throws  SQLException{
        long venueId = venuesUser.getLong("venue_id");
        long userId = venuesUser.getLong("id_user");
        long userChekinsInVenue = venuesUser.getLong("total");
        User u = this.users.get(new Long(userId));
        if( u == null ){
            u = new User(userId);
            this.users.put( new Long(userId), u );
        }
        u.addVenue(venueId, userChekinsInVenue);
    }

    private void addVenue(ResultSet venuesUser ) throws SQLException {
        long venueId = venuesUser.getLong("venue_id");
        Venue v = this.venues.get(new Long(venueId));
        if( v == null ){
            double latitude = venuesUser.getDouble("latitude");
            double longitude = venuesUser.getDouble("longitude");
            v = new Venue(venueId,latitude, longitude);
            this.venues.put( new Long(venueId), v);
        }
        //long categoryId = venuesUser.getLong("category");
        long userId = venuesUser.getLong("id_user");
        //v.addCategory(new Long(categoryId));
        v.addUser(new Long( userId ));
    }
}
