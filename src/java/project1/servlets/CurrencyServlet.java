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
import java.util.Iterator;
import java.util.Set;
import org.json.simple.parser.JSONParser;
import com.google.gson.Gson;
import java.math.MathContext;

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
        
        JSONObject jsonObject = new JSONObject();
        boolean hasresults;
        
        try{
            db = new CurrencyDatabase();
            connection = db.getConnection();
            
            pstatement = connection.prepareStatement("SELECT rate FROM rate WHERE code = ? AND date = ?");
            
            String code = request.getParameter("currency_select");
            String dateString = request.getParameter("date");
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            
            pstatement.setString(1, code);
            pstatement.setDate(2, new java.sql.Date(date.getTime()));
            hasresults = pstatement.execute();
            resultset = pstatement.getResultSet();
            
            if (resultset.next()) {
                System.err.println("Has Results from db");
                
                System.err.println(resultset.toString());
                
                JSONObject dbResponse = new JSONObject();
                jsonObject.put(code, resultset.getFloat("rate"));
                dbResponse.put("rates", jsonObject);
                dbResponse.put("base", "USD"); // I get an error "java.sql.SQLException: Column 'base' not found" if i try to get the base from the reultset. It inserts the base correctly though so ¯\_(ツ)_/¯ this works until the user is allowed to change the base currency.
                dbResponse.put("date", dateString);
                
                String jsonString = dbResponse.toJSONString();
                System.err.println(jsonString);
                out.print(jsonString);
                out.flush();
            }
            else{
                JSONObject responseObj = retrieveData(date, code);
                addToDatabase(responseObj, connection, date, code);
                
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
    
    protected void addToDatabase(JSONObject jsonResponse, Connection connection, Date date, String symbol) {
        try{
            System.err.println("addToDatabase called");
            
            PreparedStatement pst = connection.prepareStatement("INSERT INTO rate VALUES(?, ?, ?, ?)");
            
            //String base = jsonResponse.get("base").toString();
            JSONObject rate = new JSONObject();
            Gson gson = new Gson();
            String r = gson.toJson(jsonResponse.get("rates"));
            JSONParser parser = new JSONParser();
            rate = (JSONObject)parser.parse(r);
            
            System.err.println(rate.toJSONString() + "Ratestring");
            System.err.println(symbol + ": Base");
            
            r = rate.get(symbol).toString();
            BigDecimal decimalRate = new BigDecimal(r);
            
            System.err.println(r + " Rate - string");
            
            pst.setString(1, symbol);
            pst.setDate(2, new java.sql.Date(date.getTime()));
            pst.setFloat(3, Float.parseFloat(r));          
            pst.setString(4, "USD");
            
            int success = pst.executeUpdate();
            
            if (success > 0) {
                System.err.println("Added to db successfully");
            }
            else{
                System.err.println("Failed to add to db");
            }        
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Retrieves the desired info if it's not already
     * in the database
     */
    protected JSONObject retrieveData(Date date, String symbol) {
        System.err.println("Retrieve data called");
        System.err.println("Symbol: " + symbol);
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
