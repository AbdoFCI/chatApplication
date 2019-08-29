package application.multiChat;

import java.io.*; 
import java.net.*; 
import java.util.Scanner; 

public class Client 
{ 
	public static void main(String[] args) throws IOException 
	{ 
		try
		{ 
			Scanner scn = new Scanner(System.in); 
			
			// getting localhost ip 
			InetAddress ip = InetAddress.getByName("localhost"); 
	
			// establish the connection with server port 5056 
			Socket s = new Socket(ip, 5056); 
	
			// obtaining input and out streams of the socket connection
			DataInputStream dis = new DataInputStream(s.getInputStream()); 
			DataOutputStream dos = new DataOutputStream(s.getOutputStream()); 
	
			// the following loop performs the exchange of 
			// information between client and client handler 
			while (true) 
			{ 
				
				// send client username to client handler
				System.out.println("Enter username: ");
				String userName = scn.nextLine();
				dos.writeUTF(userName);
				
				// send client password to client handler
				System.out.println("Enter password: ");
				String password = scn.nextLine();
				dos.writeUTF(password);
				
				// client handler authentication message
				String authMsg = dis.readUTF();
				
				if(authMsg.equals("invalid")) {
					System.out.println("- invalid username or password, please try again -");
				}
				else if(authMsg.equals("valid")){
					
					System.out.println("- successful login -");
					System.out.println("- type < bye bye > to end connection -");
					
					String tosend = "";
					
					// If client sends bye bye, close this connection 
					// and then break from the while loop 
					while(!tosend.equals("bye bye")) 
					{ 
						tosend = scn.nextLine().toLowerCase(); 
						dos.writeUTF(tosend); 
					} 
					System.out.println("Closing this connection : " + s); 
					s.close(); 
					System.out.println("Connection closed"); 
					break;
				} 
			} 
			
			// closing resources 
			scn.close(); 
			dis.close(); 
			dos.close(); 
		}catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 
} 

