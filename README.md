Syndicate

Backend : MySql server
Frontend : HTML, java script
Server: Apache Tomcat 8

![DB Schema](https://i.imgur.com/6roCTS3.png "DB Schema")

Since MySql server does not support OVER() function, the id column acts as a ROW_NUMBER of the analytical function.

The log in id is admin and the password is password.

Asumption:
1) An event can occur more than once only if its current start date and previous end date doesnt overlap.

my cron file would look like the following:
"* 1 * * * node /dir/cast.js"
