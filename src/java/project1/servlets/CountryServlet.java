/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project1.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author joe84
 */
public class CountryServlet extends HttpServlet {

    // <editor-fold desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        CurrencyDatabase db = null;
        
        Connection connection;
        PreparedStatement pstatement = null;
        ResultSet resultset = null;
        
        JSONObject jsonObject;
        JSONArray countryArray = new JSONArray();
        boolean hasresults;
        
        try{
            db = new CurrencyDatabase();
            connection = db.getConnection();
            
            pstatement = connection.prepareStatement("SELECT code FROM currency");
            hasresults = pstatement.execute();
            
            if (hasresults) {
                
                resultset = pstatement.getResultSet();
                System.err.println(resultset.toString());
                jsonObject = new JSONObject();
                
                while (resultset.next()) {
                    
                    jsonObject.put(resultset.getString("code"), resultset.getString("code"));
                }
                
                
                String jsonString= jsonObject.toJSONString();
                System.err.println(jsonString);
                out.print(jsonString);
                out.flush();
            
            }
                      
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            
            out.close();
            
            if (resultset != null) { try { resultset.close(); resultset = null; } catch (Exception e) {} }
            
            if (pstatement != null) { try { pstatement.close(); pstatement = null; } catch (Exception e) {} }
            
            if (db != null) { db.closeConnection(); }
            
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Country Servlet";
    }// </editor-fold>

}
