package com.syndicate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson; //google's library to convert collections to json string

public class updateWebsites extends HttpServlet {

    private String dbDriver = "com.mysql.jdbc.Driver"; //mysql driver string
    private String dbName = System.getProperty("RDS_DB_NAME"); //the database name = ebdb
    private String userName = System.getProperty("RDS_USERNAME"); // user name for mysql server
    private String password = System.getProperty("RDS_PASSWORD"); // password for mysql server
    private String hostname = System.getProperty("RDS_HOSTNAME"); // aws rds host string
    private String port = System.getProperty("RDS_PORT"); //port to which the mysql server is connected
    /* the connection string */
    private String jdbcUrl = "jdbc:mysql://" + hostname + ":" +port + "/" + dbName + "?user=" + userName + "&password=" + password;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Connection con = null;
    	PrintWriter out = null;
    	try {
    		Class.forName(dbDriver);
    		con = DriverManager.getConnection(jdbcUrl);
    		int count = Integer.parseInt(request.getParameter("count")); //the last count that was published to the websites
            /* checks if there exists any tuple which was added since the last periodic update */
    		String sqlQuery = "SELECT id,event_name,event_start_date,event_end_date, address, fare, contact_person, contact_number FROM events WHERE id > "+count;
    		Statement queryStatement = null;
    		ResultSet sqlResponse = null;
    		Map<Long,List<String>> hash = new HashMap<>();
    		try {
    			queryStatement = con.createStatement();
    			sqlResponse = queryStatement.executeQuery(sqlQuery);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    			while(sqlResponse.next()) {
                    List<String> eventDetails = new ArrayList<>();
                    eventDetails.add(sqlResponse.getString(2));
                    Date startDate = new Date(sqlResponse.getLong(3));
                    /* converts the state date to user readable date format (MM/dd/yyyy) */
                    String startDateStr = sdf.format(startDate);
                    eventDetails.add(startDateStr);
                    Date endDate = new Date(sqlResponse.getLong(4));
                    /* converts the state date to user readable date format (MM/dd/yyyy) */
                    String endDateStr = sdf.format(endDate);
                    eventDetails.add(endDateStr);
                    eventDetails.add(sqlResponse.getString(5));
                    eventDetails.add(String.valueOf(sqlResponse.getString(6)));
                    eventDetails.add(sqlResponse.getString(7));
                    eventDetails.add(String.valueOf(sqlResponse.getInt(8)));
                    hash.put(sqlResponse.getLong(1),eventDetails);
    			}
                sqlResponse.close();
                queryStatement.close();
    		} catch(SQLException sqle) {
    			sqle.printStackTrace();
    		} catch(Exception e) {
    			e.printStackTrace();
    		} finally {
    			try {
    				if(sqlResponse != null) {
    					sqlResponse.close();
    				}
    				if(queryStatement != null) {
    					queryStatement.close();
    				}
    			} catch(SQLException sqle) {
    				sqle.printStackTrace();
    			} catch(Exception e) {
    				e.printStackTrace();
    			}
    		}
            /*
                the returned JSON is in the format:
                {'count': {'event details' }}
                
                if the sql query returns an empty set,
                the returned string will be an empty string
            */
    		String json = new Gson().toJson(hash); //converts the hashmap into a json string
    		response.setContentType("application/json"); //sets the response type as json
    		response.setCharacterEncoding("UTF-8");
    		out = response.getWriter();
    		out.write(json); //writes the json string to the calling function
    		out.close();
    		con.close();
        } catch(ClassNotFoundException cnfe) {
    		cnfe.printStackTrace();
    	} catch(SQLException sqle) {
    		sqle.printStackTrace();
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if(out != null) {
    				out.close();
    			}
    			if(con != null) {
    				con.close();
    			}
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }

     public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
     	doPost(request,response);
     }
}
