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
        Command commandToExecute = CommandFactory.getInstance().createCommand(command);
        commandToExecute.run();
    }
}
