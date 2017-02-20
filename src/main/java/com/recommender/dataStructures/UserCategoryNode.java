package com.recommender.dataStructures;

import java.util.Comparator;

/**
 * Created by Usuario on 09/07/2016.
 */
public class UserCategoryNode implements Comparable<UserCategoryNode> {
    private double score;
    private long id;

    private long numberChekins;
    private String name;

    private long userId;

    public UserCategoryNode(long  userId,long id, String name, double score, long checkins ){
        this.userId = userId;
        this.id = id;
        this.name = name;
        this.score = score;
        this.numberChekins = checkins;
    }

    public long getUserId(){
        return this.userId;
    }

    public double getScore(){
        return this.score;
    }

    public long getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public long getNumberChekins() {
        return numberChekins;
    }

    public void setNumberChekins(long numberChekins) {
        this.numberChekins = numberChekins;
    }

    public int compareTo(UserCategoryNode o) {
        if ( this.score > o.getScore() ){
            return 1;
        }else if( this.score == o.getScore() ){
            return 0;
        }
        return -1;
    }
}

