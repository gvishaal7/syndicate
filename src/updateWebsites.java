package com.syndicate;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.*; //wraps the hashmap into a json string and sends it back to the requesting client

public class updateWebsites extends HttpServlet {

    private static final String dbDriver = "com.mysql.jdbc.Driver";
    private static final String dbUName = ""; //DB username
    private static final String dbPass = ""; //DB password
    private static final String dbUrl = ""; //DB connection URL

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
    	Connection con = null;
    	PrintWriter out = null;
    	try {
    		Class.forName(dbDriver);
    		con = DriverManager.getConnection(dbUrl,dbUName,dbPass); //connection object to connect to the backend
    		int count = Integer.parseInt(request.getParameter("count"));

		/* Retrieves the new updated rows/tuples */
    		String sqlQuery = "SELECT id,event_name,event_start_date,event_end_date, address, fare, contact_person, contact_number FROM events WHERE id > "+count;
		
    		Statement queryStatement = null;
    		ResultSet sqlResponse = null;
    		Map<Long,List<String>> hash = new HashMap<>();
		
    		try {
    			queryStatement = con.createStatement();
    			sqlResponse = queryStatement.executeQuery(sqlQuery);
			/*
				retrieves the data from the sql in the following order,
				id,event_name,event_start_date,event_end_date, address, fare, contact_person, contact_number
				with 1 indexed (starting index is 1 not 0).
			*/
                	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    			while(sqlResponse.next()) {
                    		List<String> eventDetails = new ArrayList<>();
	                    	eventDetails.add(sqlResponse.getString(2));
				/* converts the time from milliseconds to user readable format (MM/dd//yyyy) */
                    		java.util.Date startDate = new java.util.Date(sqlResponse.getLong(3));
                    		String startDateStr = sdf.format(startDate);
                    		eventDetails.add(startDateStr);
				/* converts the time from milliseconds to user readable format (MM/dd//yyyy) */
                    		java.util.Date endDate = new java.util.Date(sqlResponse.getLong(4));
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
    		String json = new Gson().toJson(hash); //wraps the hashmap into a json string and sends it back to the requesting client
    		response.setContentType("application/json");
    		response.setCharacterEncoding("UTF-8");
    		out = response.getWriter();
    		out.write(json); //writes to the requesting client
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
