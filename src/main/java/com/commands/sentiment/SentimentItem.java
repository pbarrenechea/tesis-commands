package com.commands.sentiment;

/**
 * Created by Pablo on 3/28/2016.
 */
public class SentimentItem {

    private double accuracy;
    private String sentiment;

    public double getAccuracy(){
        return accuracy;
    }

    public String getSentiment(){
        return sentiment;
    }

    public SentimentItem(String s, double a){
        sentiment = s;
        accuracy = a;
    }
}
