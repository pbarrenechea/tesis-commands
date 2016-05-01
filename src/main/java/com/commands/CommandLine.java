package com.commands;

import com.commands.command.*;
import com.commands.sentiment.SentimentCalculator;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Created by Pablo on 3/20/2016.
 */
public class CommandLine {

    private static final Logger logger = LogManager.getLogger(CommandLine.class);
    private static Scanner user_input;
    private static Command commandToExecute;

    public static void main(String[] args) throws UnirestException, SQLException, IOException {
        System.out.println("Enter command: ");
        user_input = new Scanner( System.in );
        String command;
        command = user_input.next( );
        if( command.equals("sentiment") ){
            commandToExecute = new SentimentTagger();
            //loads categories from categories.json file
        }else if( command.equals("categories") ){
            commandToExecute = new CategoriesLoader();
            //maps category parent id to the corresponding integer
        }else if( command.equals("parents") ){
            commandToExecute = new CategoriesParentMapper();
            //creates a relationship table for venues and categories
        }else if( command.equals("venueCategories") ){
            commandToExecute = new CategoryVenueMapper();
            //Sets venue_id as an integer on the tips table
        }else if( command.equals("venueTips") ){
            commandToExecute = new TipsVenueMapper();
        }else if( command.equals("globalTfIdf") ){
            commandToExecute = new CategoryTfIdf();
        }
        commandToExecute.run();
    }
}
