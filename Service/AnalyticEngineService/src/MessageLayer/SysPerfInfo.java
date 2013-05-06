package MessageLayer;

import java.io.Serializable;

public class SysPerfInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public double cpuPercent = 0.0; //if value is 30.7 then is 30.7%
	public double totalJVMMem = 0.0; //total JVM memory (kb)
	public double freeJVMMem = 0.0; //free JVM memory (kb)
	public double maxJVMMem = 0.0; //maximum JVM memory (kb)
	public int thrdNum = 0; // total number of process thread
	public String osName = "Unknown OS";
}
