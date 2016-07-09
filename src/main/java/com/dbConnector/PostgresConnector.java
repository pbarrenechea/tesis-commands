package com.dbConnector;

import com.commands.config.Config;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Pablo on 3/20/2016.
 */
public class PostgresConnector implements DbConnector{
    private static final Logger logger = LogManager.getLogger(PostgresConnector.class);
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet lastResults;

    public PostgresConnector(){ }

    public void connect() {
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://" + Config.getInstance().getProperty("postgres.database_host")
                            + ":" + Config.getInstance().getProperty("postgres.database_port") + "/"
                            + Config.getInstance().getProperty("postgres.database_name"),
                    Config.getInstance().getProperty("postgres.database_user"),
                    Config.getInstance().getProperty("postgres.database_password"));
            logger.debug("Succesfully connected to Postgres");
        } catch (SQLException e) {
            logger.debug("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
    }

    public void executeUpdate(String query) throws SQLException {
        this.stmt = conn.createStatement();
        this.stmt.executeUpdate(query);
    }

    public void executeQuery(String query) throws SQLException {
        this.stmt = conn.createStatement();
        this.lastResults = this.stmt.executeQuery(query);
    }

    public void closeResultSet() throws SQLException {
        this.stmt.close();
        this.lastResults.close();
    }

    public ResultSet getLastResults(){
        return lastResults;
    }
}
