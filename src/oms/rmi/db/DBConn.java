/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package oms.rmi.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.codegen.CompilerConstants;

/**
 *
 * @author End User
 */
public class DBConn {
    private static String host = "127.0.0.1";
    private static int netTime = 2000;
    private static String user = "root";
    private static String pass = "%$#@!54321Edcba";
//    private static String pass = "";
    private static String database = "dinarpal_db";
    private static String dbUrl = "jdbc:mysql://" + getHost() + "/" + getDatabase();
    private static int port_rmi = 1099;
    
    public static void setPass(String p) {
        pass = p;
    }

    public static String getHost() {
        return host;
    }

    public static int getNetTime() {
        return netTime;
    }

    public static String getUser() {
        return user;
    }

    public static String getDatabase() {
        return database;
    }

    public static int getPort_rmi() {
        return port_rmi;
    }

    public static void setHost(String aHost) {
        host = aHost;
    }

    public static void setPort_rmi(int aPort_rmi) {
        port_rmi = aPort_rmi;
    }
    
    public Connection getConn() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(dbUrl, getUser(), pass);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
//    public static void main(String[] args) throws SQLException {
//        DBConn dbc = new DBConn();
//        Connection c = dbc.getConn();
//        if (c.isClosed()) {
//            System.out.println("close");
//        } else {
//            System.out.println("open");
//        }
//    }
}
