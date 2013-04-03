package testScript;

import java.util.ArrayList;
import java.util.Date;

public class brutalForceTest {
	
	public static void main(String[] args)
	{
		java.util.Random ran = new java.util.Random();
		ArrayList<ArrayList<Double>> vecs = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i < 50000; i++)
		{
			ArrayList<Double> vec = new ArrayList<Double>();
			for (int j = 0; j < 520; j++)
				vec.add(ran.nextDouble());
			
			vecs.add(vec);
		}
		
		Date d1 = new Date();
		ArrayList<Double> vec = vecs.get(0);
		for (int i = 1; i < vecs.size(); i++)
		{
			double d = 0.0;
			for (int j = 0; j < 520; j++)
			{
				d += Math.abs(vec.get(j) - vecs.get(i).get(j));
			}
		}
		
		Date d2 = new Date();
		System.out.println(d2.getTime() - d1.getTime());
	}
	
}
