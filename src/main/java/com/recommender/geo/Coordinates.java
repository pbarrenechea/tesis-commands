package com.recommender.geo;

/**
 * Created by Usuario on 09/07/2016.
 */
public class Coordinates {
    private static final double EARTH_RADIUS = 6371000; //Earth radius in meters
    private static final float METERS_PER_KM = 1000;

    public static float getDistanceInKm(double x1, double y1, double x2, double y2){
        double dLat = Math.toRadians(x2 - x1);
        double dLng = Math.toRadians(y2 - y1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(x1))
                * Math.cos(Math.toRadians(x2)) *
                Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (Coordinates.EARTH_RADIUS * c) / Coordinates.METERS_PER_KM ;
        return dist;
    }

    public static float getDistanceInKm(String x1, String y1, String x2, String y2){
        float xf1 = Float.parseFloat(x1);
        float yf1 = Float.parseFloat(y1);
        float xf2 = Float.parseFloat(x2);
        float yf2 = Float.parseFloat(y2);
        return getDistanceInKm(xf1,yf1, xf2, yf2);
    }
}
