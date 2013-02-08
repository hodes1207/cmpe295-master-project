package datamining;

import java.util.ArrayList;
import java.util.Comparator;


public class ImgFeatureComparator implements Comparator<CLASSIFY_ENTITY>
{
	public ImgFeatureComparator(CLASSIFY_ENTITY info)
	{
		vec = info.vectors;
	}
	
	public int compare(CLASSIFY_ENTITY a, CLASSIFY_ENTITY b) 
	{
        double dbDist1 = calcDist(vec, a.vectors);
        double dbDist2 = calcDist(vec, b.vectors);
        
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