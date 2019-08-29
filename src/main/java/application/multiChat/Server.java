package application.multiChat;

//Java implementation of Server side 
//It contains two classes : Server and ClientHandler 
//Save file as Server.java 

import java.io.*; 
import java.net.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject; 

//Server class 
public class Server 
{ 
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException 
	{ 
		// server is listening on port 5056 
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(5056); 
		
		System.out.println("\t-- Server is on and waiting for Clients --");
		
		// running infinite loop for getting 
		// client request 
		while (true) 
		{ 
			Socket s = null; 
			FileWriter file = null;
			try
			{ 
				//Generate users and save them in JSON file
				file = new FileWriter("users.json");
				ClientService css = new ClientService();
				JSONObject usr1 = new JSONObject();
				usr1.put("password", css.hashPassword("1234"));
		        usr1.put("userName", "abdo");
		        
		        JSONObject usr2 = new JSONObject();
		        usr2.put("password", css.hashPassword("1234"));
		        usr2.put("userName", "bebo");
		        
		        JSONArray usersList = new JSONArray();
		        usersList.add(usr1);
		        usersList.add(usr2);
		         
		        file.write(usersList.toJSONString());
		        file.flush();
		 
		        
				// socket object to receive incoming client requests 
				s = ss.accept(); 
				
				System.out.println("\n\t-- A new client is connected : " + s); 
				
				// obtaining input and out streams 
				DataInputStream dis = new DataInputStream(s.getInputStream()); 
				DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
				
				System.out.println("\t-- Assigning new thread for this client --"); 

				// create a new thread object 
				Thread t = new ClientHandler(s, dis, dos); 

				// Invoking the start() method 
				t.start(); 
				
			}
			catch (IOException e) {
	            e.printStackTrace();
	        }
			catch (Exception e){ 
				s.close(); 
				e.printStackTrace(); 
			}
			
		} 
	} 
} 


