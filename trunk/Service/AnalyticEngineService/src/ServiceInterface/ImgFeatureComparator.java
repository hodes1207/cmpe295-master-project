package ServiceInterface;

import java.util.ArrayList;
import java.util.Comparator;

import database.MedicalImage;


public class ImgFeatureComparator implements Comparator<MedicalImage>
{
	public ImgFeatureComparator(ArrayList<Double> featureV)
	{
		vec = featureV;
	}
	
	public int compare(MedicalImage a, MedicalImage b) 
	{
        double dbDist1 = calcDist(vec, a.featureV);
        double dbDist2 = calcDist(vec, b.featureV);
        
        return dbDist1 < dbDist2 ? -1 : 1;
	}
	
	double calcDist(ArrayList<Double> a, ArrayList<Double> b)
	{
		if (a.size() != b.size())
			return Double.MAX_VALUE;
		
		int n = a.size();
		double dbDis = 0.0;
		for (int i = 0; i < n; i++)
			dbDis += Math.abs(a.get(i) - b.get(i));
		
		return dbDis;
	}
	
	private ArrayList<Double> vec;
}