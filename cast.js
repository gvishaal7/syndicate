var fs = require("fs");
var counts = parseInt(fs.readFileSync("count.txt").toString()); //reads the last count that was updated from the count file.
var request = require("request");
request ({
	uri:"http://localhost:8541/syndicate/updateWebsites", //sends a request to the updateWebsites servlet with the last count
	method: "POST",
	form: {
		count: counts
	}
}, function(error, response, body) {
	var output = JSON.parse(body);
	var keys = Object.keys(output);
	/*
	if the servlet responds with a non-empty string, then the count is updated and the new data is send to the 5 
	looked up websites.
	*/
	if(keys.length > 0) { 
		var newCount = keys[keys.length-1];
		setCount(newCount);
		castIt(output);	
	}
});

// function to update the new count value
function setCount(count) {
	fs.writeFile("count.txt",count,function(err) {
		if(err) {
        	return console.log(err);
    	}
    	console.log("Count updated");
	});
}

//function to send the data to the 5 lookedup websites
function castIt(output) {
	var urls = {}; //a string array to store api ends of the looked up websites.
	var methods = {}; //a string array to store the type of send methods for each of the api in the previous array
	for(var i=0;i<urls.length;i++) {
		request ({
			uri : urls[i],
			method: methds[i],
			form : {
				//data in the desired format
			}
		}, function(error, response, body) {
			console.log(body);
		});
	}
}
