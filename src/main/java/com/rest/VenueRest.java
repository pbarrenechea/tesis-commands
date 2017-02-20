package com.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Usuario on 26/11/2016.
 */

@XmlRootElement
public class VenueRest {

    private long id;

    private String name;

    private String category;

    private double latitude;

    private double longitude;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
