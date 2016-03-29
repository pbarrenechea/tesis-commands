package com.commands.sentiment;

import com.commands.config.Config;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

/**
 * Created by Pablo on 3/20/2016.
 */
public class SentimentCalculator {

    private static SentimentCalculator instance;

    public static SentimentCalculator getInstance(){
        if( instance == null ){
            instance = new SentimentCalculator();
        }
        return instance;
    }

    public static SentimentItem calculate(String text) throws UnirestException {
        // These code snippets use an open-source library.
        HttpResponse<JsonNode> response = Unirest.post(Config.getInstance().getProperty("mashape.url"))
                .header("X-Mashape-Key", Config.getInstance().getProperty("mashape.key"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Accept", "application/json")
                .field("txt", text)
                .asJson();
            JSONObject map = (JSONObject) response.getBody().getObject().get("result");
            SentimentItem resultObj = new SentimentItem((String)map.get("sentiment"), Double.parseDouble((String) map.get("confidence")) ) ;
            return resultObj;
    }
}
