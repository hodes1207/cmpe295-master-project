package util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import MessageLayer.CommunicationAPI;

public class SessionHashMap{
	private final ConcurrentHashMap<String, CommunicationAPI> sessionHashMap = new ConcurrentHashMap<String, CommunicationAPI>();
	static private SessionHashMap instance = null;
	private String strIp = "127.0.0.1";
	private int nPort = 0;
	
	private SessionHashMap(){
		
	}
	
	public static SessionHashMap getInstance()
	{
		if (instance == null)
			instance = new SessionHashMap();
		
		return instance;
	}
	
	public void init(String ip, int port)
	{
		strIp = ip;
		nPort = port;
	}
	
	public CommunicationAPI getSession(String id)
	{ 
		System.out.println("session Id: "+id);
		
		Set keySet = sessionHashMap.keySet();
		Iterator<String> itr = keySet.iterator();
		while(itr.hasNext()){
			System.out.println(itr.next());
		}		
		
		if (!sessionHashMap.containsKey(id)){
			System.out.println("sessionHashMap does not contain the session id: "+id);
			try {
				sessionHashMap.put(id, new CommunicationAPI(strIp, nPort));
				System.out.println("create new communicationAPI object, session Id: "+sessionHashMap.get(id));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		
		return (CommunicationAPI) sessionHashMap.get(id);
	}
	
	void removeSession(String id)
	{
		if (sessionHashMap.contains(id))
		{
			CommunicationAPI obj = (CommunicationAPI) sessionHashMap.get(id);
			sessionHashMap.remove(id);
			try {
				obj.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
