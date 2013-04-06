package testScript;

import java.util.concurrent.ConcurrentHashMap;

import MessageLayer.CommunicationAPI;

public class HashMapTest {
public static void main(String[] args)
{
	ConcurrentHashMap<String, Integer> test = new ConcurrentHashMap<String, Integer>();
	String str = new String("123456");
    String str2 = new String("123456");

    if(str == str2)
          System.out.println("str == str 2");
//    System.out.println((Object)str2);
    
	test.put(str, 37);
	
	if(test.containsKey(str2))
		System.out.println("test contains");
	else
		System.out.println("test not contains");
	
	
}
}
