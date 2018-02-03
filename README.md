Syndicate

Backend : MySql server
Frontend : HTML, java script
Server: Apache Tomcat 8

DB Schema: 

![DB Schema](https://i.imgur.com/6roCTS3.png "DB Schema")

Since MySql server does not support OVER() function, the "id" column acts as a ROW_NUMBER of the analytical function.

log in credentials:

id: admin

password: password

Asumption:
An event can occur more than once only if its current start date and previous end date doesnt overlap.

my cron file would look like the following:
"59 * * * * node /dir/cast.js"
the cron job file checks calls the cast.js file every 1 hr

"compile.sh" file inside the "src" directory provides the insight on how the java classes were compiled.

Initially the "count.txt" file countains a count value of 0 and is updated everytime when the servlet returns new data.
This helps in tracking if some new data was added by the admin between every periodic check.

The lookedup website's api end points are hard coded to "cast.js" and using this data, the newly added data is sent to these websites.

note: make sure node.js is installed before running cast.js
