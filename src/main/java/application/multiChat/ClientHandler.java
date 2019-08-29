package application.multiChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class ClientHandler extends Thread 
{ 
	final DataInputStream dis; 
	final DataOutputStream dos;
	final ClientService cs;
	final Socket s; 
	 
	public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) 
	{ 
		this.s = s; 
		this.cs = new ClientService();
		this.dis = dis; 
		this.dos = dos; 
	}
	
	
	@Override
	public void run() 
	{ 
		String received = ""; 
		while (true) 
		{ 
			try { 

				// Ask user what he wants 
				
				// receive the answer from client 
				String clientUserName = dis.readUTF();
				String clientPassword = dis.readUTF();
				
				// validate Client credentials
				boolean auth = cs.validateClient(clientUserName, clientPassword);
				
				if(!auth) {
					dos.writeUTF("invalid");
				}
				else {
					System.out.println("\t-- Client authenticated successfully --");
					dos.writeUTF("valid");
					
					String conversationText = "";
					
					while(!received.equals("bye bye")) 
					{ 
						received = dis.readUTF();
						conversationText += received+"\n"; 
					} 
					
					if(conversationText.length()>0) {
						cs.dumpClientConversation(clientUserName, conversationText);
					}
					
					System.out.println("Client " + this.s + " sends bye bye..."); 
					System.out.println("Closing this connection."); 
					this.s.close(); 
					System.out.println("Connection closed"); 
					break;
					
				}
			} 
			catch (IOException e) { 
				e.printStackTrace(); 
			} 
		} 
		
		try
		{ 
			// closing resources 
			this.dis.close(); 
			this.dos.close(); 
			
		}catch(IOException e){ 
			e.printStackTrace(); 
		} 
	} 
} 

