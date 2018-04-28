# Syndicate

Basic syndication process: Whenever a new block of element is added to the server, all the subscribing websites must also be updated with the same information. Eg: RSS feed

To showcase the above behaviour, two independent webapplications are developed. 'Driver' is the application through which you can add new elements (here events) to the server. 'Hosting' is the application which has subscribed to 'Driver' and is updated whenever a new event is added. 'Hosting' is updated every minute.

The log-in credentials for the admin is,

user id: admin

password: password

The following technologies are used to aid syndication process,

Backend : Java Servlets, MySql server, node.js

Frontend : HTML, JavaScript, CSS

Server: Apache Tomcat 8

## DB Schema: 

![DB Schema](https://i.imgur.com/k92KVw8.png "DB Schema")

The 'time_added' column helps in keeping track of the newly added events to the table. It makes retrieving data that was added in the last min easier.

Asumption: An event can occur more than once only if its current start date and previous end date doesnt overlap.

my cron file would look like the following:

"* * * * * node /'directory'/cast.js"
  
the cron job file calls the cast.js file every min.

"compile.sh" file inside the "src" directory provides the insight on how the java classes were compiled.

The looked-up website's api end points are hard coded (must be added) in "cast.js". The new events are cast to the looked-up websites upon retriving from the database.

note: make sure node.js, 'mysql' component of node.js and 'request' component of node.js are installed before running cast.js. The Cron job will be turned off, unless required.

The AWS webserver was brought down due to in-sufficient funds.

Driver URL: http://syndicate-driver.us-east-2.elasticbeanstalk.com/

Host URL: http://syndicate-hosted.us-east-2.elasticbeanstalk.com/

### Note: 
The start and end dates follow in UTC format. So the upcoming and expired tabs are updated accordingly.
