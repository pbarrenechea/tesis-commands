package com.recommender.geo;

/**
 * Created by Usuario on 09/07/2016.
 */
public class Point {

    protected double x;
    protected  double y;

    public Point(double x, double y){
        this.x  = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
