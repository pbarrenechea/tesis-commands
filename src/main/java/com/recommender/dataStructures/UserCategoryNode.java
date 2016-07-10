package com.recommender.dataStructures;

/**
 * Created by Usuario on 09/07/2016.
 */
public class UserCategoryNode {
    private double score;
    private long id;

    private long numberChekins;
    private String name;

    public UserCategoryNode(long id, String name, double score, long checkins ){
        this.id = id;
        this.name = name;
        this.score = score;
        this.numberChekins = checkins;
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
}

