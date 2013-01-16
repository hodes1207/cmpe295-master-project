package testScript;

import java.util.Comparator;

public class ImgFeatureComparator implements Comparator<ImgFeatureInfo>
{
	public ImgFeatureComparator(ImgFeatureInfo info)
	{
		vec = info.features;
	}
	
	public int compare(ImgFeatureInfo a, ImgFeatureInfo b) 
	{
        double dbDist1 = calcDist(vec, a.features);
        double dbDist2 = calcDist(vec, b.features);
        
        return dbDist1 < dbDist2 ? -1 : 1;
	}
	
	double calcDist(double[] a, double b[])
	{
		if (a.length != b.length)
			return Double.MAX_VALUE;
		
		int n = a.length;
		double dbDis = 0.0;
		for (int i = 0; i < n; i++)
			dbDis += Math.abs(a[i] - b[i]);
		
		return dbDis;
	}
	
	private double[] vec;
}