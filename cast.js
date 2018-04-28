var mysql = require('mysql');
var request = require('request');

var con = mysql.createConnection({
	host: //DB HostName
	user : //DB Username
	password : //DB Password
	database: //DB Name
});

con.connect(function(err) {
	if(err) throw err;
	var d = new Date(); //returns the current time
	var hr = d.getHours(); //returns the current hour in 24hr format
	var min = d.getMinutes();
	min--; 
	/*
		since we need the tupels which were updated in the past 1 min, we subtract 1 from current min.
		since we get -1 when we subtract 1 from 00mins, min is set 59 and 1 is subtracted from hr.
		since we get -1 when we subtract 1 from 00hours, hr is set to 23 and the returned date is subtracted by 1.
	*/
	if(min < 0) {
		min = 59;
		hr--;
		if(hr < 0) {
			hr = 23;	
			d.setDate(d.getDate()-1);
		}
		d.setHours(hr);
	}
	d.setMinutes(min);
	d = Date.parse(d);
	/*
		the following query returns all the tuples which added in the previous hour
	*/	
	var query = "SELECT event_name, event_start_date, event_end_date, Address, contact_person, contact_number, fare FROM events WHERE time_added >= "+d;
	con.query(query, function(error, result, fields) {
		if(error) throw error;
		var json_string = JSON.stringify(result);
		send_data(json_string);
	});
});

function send_data(data) {	
	request({
		uri: //server URL to which you want to send the data
		method: "POST",
		form: {
			data : data
		}
	},function(error, response, body){
		if(error) throw error;
	});
}
