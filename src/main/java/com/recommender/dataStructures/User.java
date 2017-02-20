package com.recommender.dataStructures;


import java.util.HashMap;
public class User {

    private long userId;

	private HashMap<Long, Long> venues;

    public User(long id){
        this.setUserId(id);
        this.venues = new HashMap<Long, Long>();
    }

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

    public void addVenue(long venueId, long venueCheckins){
        this.venues.put( new Long(venueId), new Long(venueCheckins) );
    }

    public boolean hasVisitVenue(Long venueId){
        return this.venues.containsKey(venueId);
    }

    public HashMap<Long, Long> getUserCheckins(){
        return this.venues;
    }

    public long getUserCheckinsAt(long venueId){
        Long checkins = this.venues.get(new Long(venueId));
        if( checkins != null ){
            return checkins.longValue();
        }else{
            return 0;
        }
    }
}