package SystemMonitor;

public class MonitorInfoBean {
	
	private long totalMemory;
	
	private long freeMemory;
	
	private long maxMemory;
	
	private String osName;
	
	private int totalThread;
	
	private double cpuRatio;
	public long getFreeMemory() {
		return freeMemory;
	}
	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}
	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public long getTotalMemory() {
		return totalMemory;
	}
	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}
	
	public int getTotalThread() {
		return totalThread;
	}
	public void setTotalThread(int totalThread) {
		this.totalThread = totalThread;
	}
	public double getCpuRatio() {
		return cpuRatio;
	}
	public void setCpuRatio(double cpuRatio) {
		this.cpuRatio = cpuRatio;
	}
}
