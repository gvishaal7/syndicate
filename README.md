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
"* 1 * * * node /dir/cast.js"

Initially the "count.txt" file countains a count value of 0 and is updated everytime when the servlet returns new data.
This helps in tracking if some new data was added by the admin for every periodic check.
