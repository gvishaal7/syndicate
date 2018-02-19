package com.syndicate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
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

public class addToDB extends HttpServlet {

	private String dbDriver = "com.mysql.jdbc.Driver"; //mysql driver string
   	private String dbName = System.getProperty("RDS_DB_NAME"); //the database name = ebdb
  	private String userName = System.getProperty("RDS_USERNAME"); // user name for mysql server 
  	private String password = System.getProperty("RDS_PASSWORD"); // password for mysql server
  	private String hostname = System.getProperty("RDS_HOSTNAME"); // aws rds host string
 	private String port = System.getProperty("RDS_PORT"); //port to which the mysql server is connected
    	/* the connection string */
    	private String jdbcUrl = "jdbc:mysql://" + hostname + ":" +port + "/" + dbName + "?user=" + userName + "&password=" + password;
  

    //event_name,event_start_date,event_end_date, address, fare, contact_person, contact_number

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	Connection con = null;
    	PrintWriter out = null;
    	try {
    		Class.forName(dbDriver);
    		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            	con = DriverManager.getConnection(jdbcUrl);
            	//calls the function which checks if the given table exists or not
    		createTable(con,"events");
    		String eventName = request.getParameter("event_name").toLowerCase(); //the event names are set to lowercase
    		String eventStartDate = request.getParameter("event_start_date"); 
    		java.util.Date startDate = sdf.parse(eventStartDate);
    		long startDateMilli = startDate.getTime(); //converts the given event start date to milliseconds
            	/* checks if the given event doesn't overlap itself */
    		String checkSQLString = "select event_name from events where event_name='"+eventName+"' and event_end_date >="+startDateMilli;
    		String status = "";
    		Statement queryStatement = null;
    		ResultSet sqlResponse = null;
    		Statement insertStatement = null;
    		try {
    			queryStatement = con.createStatement();
    			sqlResponse = queryStatement.executeQuery(checkSQLString);
    			if(sqlResponse.next()) { //condition to check if the event overlaps itself
    				status = "Event already exists";
    			}
    			else {
    				String eventEndDate = request.getParameter("event_end_date");
    				Date endDate = sdf.parse(eventEndDate);
    				long endDateMilli = endDate.getTime(); //converts the given event end date to milliseconds
    				String eventAddress = request.getParameter("event_address");
    				float fare = Float.parseFloat(request.getParameter("event_fare"));
    				String contactPerson = request.getParameter("contact_person");
    				long contactNumber = Long.parseLong(request.getParameter("contact_number")); 
                    long currentTime = new Date().getTime();
                    /* insert statement for the query. delibratly id is ignored as it is set as auto_increment */   		
    				String sqlUpdateQuery = "INSERT INTO events (event_name, event_start_date, event_end_date, address, fare, contact_person, contact_number, time_added) VALUES ('"+eventName+"',"+startDateMilli+","+endDateMilli+",'"+eventAddress+"',"+fare+",'"+contactPerson+"',"+contactNumber+","+currentTime+")";
    				insertStatement = con.createStatement();
    				insertStatement.executeUpdate(sqlUpdateQuery); //adds the new tuple to the table
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
    		response.setContentType("text/html"); //sets the response type as string
    		response.setCharacterEncoding("UTF-8");
    		out = response.getWriter();
    		out.write(status); //writes the response to the calling function
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


    /*
        function which checks if the table we are going to access exists in the database
        if it doesn't exist, it creates a new table with the schema mentioned in the readme file.
    */
    public void createTable(Connection con, String table) {
    	Statement checkStatement = null;
    	ResultSet checkSet = null;
    	try {
    		checkStatement = con.createStatement();
    		String checkString = "SELECT * FROM "+table;
            /* if the table doesn't exist, checkStatement will throw an exception */
    		checkSet = checkStatement.executeQuery(checkString);
    		checkSet.close();
    		checkStatement.close();
    	} catch(Exception e) { //the thrown exception is caught here
    		String message = e.getMessage();
    		if(message.contains("Table") && message.contains("doesn't exist")) {
                /* table schema as mentioned in the readme file */
    			String createStatementString = "create table events(id BIGINT NOT NULL AUTO_INCREMENT, event_name varchar(255) NOT NULL, event_start_date BIGINT NOT NULL, event_end_date BIGINT NOT NULL, Address varchar(255) NOT NULL, fare decimal(7,2), contact_person varchar(255), contact_number BIGINT, time_added BIGINT NOT NULL, PRIMARY KEY(id))";
    			Statement createStatement = null;
    			try {
    				createStatement = con.createStatement();
    				createStatement.executeUpdate(createStatementString); //creates the table.
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
