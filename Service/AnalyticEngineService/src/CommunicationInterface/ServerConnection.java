/**
 * 
 */
package CommunicationInterface;
import java.io.*;
import java.net.*;

/**
 * @author pramod
 *
 */
public class ServerConnection implements Serializable {

	private  Socket soc;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	/**
	 * 
	 */
	public ServerConnection() throws IOException {
		// TODO Auto-generated constructor stub
		soc = new Socket("localhost", 3456);  // New socket connection to the server
		output = new ObjectOutputStream(soc.getOutputStream()); // Output Stream to send to server
		input = new ObjectInputStream(soc.getInputStream()); // Input Stream to read from server
		
	}
	
	// Send the client requested message to the server.
	public void sendmsg (MessageObject query) throws IOException {
		
		if (query == null) {
			System.out.println("ERROR!! NULL query string passed in");
		}
		
		if (!validate_query_type(query.gettype())) {
			System.out.println("ERROR! Unsupported query type passed in");
		}
		
		output.writeObject(query);
		output.flush();
		output.reset();
	}
	
	// Get the message object from the server.
	public MessageObject getmsg (MessageObject query) throws IOException {
	    MessageObject result;
	    
	    try {
	    result = (MessageObject) input.readObject();
	  //  input.reset();
	    return result;
	    } catch (ClassNotFoundException classNot) {
	    	System.out.println("ERROR execption classnotfound");
	    	return null;
	    }
	}
	
	public void close() throws IOException {
	   soc.close();  // Close the server connection socket
	   output.close(); // Close the output stream
	   input.close(); // Close the input stream
	}
	
	private boolean validate_query_type (MsgId id) {
		
		if (id == MsgId.UNINIT) {
			return false;
		}
		
		// Add code here to validate all types.
		
		return true;
		
	}
	
}
