package com.recommender.dataStructures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Venue {

    private double latitude;

    private double longitude;
    
    private long id;

    private Set<Long> users;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id){ this.id = id; }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Venue(long id, double lat, double lon){
        this.id = id;
        this.latitude = lat;
        this.longitude = lon;
        this.users = new HashSet<Long>();
    }

    public void addUser(Long u){
        this.users.add(u);
    }

    public Set<Long> getUsers(){
        return this.users;
    }

}