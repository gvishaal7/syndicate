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


public class displayEvents extends HttpServlet{

	private String dbDriver = "com.mysql.jdbc.Driver"; //mysql driver string
    private String dbName = System.getProperty("RDS_DB_NAME"); //the database name = ebdb
  	private String userName = System.getProperty("RDS_USERNAME"); // user name for mysql server
  	private String password = System.getProperty("RDS_PASSWORD"); // password for mysql server
  	private String hostname = System.getProperty("RDS_HOSTNAME"); // aws rds host string
  	private String port = System.getProperty("RDS_PORT"); //port to which the mysql server is connected
    	/* the connection string */
  	private String jdbcUrl = "jdbc:mysql://" + hostname + ":" +port + "/" + dbName + "?user=" + userName + "&password=" + password;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	
		PrintWriter out = null;
		try {
			int flag = Integer.parseInt(request.getParameter("flag"));
			String output = getEvents(flag);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			out = response.getWriter();
			out.write(output);
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(out != null) {
					out.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	public String getEvents(int flag) {
		String output = "";
		Connection con = null;	
		try {
			Class.forName(dbDriver);
            con = DriverManager.getConnection(jdbcUrl);
            addToDB atd = new addToDB();
            atd.createTable(con,"events");
            long currentTime = new Date().getTime();
            String sqlQuery = "";
            if(flag == 1) {
            	sqlQuery = "SELECT event_name, event_start_date, event_end_date, Address, contact_person, contact_number, fare FROM events WHERE event_start_date >= "+currentTime;
            }
            else {
            	sqlQuery = "SELECT event_name, event_start_date, event_end_date, Address, contact_person, contact_number, fare FROM events WHERE event_end_date < "+currentTime;	
            }
            Statement queryStatement = null;
            ResultSet sqlResponse = null;
            Map<Integer,List<String>> events = new HashMap<>();
            try {
            	queryStatement = con.createStatement();
            	sqlResponse = queryStatement.executeQuery(sqlQuery);
            	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            	int iter = 1;
            	while(sqlResponse.next()) {
            		List<String> eventDetails = new ArrayList<>();
            		eventDetails.add(sqlResponse.getString(1)); //event name
            		Date startDate = new Date(sqlResponse.getLong(2));
            		eventDetails.add(sdf.format(startDate)); //event start date is converted from milli seconds to user readable format
            		Date endDate = new Date(sqlResponse.getLong(3));
            		eventDetails.add(sdf.format(endDate)); //event end date is converted from milli seconds to user readable format
            		eventDetails.add(sqlResponse.getString(4)); //address
            		eventDetails.add(sqlResponse.getString(5)); //contact person name
            		eventDetails.add(sqlResponse.getString(6)); //contact person number
            		eventDetails.add(sqlResponse.getString(7)); //fare
            		events.put(iter,eventDetails);
            		iter++;
            	}
            	if(events.size() == 0) {
            		events.put(-1,new ArrayList<>());
            	}
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
            	} catch(Exception e) {
            		e.printStackTrace();
            	}
            }
            output = new Gson().toJson(events);
            sqlResponse.close();
            queryStatement.close();
		} catch(ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		} catch(SQLException sqle) {
			sqle.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(con != null) {
					con.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return output;
	}
}

