package com.foameraserblue.connection;

import java.sql.Connection;
import java.sql.SQLException;

public class AnotherConnection implements ConnectionMaker{
    @Override
    public Connection makeConnection() throws ClassNotFoundException, SQLException {
        return null;
    }
}
