package com.syndicate;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.text.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import com.google.gson.*;

public class addToDB extends HttpServlet {

	private String dbDriver = "com.mysql.jdbc.Driver";
    private String dbName = System.getProperty("RDS_DB_NAME");
  	private String userName = System.getProperty("RDS_USERNAME");
  	private String password = System.getProperty("RDS_PASSWORD");
  	private String hostname = System.getProperty("RDS_HOSTNAME");
  	private String port = System.getProperty("RDS_PORT");
  	private String jdbcUrl = "jdbc:mysql://" + hostname + ":" +port + "/" + dbName + "?user=" + userName + "&password=" + password;
  

    //event_name,event_start_date,event_end_date, address, fare, contact_person, contact_number

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Connection con = null;
    	PrintWriter out = null;
    	System.out.println("uid : "+userName);
    	System.out.println("pass : "+password);
    	System.out.println("host : "+hostname);
    	System.out.println("DBName : "+dbName);
    	try {
    		Class.forName(dbDriver);
    		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
    		con = DriverManager.getConnection(jdbcUrl);
    		createTable(con,"events");
    		String eventName = request.getParameter("event_name").toLowerCase();
    		String eventStartDate = request.getParameter("event_start_date");
    		java.util.Date startDate = sdf.parse(eventStartDate);
    		long startDateMilli = startDate.getTime(); 
    		String checkSQLString = "select event_name from events where event_name='"+eventName+"' and event_end_date >="+startDateMilli;
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
    				String sqlUpdateQuery = "INSERT INTO events (event_name, event_start_date, event_end_date, address, fare, contact_person, contact_number) VALUES ('"+eventName+"',"+startDateMilli+","+endDateMilli+",'"+eventAddress+"',"+fare+",'"+contactPerson+"',"+contactNumber+")";
    				insertStatement = con.createStatement();
    				insertStatement.executeUpdate(sqlUpdateQuery);
    				status = "New event has been added";
    			}
    			queryStatement.close();
    			sqlResponse.close();
    			insertStatement.close(); 	
    		} 
    		catch(SQLException sqle) {
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
    		out.write(status);
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


    public void createTable(Connection con, String table) {
    	Statement checkStatement = null;
    	ResultSet checkSet = null;
    	try {
    		checkStatement = con.createStatement();
    		String checkString = "SELECT * FROM "+table;
    		checkSet = checkStatement.executeQuery(checkString);
    		checkSet.close();
    		checkStatement.close();
    	} catch(Exception e) {
    		String message = e.getMessage();
    		if(message.contains("Table") && message.contains("doesn't exist")) {
    			String createStatementString = "create table events(id BIGINT NOT NULL AUTO_INCREMENT, event_name varchar(255) NOT NULL, event_start_date BIGINT NOT NULL, event_end_date BIGINT NOT NULL, Address varchar(255) NOT NULL, fare decimal(7,2), contact_person varchar(255), contact_number BIGINT, PRIMARY KEY(id))";
    			Statement createStatement = null;
    			try {
    				createStatement = con.createStatement();
    				createStatement.executeUpdate(createStatementString);
    				createStatement.close();
    			} catch(Exception e1) {
    				e1.printStackTrace();
    			} finally {
    				try {
    					if(createStatement != null) {
    						createStatement.close();
    					}
    				} catch(Exception e2) {
    					e2.printStackTrace();
    				}
    			}
    		}
    	} finally {
    		try {
    			if(checkSet != null){
    				checkSet.close();
    			}
    			if(checkStatement != null) {
    				checkStatement.close();
    			}
    		} catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
}