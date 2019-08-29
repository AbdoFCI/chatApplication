package application.multiChat;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ClientService {

	public ClientService() {
		// TODO Auto-generated constructor stub
	}
	
	
	// Read Existing users JSON file and return them as JSON array
	public JSONArray getClients(String jsonFilePath) {
		JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFilePath))
        {
            Object obj = jsonParser.parse(reader);
            JSONArray clientsList = (JSONArray) obj;
            return clientsList;
        } 
        catch (FileNotFoundException e) {e.printStackTrace();return null;}
        catch (IOException e) {e.printStackTrace();return null;}
        catch (ParseException e) {e.printStackTrace();return null;}
	}
	
	
	// Generate and return hash value for an input password 
	public String hashPassword(String input) 
    { 
        try { 
            // getInstance() method is called with algorithm SHA-256 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
            
            // the algorithm works on an array of bytes
            byte[] messageDigest = md.digest(input.getBytes()); 
            BigInteger no = new BigInteger(1, messageDigest); 
            
            // converting the hashed array of bytes into a hashed string
            String hashtext = no.toString(16); 
  
            // Add preceding 0s to make it 32 bit 
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 

            // return the HashText 
            return hashtext; 
        } 
        catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); } 
    } 
	
	
	// Validates client credential against existing clients in JSON file
	public boolean validateClient(String userName, String password) {
		JSONArray clientsList = getClients("users.json");
		for (int i = 0; i < clientsList.size(); i++) {
			JSONObject clientObj = (JSONObject)clientsList.get(i);
			String cUserName = ((String) clientObj.get("userName")).toLowerCase();
			String cPassword = (String) clientObj.get("password");
			if(cUserName.equals(userName.toLowerCase())
					&& cPassword.equals(hashPassword(password))){return true;}
		}
		return false;
	}
	
	
	// Check if client has a previous conversations
	public boolean hasConversations(String userName) {
		if(Files.exists(Paths.get(userName+"Conversations.json"))) {return true;}
		return false;
	}
	
	
	// Get client previous conversations object from JSON file
	public JSONObject getClientConversations(String userName) {
		JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(userName+"Conversations.json"))
        {
            Object obj = jsonParser.parse(reader);
            JSONObject conversationsObj = (JSONObject) obj;
            return conversationsObj;
        } 
        catch (FileNotFoundException e) {e.printStackTrace();return null;}
        catch (IOException e) {e.printStackTrace();return null;}
        catch (ParseException e) {e.printStackTrace();return null;}
	}
	
	
	// Compute each word(key) frequency(value) and store both in a Map
	public Map<String, Integer> calcConversationStats(String conversationText) {
		Map<String, Integer> conversationStats = new HashMap<String, Integer>();
		
		String lines[] = conversationText.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String wordes[] = lines[i].trim().split(" ");
			
			for (int j = 0; j < wordes.length; j++) {
				if(conversationStats.containsKey(wordes[j].trim())) {
					int currFreq = conversationStats.get(wordes[j].trim());
					conversationStats.put(wordes[j].trim(), currFreq+1);
				}
				else {conversationStats.put(wordes[j].trim(), 1);}
			}
		}	
		return conversationStats;
	}
	
	
	@SuppressWarnings("unchecked")
	public void dumpClientConversation(String userName, String conversationText) {
		
		FileWriter file = null;
		try {
			// Calculate statistics for the new conversation
			Map<String, Integer> conversationStats = calcConversationStats(conversationText);
			
			JSONObject newConversation = new JSONObject();
	        JSONObject statsObj = new JSONObject();
			for (Map.Entry<String, Integer> entry : conversationStats.entrySet()) {
				statsObj.put(entry.getKey(), entry.getValue());
			}
			newConversation.put("stats", statsObj);
			newConversation.put("conversationText", conversationText);
			
			// Client has previous conversations
			if(hasConversations(userName)) {
				JSONObject conversationsObj = getClientConversations(userName);
				JSONObject accumlatedStats = (JSONObject) conversationsObj.get("accumulatedStats");
				JSONArray conversationsList = (JSONArray) conversationsObj.get("conversations");
				
				// Generate conversion incremented id
				// and append the new conversation
				newConversation.put("conversationId", (conversationsList.size())+1);
				conversationsList.add(newConversation);
				
				// Update the accumulated statistics of the 
				// client with the new generated conversation
				for (Map.Entry<String, Integer> entry : conversationStats.entrySet()) {
					if(accumlatedStats.containsKey(entry.getKey())) {
						int currFreq = ((Long) accumlatedStats.get(entry.getKey())).intValue();
						accumlatedStats.put(entry.getKey(), currFreq+entry.getValue());
					}
					else {accumlatedStats.put(entry.getKey(), entry.getValue());}
				}
				
				// overwriting the conversations object with the new updates
				conversationsObj.put("conversations", conversationsList);
				conversationsObj.put("accumulatedStats", accumlatedStats);
				
				// overwriting the existing JSON file
				file = new FileWriter(userName+"Conversations.json");
		        file.write(conversationsObj.toJSONString());
		        file.flush();
		        file.close();
			}
		
			// Client first conversation
			else {
				// create conversation for the first time with id 1
				newConversation.put("conversationId", 1);
				
				JSONArray conversations = new JSONArray();
		        conversations.add(newConversation);
				
				JSONObject conversationsObj = new JSONObject();
				conversationsObj.put("conversations", conversations);
				conversationsObj.put("accumulatedStats", statsObj);
		         
				file = new FileWriter(userName+"Conversations.json");
		        file.write(conversationsObj.toJSONString());
		        file.flush();
		        file.close();
			}
		} 
		catch (IOException e) {e.printStackTrace();}
	}
	

}
