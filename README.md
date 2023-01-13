# appointment-scheduler

This application serves as an appointment scheduling and reporting service usable by a company that has clients and contacts. All needed functionality to create appointments is provided, this includes the ability create new customers, and create appointments for those customers. In addition, the user can also view detailed reports of the appointments and customers. 

Below are some sample screenshots:

Login screen: <br></br>
![Login screen](/images/Login.png)

Main menu screen:
![Main menu screen](/images/MainMenu.png)

Features:
* Ability to login using user ID and password.
* Partial language support for Dutch and French. Full support for English. Language is automatically detected.
* Automatic time zone adjustments. Company, user, and database can each be in separate time zones. All time is ultimately converted to UTC.
* Logging of all user login attempts in a text file.
* Profile icon customization.
*	Popup alerts for upcoming appointments.
*	Address formatting automatically changes based on if the address location is in the UK or USA.
*	Ability to add, modify, and delete customer record.
*	Ability to add, modify, and delete customer appointment.
*	Can generate various reports to help users better understand customers and appointments currently stored.

The following technology is utilized:
Java: Java SE 17.0.1
JavaFX: JavaFX-SDK-17.0.1
MySQL Driver:  mysql-connector-java-8.1.23
DBMS: MySQL Workbench 8.0 CE

