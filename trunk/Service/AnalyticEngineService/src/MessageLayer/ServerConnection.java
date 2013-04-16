/**
 * 
 */
package MessageLayer;
import java.io.*;
import java.net.*;

/**
 * @author pramod
 *
 */
public class ServerConnection {

	private  Socket soc;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	/**
	 * 
	 */
	public ServerConnection(String ip, int port) throws IOException {
		// TODO Auto-generated constructor stub
		soc = new Socket(ip, port);  // New socket connection to the server
		output = new ObjectOutputStream(soc.getOutputStream()); // Output Stream to send to server
		input = new ObjectInputStream(soc.getInputStream()); // Input Stream to read from server
		
	}
	
	// Send the client requested message to the server.
	public synchronized void sendmsg (MessageObject query) throws IOException {
		
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
	public synchronized MessageObject getmsg (MessageObject query) throws IOException {
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
