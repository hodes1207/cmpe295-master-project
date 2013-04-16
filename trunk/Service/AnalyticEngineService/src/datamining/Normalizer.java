package datamining;

import java.util.ArrayList;

public class Normalizer {
	
	public Normalizer(int dim)
	{
		m_rec = new double[2*dim];
		for (int i = 0; i < dim; i++)
		{
			m_rec[2*i] 		= 	Double.MAX_VALUE;
			m_rec[2*i+1] 	= 	Double.MIN_VALUE;
		}
	}
	
	public void InitialScan(double[] vec)
	{
		if (null == vec || null == m_rec || m_rec.length != 2*vec.length)
			return;
		
		for (int i = 0; i < vec.length; i++)
		{
			m_rec[2*i] 		= 	Math.min(m_rec[2*i],  vec[i]);
			m_rec[2*i+1] 	= 	Math.max(m_rec[2*i+1],  vec[i]);
		}
	}
	
	public void InitialScan(ArrayList<Double> vec)
	{
		if (null == vec || null == m_rec || m_rec.length != 2*vec.size())
			return;
		
		for (int i = 0; i < vec.size(); i++)
		{
			m_rec[2*i] 		= 	Math.min(m_rec[2*i],  	vec.get(i));
			m_rec[2*i+1] 	= 	Math.max(m_rec[2*i+1],  vec.get(i));
		}
	}
	
	public void normalizeVector(double[] vec)
	{
		if (null == vec || null == m_rec || m_rec.length != 2*vec.length)
			return;
		
		int nLow = -1;
		int nUp = 1;
		
		for (int i = 0; i < vec.length; i++)
		{
			if (m_rec[2*i+1] == m_rec[2*i])
				vec[i] = 0.0;
			else
				vec[i] = nLow + 
					(nUp - nLow)*(vec[i] - m_rec[2*i])
					/(m_rec[2*i+1] - m_rec[2*i]);
		}
	}
	
	public void normalizeVector(ArrayList<Double> vec)
	{
		if (null == vec || null == m_rec || m_rec.length != 2*vec.size())
			return;
		
		int nLow = -1;
		int nUp = 1;
		
		for (int i = 0; i < vec.size(); i++)
		{
			if (m_rec[2*i+1] == m_rec[2*i])
				vec.set(i, 0.0);
			else
			{
				double x = nLow + 
						(nUp - nLow)*(vec.get(i) - m_rec[2*i])
						/(m_rec[2*i+1] - m_rec[2*i]);
				
				vec.set(i, x);
			}
		}
	}
	
	private double[] m_rec = null;
}
