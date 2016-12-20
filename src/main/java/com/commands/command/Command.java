package com.commands.command;

import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Pablo on 3/20/2016.
 */
public interface Command {

    void run() throws SQLException, UnirestException, IOException;
}
