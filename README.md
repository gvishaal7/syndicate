Syndicate

Backend : MySql server

Frontend : HTML, java script, CSS

Server: Apache Tomcat 8

DB Schema: 

![DB Schema](https://i.imgur.com/k92KVw8.png "DB Schema")

The 'time_added' column helps in keeping track of the newly added events to the table. It makes retrieving data that was added in the last 1 hour easier.

Asumption: An event can occur more than once only if its current start date and previous end date doesnt overlap.

my cron file would look like the following:

"59 * * * * node /'directory'/cast.js"
  
the cron job file checks calls the cast.js file every 1 hr.

"compile.sh" file inside the "src" directory provides the insight on how the java classes were compiled.

The looked-up website's api end points are hard coded (must be added) in "cast.js". The new events are cast to the looked-up websites upon retriving from the database.

note: make sure node.js, 'mysql' component of node.js and 'request' component of node.js are installed before running cast.js

AWS Links:

Syndication application : http://syndicateapp-env.us-east-2.elasticbeanstalk.com/

Hosting application : http://hosting-syndicate.us-east-2.elasticbeanstalk.com/
