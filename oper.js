/* function to invoke the calender window for inputing start and end dates */
$(function() {
	$(".date_pick").datepicker();
});

/* log in form submit event */
$(document).on("click","#log_in_button", function(event) {
	//alert("hello");
	var user = document.getElementById("log_in_id").value;
	if(user == null || user == "") {
		alert("enter a user name");
	}
	else if(user != "admin") {
		alert("enter admin as ID");
	}
	else {
		var pass = document.getElementById("password").value;
		if(pass == null || pass == "") {
			alert("enter the password");
		}
		else if(pass != "password") {
			alert("enter the correct password");
		}
		else {
			document.getElementById("log_in_div").style.display = 'none';
			document.getElementById("admin_operations").style.display = 'block';
			new_event();	
			$("#output").empty();
		}
	}
	event.preventDefault();
});

/* returns back to the log in page */
$(document).on("click", "#log_off_button", function(event) {
	document.getElementById("log_in_div").style.display = 'block';
	document.getElementById("admin_operations").style.display = 'none';
	event.preventDefault();
});

/* clears all the inputed values */
$(document).on("click","#event_clear_button", function(event) {
	clear_values("event_info");
	event.preventDefault();
});


/* event addition form submit */
$(document).on("click","#event_submit_button", function(event) {
	var event_info_details = document.getElementsByName("event_info");
	var event_info = [];
	for(var i=0;i<event_info_details.length;i++) {
		event_info[i] = event_info_details[i].value;
	}
	if(checkStatus(event_info)) {
		add_to_db(event_info);
		clear_values("event_info");
	}
	else {
		//alert("errors");
		$("#output").empty();
		$("#output").append("Fix the errors");
	}
	event.preventDefault();
});


/*
	validates the entered fare
	checks if the entered fare is positive
	is a proper decimal number with only numeric characters
*/
$(document).on("change","#event_fare", function(event) {
	var fare_value = document.getElementById("event_fare").value;
	if(fare_value.match(/^[-]\d+\.\d+$/) || fare_value.match(/^[-]\d+$/)) {
		$("#event_fare_error").empty();
		$("#event_fare_error").append("Enter positive numbers");
	}
	else if(!fare_value.match(/^\d+\.\d+$/) && !fare_value.match(/^\d+$/) && !fare_value.match(/^\.\d+$/)) {
		$("#event_fare_error").empty();
		$("#event_fare_error").append("Enter numbers");
	}
	else {
		$("#event_fare_error").empty();
	}
	event.preventDefault();
});


/*
	validates the contact person's name
	checks if the entered value contains only alphabets
*/
$(document).on("change","#event_person",function(event) {
	var name = document.getElementById("event_person").value;
	if(!name.match(/^[a-z]+$/i)) {
		$("#event_person_name_error").empty();
		$("#event_person_name_error").append("Enter only alphabets");
	}
	else {
		$("#event_person_name_error").empty();
	}
	event.preventDefault();
});


/*
	validates the contact phone number
	checks if the entered value contains only numeric values
*/
$(document).on("change","#event_phone_number",function(event) {
	var number = document.getElementById("event_phone_number").value;
	if(!number.match(/^\d+$/)) {
		$("#event_phone_number_error").empty();
		$("#event_phone_number_error").append("Enter only numbers");
	}
	else {
		$("#event_phone_number_error").empty();
	}
	event.preventDefault();
});


/*
	validates the start date
	checks if the entered value is,
		1) greater than the current date (today's date)
		2) is less than the end date (if entered before)
*/
$(document).on("change","#event_start_date",function(event) {
	var start_date = document.getElementById("event_start_date").value;
	start_date = Date.parse(start_date);
	var today = new Date();
	today = Date.parse(today);	
	var end_date = document.getElementById("event_end_date").value;
	end_date = Date.parse(end_date);
	if(start_date < today) {
		$("#start_date_error").empty();
		$("#start_date_error").append("Enter a date in the present/future");
	}
	else if(start_date > end_date) {
		$("#start_date_error").empty();
		$("#start_date_error").append("Select a date less than the end date");	
	}
	else {
		$("#start_date_error").empty();
		$("#end_date_error").empty();
	}
});

/*
	validates the end date
	checks if the entered value is less than the start date and
	if a valid date has been entered for the start date
*/
$(document).on("change","#event_end_date", function(event) {
	var end_date = document.getElementById("event_end_date").value;
	var start_date = document.getElementById("event_start_date").value;
	start_date = Date.parse(start_date);
	end_date = Date.parse(end_date);
	var today = new Date();
	today = Date.parse(today);
	if(end_date < today) {
		$("#end_date_error").empty();
		$("#end_date_error").append("Enter a date in the present/future");
	}
	else if(end_date < start_date) {
		$("#end_date_error").empty();
		$("#end_date_error").append("Enter an end date greater than or equal to the start date");
	}
	else {
		$("#end_date_error").empty();
		$("#start_date_error").empty();
	}
});

