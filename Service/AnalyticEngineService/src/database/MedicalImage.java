package database;

import java.util.ArrayList;

public class MedicalImage {

	public int domainId = -1;
	public int classId = -1;
	public long imageId = -1; //using time stamp as the seed to generate random number
	public byte[] image = null;
	public ArrayList<Double> featureV = null;
	
}
