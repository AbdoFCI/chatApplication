# chatApplication
demo chatting application using java sockets with single multi threaded server and multi client instances.

## Installation
 * clone the repository and open it in eclipse IDE.
 * ensure that JRE system library is 1.8:
   1. Right click on project folder → Click properties.
   2. Navigate to *Java Build Path → Click JRE system library* → edit → choose "J2SE-1.8" from excution environment drop list.
   3. Click finish → Apply and close
  
### Steps to run the application ###

1. run **Server.java** as java application.
2. for each needed client instance run **Client.java** as java application
 *EX:* to instantiate 2 clients run **Client.java** twice  .
3. once client is alive provide valid username and password to successfully connect to the server.
4. after successful connection send all the messages you want to the server.
5. finally to terminate connection type **bye bye** and hit enter.

### Available users ###
there exists two hard coded users always cerated and saved to users.json
as new client regiteration is not implemented yet.

1. **username**:abdo  **password**:1234 .
2. **username**:bebo  **password**:1234 .
 
---

### Design Notes ###
the App consists of 4 classes as follows:
1. **Server**: responsible for creating a server socket then creating a **ClientHandler** thread for each client connection .
2. **Client**: responsible for creating client instance and connection it to server socket .
3. **ClientHandler**: it extends Thread class as thier exists a ClientHandler thread for each Client instance and resposible for serveing the client depending on **ClientService** instance .
4. **ClientService**: provide a punch of methods(services) to be consumed in **ClientHandler** .
 
---
