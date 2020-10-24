/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project1.servlets;

/**
 *
 * @author joe84
 */

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class CurrencyDatabase {
    
    Context envContext = null, initContext = null;
    DataSource ds = null;
    Connection conn = null;

    public CurrencyDatabase() throws NamingException {
        
        try {
            
            envContext = new InitialContext();
            initContext  = (Context)envContext.lookup("java:/comp/env");
            ds = (DataSource)initContext.lookup("jdbc/db_pool");
            conn = ds.getConnection();
            
        }
        
        catch (SQLException e) {}

    }
    
    public void closeConnection() {
        
        if (conn != null) {
            
            try {
                conn.close();
            }
            
            catch (SQLException e) {}
            
        }
    
    } // End closeConnection()
    
    public Connection getConnection() { return conn; }
    
}
