/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project1.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.Date;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author Forrest Hood
 */
public class CurrencyServlet extends HttpServlet {

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
        boolean hasresults;
        
        try{
            db = new CurrencyDatabase();
            connection = db.getConnection();
            
            pstatement = connection.prepareStatement("SELECT rate FROM rate WHERE code = ? AND date = ?");
            
            String parameter = request.getParameter("currency_select");
            String dateString = request.getParameter("date");
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            
            pstatement.setString(1, parameter);
            pstatement.setDate(2, new java.sql.Date(date.getTime()));
            hasresults = pstatement.execute();
            resultset = pstatement.getResultSet();
            
            if (resultset.next()) {
                System.err.println("Has Results (not possible)");
                
                System.err.println(resultset.toString());
            }
            else{
                JSONObject responseObj = retrieveData(date, parameter);
                addToDatabase(responseObj);
                
                String jsonString= responseObj.toJSONString();
                System.err.println(jsonString);
                out.print(jsonString);
                out.flush();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
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
    
    protected void addToDatabase(JSONObject jsonResponse) {
        
    }
    
    protected JSONObject retrieveData(Date date, String symbol) {
        System.err.println("Retrieve data called");
        String uri= "https://api.exchangeratesapi.io/";
        JSONObject jsonResponse = null;
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        
        uri = uri.concat(fmt.format(date) + "?base=USD&symbols=" + symbol);
        
        try {
            URL url= new URL(uri);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json; charset=UTF-8");
            connection.setRequestMethod("GET");
            int responseCode= connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStreamReader in = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(in);
                String response = reader.readLine();
                JSONParser parser = new JSONParser();
                jsonResponse = (JSONObject)parser.parse(response);
            }
            connection.disconnect();
        }
        catch (Exception e) { 
            e.printStackTrace(); 
        }
        return jsonResponse;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Currency Servlet";
    }// </editor-fold>

}
