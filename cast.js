var mysql = require('mysql');
var request = require('request');

var con = mysql.createConnection({
	host : //DB host string
	user : //DB user name
	password : //DB password
	database: //DB name
});

con.connect(function(err) {
	if(err) throw err;
	var d = new Date(); //returns the current time
	var hr = d.getHours(); //returns the current hour in 24hr format
	hr--; 
	/*
		since we need the tupels which were updated in the past 1 hr, we subtract 1 from current hour
		since we get -1 when we substract 1 from 00hours, hr is set to 23 and the returned date is subtracted by 1
	*/
	if(hr < 0) {
		hr = 23;
		d.setDate(d.getDate()-1);
	}
	d.setHours(hr);
	d = Date.parse(d);
	/*
		the following query returns all the tuples which added in the previous hour
	*/
	var query = "SELECT event_name, event_start_date, event_end_date, Address, contact_person, contact_number, fare FROM events WHERE time_added >= "+d;
	con.query(query, function(err, result, fields) {
		if(err) throw err;
		var json_string = JSON.stringify(result); //the data retrieved is converted to a string
		send_data(json_string);
	});
});

function send_data(data) {
	request({
		uri: //url to which you want to send the data
		method: "POST",
		form: {
			data : data 
		}
	},function(error, response, body){
		console.log(error);
	});
}
