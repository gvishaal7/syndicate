var fs = require("fs");
var counts = parseInt(fs.readFileSync("count.txt").toString());
var request = require("request");
request ({
	uri:"http://localhost:8541/syndicate/updateWebsites",
	method: "POST",
	form: {
		count: counts
	}
}, function(error, response, body) {
	var output = JSON.parse(body);
	console.log(output);
	var keys = Object.keys(output);
	if(keys.length > 0) {
		var newCount = keys[keys.length-1];
		setCount(newCount);
		
	}
	/*
	else {
		console.log("hello");
	}*/
});

function setCount(count) {
	fs.writeFile("count.txt",count,function(err) {
		if(err) {
        	return console.log(err);
    	}
    	console.log("Count updated");
	});
}