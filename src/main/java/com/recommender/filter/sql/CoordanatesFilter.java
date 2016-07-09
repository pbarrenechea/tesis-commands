package com.recommender.filter.sql;

import com.recommender.filter.QueryFilter;
import com.recommender.geo.Point;

/**
 * Created by Pablo Barrenechea on 10/10/2015.
 */
public class CoordanatesFilter implements QueryFilter {

    /**
     * Constant with db fields
     */
    private static final String X_FIELD = "latitude";

    private static final String Y_FIELD = "longitude";

    /**
     * current point
     */
    Point filterPoint;
    /**
     * Distance between the points in KM
     */
    double distance;

    public CoordanatesFilter(Point p, double d) {
        this.filterPoint = p;
        this.distance = d;
    }

    public String getQueryFilter() {
        String filter = "( distance(";
        filter += filterPoint.getX() + ", ";
        filter += filterPoint.getY() + ", ";
        filter += X_FIELD + ", ";
        filter += Y_FIELD + ")";
        filter += " <= " + distance + " )";
        return filter;
    }
}
