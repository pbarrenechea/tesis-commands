package com.commands.dbConnector;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Pablo on 3/20/2016.
 */

public interface DbConnector {

    void connect();
    void executeUpdate(String query) throws SQLException;
    void executeQuery(String query) throws SQLException;
    void closeResultSet() throws SQLException ;
    ResultSet getLastResults();
}
