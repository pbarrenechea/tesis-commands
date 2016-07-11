package com.recommender.selection;

/**
 * Created by Usuario on 11/07/2016.
 */
public class PreferenceAwareCandidateSelection {

    private static final String qNearestVeunes = "select * from venue v\n" +
                                                 "where public.distance(_LAT_,_LONG_,  v.latitude, v.longitude) < _RADIUS_";
    private static final String qUsersByVenues = "";
}
