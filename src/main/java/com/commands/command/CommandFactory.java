package com.commands.command;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Usuario on 5/5/2016.
 */
public class CommandFactory {

    private static CommandFactory instance;
    protected CommandFactory(){}

    public static CommandFactory getInstance(){
        if( instance  == null ){
            instance = new CommandFactory();
        }
        return instance;
    }

    /**
     *
     * @param command : command to create
     * @return Command
     */
    public Command createCommand(String command) throws IOException, SQLException {
        Command commandToExecute;
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
            //Calculates TF/Idf globally
        }else if( command.equals("globalTfIdf") ){
            commandToExecute = new CategoryTfIdf();
            //Calcuales TF/idf grouping by user
        }else if( command.equals("userCategoriesPreference") ){
            commandToExecute = new UserCategoriesPreferences();
        }else{
            return null;
        }
        return commandToExecute;
    }

}
