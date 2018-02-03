$(function() {
	$(".date_pick").datepicker();
});

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
			document.getElementById("add_to_db_div").style.display = 'block';
			$("#output").empty();
		}
	}
	event.preventDefault();
});

$(document).on("click", "#log_off_button", function(event) {
	document.getElementById("log_in_div").style.display = 'block';
	document.getElementById("add_to_db_div").style.display = 'none';
	event.preventDefault();
});

$(document).on("click","#event_clear_button", function(event) {
	clear_values("event_info");
	event.preventDefault();
});

$(document).on("click","#event_submit_button", function(event) {
	var event_info_details = document.getElementsByName("event_info");
	var event_info = [];
	for(var i=0;i<event_info_details.length;i++) {
		event_info[i] = event_info_details[i].value;
	}
	//console.log(event_info_details);
	if(checkStatus()) {
		//alert("no errors");
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

$(document).on("change","#event_end_date", function(event) {
	var end_date = document.getElementById("event_end_date").value;
	var start_date = document.getElementById("event_start_date").value;
	if(start_date == null || start_date == "") {
		$("#end_date_error").empty();
		$("#end_date_error").append("Enter a valid start date");
	}
	else if(end_date < start_date) {
		$("#end_date_error").empty();
		$("#end_date_error").append("Enter a end date greater than or equal to the start date");
	}
	else {
		$("#end_date_error").empty();
		$("#start_date_error").empty();
	}
});


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

function add_to_db(event_info) {
	//console.log("eventInfo : "+event_info);
	$.post("addToDB", {event_name : event_info[0], event_start_date : event_info[1], event_end_date : event_info[2], event_address : event_info[3], event_fare : event_info[4], contact_person : event_info[5], contact_number : event_info[6] }, function(status){
		//var status = responseJson["status"];
		$("#output").empty();
		$("#output").append(status);
	});
}

function checkStatus() {
	console.log($(".error_message").text());
	if($(".error_message").text().length > 0) {
		return false;
	}
	return true;
}