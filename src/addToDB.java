package com.syndicate;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class addToDB extends HttpServlet {

    private static final String dbDriver = "com.mysql.jdbc.Driver";
    private static final String dbUName = ""; //DB sql username
    private static final String dbPass = ""; //DB sql password
    private static final String dbUrl = ""; //DB connection URL

    //event_name,event_start_date,event_end_date, address, fare, contact_person, contact_number

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Connection con = null;
    	PrintWriter out = null;
    	try {
    		System.out.println(" INSIDE ADDER ");
    		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    		Class.forName(dbDriver);
    		con = DriverManager.getConnection(dbUrl,dbUName,dbPass); //connection object to connect with the backend
    		String eventName = request.getParameter("event_name").toLowerCase(); //all the event names are considered to be in lowercase
    		String eventStartDate = request.getParameter("event_start_date");
    		java.util.Date startDate = sdf.parse(eventStartDate);
		/* converts the given day to milliseconds */
    		long startDateMilli = startDate.getTime(); 
		/* sql query to check if there is no mismatch in the event */
    		String checkSQLString = "select event_name from events where event_name='"+eventName+"' and event_end_date >="+startDateMilli;
		/* a status is returned back to the admin depending the operation */
    		String status = "";
    		Statement queryStatement = null;
    		ResultSet sqlResponse = null;
    		Statement insertStatement = null;
    		try {
    			queryStatement = con.createStatement();
    			sqlResponse = queryStatement.executeQuery(checkSQLString);
    			if(sqlResponse.next()) {
    				status = "Event already exists";
    			}
    			else {
    				String eventEndDate = request.getParameter("event_end_date");
    				java.util.Date endDate = sdf.parse(eventEndDate);
    				long endDateMilli = endDate.getTime();
    				String eventAddress = request.getParameter("event_address");
    				float fare = Float.parseFloat(request.getParameter("event_fare"));
    				String contactPerson = request.getParameter("contact_person");
    				long contactNumber = Long.parseLong(request.getParameter("contact_number")); 
				/* the following query ignores the id column and inserts values for the rest of the column (as id column is set to auto_increment) */
    				String sqlUpdateQuery = "INSERT INTO events (event_name, event_start_date, event_end_date, address, fare, contact_person, contact_number) VALUES ('"+eventName+"',"+startDateMilli+","+endDateMilli+",'"+eventAddress+"',"+fare+",'"+contactPerson+"',"+contactNumber+")";
    				insertStatement = con.createStatement();
    				insertStatement.executeUpdate(sqlUpdateQuery);
    				status = "New event has been added";
    			}
    			queryStatement.close();
    			sqlResponse.close();
    			insertStatement.close();
    		} catch(SQLException sqle) {
    			sqle.printStackTrace();
    		} catch(Exception e) {
    			e.printStackTrace();
    		} finally {
    			try {
    				if(insertStatement != null) {
    					insertStatement.close();
    				}
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
    		response.setContentType("text/html"); 
    		response.setCharacterEncoding("UTF-8");
    		out = response.getWriter();
    		out.write(status); //writes a string back to the client
    		out.close();
    		con.close();
    	} catch(ClassNotFoundException cnfe) {
    		cnfe.printStackTrace();
    	} catch(SQLException sqle) {
    		sqle.printStackTrace();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}finally {
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