/*
	function to clear all the entered values based on the name attribute of the elements
*/
function clear_values(val) {
	var elements = document.getElementsByName(val);
	for(var i=0;i<elements.length;i++) {		
		if(elements[i].type == "text") {
			elements[i].value = "";	
		}
	}
	$(".error_message").empty();
	$("#output").empty();
}

/*
	function that sends data to the servlet
*/
function add_to_db(event_info) {
	open_loader();
	$.post("newEntry", {
		event_name : event_info[0], event_start_date : event_info[1], event_end_date : event_info[2], 
		event_address : event_info[3], event_fare : event_info[4], contact_person : event_info[5], contact_number : event_info[6] }, 
		function(status) {
			console.log(status);
			$("#output").empty();
			$("#output").append(status);
		});
	close_loader();
}

/*
	function that checks if all the errors occurred while adding an event has been rectified.
	this function is called before sending data to the servlet.
*/
function checkStatus(event_info) {
	//console.log($(".error_message").text());
	if($(".error_message").text().length > 0) {
		return false;
	}
	for (var i = 0; i < event_info.length; i++) {
		if(event_info[i] == "") {
			return false;
		}
	}
	return true;
}

/*
	the following functions are called based on the header chosen by the admin from the navigation menu.
	new_event() is called when the admin wants to add a new event.
*/
function new_event() {
	set_header("list_new_event");
	set_body("new_event_div");
}

/*
	up_events() is called when the admin wants to check the upcoming events.
*/
function up_events() {
	open_loader();
	set_header("list_up_events");	
	$.post("getContent", {flag : 1} , 
		function(response) {
			var to_write = set_body_content(response,1);
			$("#up_event_div").empty();
			if(to_write != "") {
				$("#up_event_div").append(to_write);
			}	
		});
	set_body("up_event_div");
	close_loader();
}

/*
	exp_events() is called when the admin wants to check the expired events.
*/
function exp_events() {
	open_loader();
	set_header("list_exp_events");
	$.post("getContent", {flag : 2}, 
		function(response) {
			var to_write = set_body_content(response,2);
			$("#exp_event_div").empty();
			if(to_write != "") {
				$("#exp_event_div").append(to_write);
			}
		});
	set_body("exp_event_div");
	close_loader();
}

/*
	the body content is set according to the header chosen.
*/
function set_body_content(events,flag) {
	var contents = "";
	var keys = Object.keys(events);
	if(keys.length == 1 && keys[0] == -1) {
		if(flag == 1) { //flag = 1 refers to upcoming events
			contents = "<p class='empty_msg'> <b> There is no upcoming event! </b> </p>";
		}
		else { //flag = 2 refers to expired events
			contents = "<p class='empty_msg'> <b> There is no exipred event! </b> </p>";
		}
	}
	else {
		for(var i=0;i<keys.length;i++) {
			var current_event = events[keys[i]];
			var event_name = current_event[0];
			var event_start_date = current_event[1];
			var event_end_date = current_event[2];
			var event_address = current_event[3];
			var event_person = current_event[4];
			var event_number = current_event[5];
			var event_fare = current_event[6];

			contents += "<div class='event_tab'>";
			contents += "<table class='event_tab_table' cellpadding='5' cellspacing='5'>";
			contents += "<tr><th colspan='2'>"+event_name+"</th></tr>";	
			contents += "<tr><td>"+event_start_date+"</td>";
			contents += "<td>"+event_end_date+"</td></tr>";
			contents += "</table>";
			contents += "<div class='event_tab_pop_up'>";
			contents += "<table class='event_tab_table'>"
			contents += "<tr><th colspan='2'>"+event_name+"</th></tr>";
			contents += "<tr><td>"+event_start_date+"</td><td>"+event_end_date+"</td></tr>";
			contents += "<tr><td>"+event_person+"</td><td rowspan='2'>"+event_address+"</td></tr>";
			contents += "<tr><td>"+event_number+"</td></tr>";
			contents += "<tr colspan='2'><td style='text-align:center'>"+event_fare+"</td></tr>";
			contents += "</table></div>";
			contents += "</div>";
		}
	}
	return contents;
}

/*
	a fucntion that provides navigational guidence for the admin
*/
function set_header(option) {
	var list = document.getElementsByName("admin_option_list");
	for (var i = 0; i < list.length; i++) {		
		var id = list[i].id;
		if(id === option) {
			document.getElementById(id).style.background = "#4CAF50";
		}
		else{
			document.getElementById(id).style.background = "#FFF";
		}
	}
}

/*
	a function that enables/disables body divs based on the header chosen.
*/
function set_body(option) {
	var body_list = document.getElementsByName("display_option");
	for(var i=0;i<body_list.length;i++) {
		var id = body_list[i].id;
		if(body_list[i].id === option) {
			document.getElementById(id).style.display = 'block';
		}
		else {
			document.getElementById(id).style.display = 'none';
		}
	}
}

function open_loader() {
	document.getElementById("loading_div").style.display = 'block';
	document.getElementById("admin_operations").style.opactiy = '0.3';
}

function close_loader() {
	document.getElementById("loading_div").style.display = 'none';
	document.getElementById("admin_operations").style.opactiy = '1';
}
