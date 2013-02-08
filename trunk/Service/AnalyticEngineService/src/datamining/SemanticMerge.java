package datamining;


import java.util.ArrayList;

public class SemanticMerge {
	
	public SemanticMerge(int nf, int nc, double dbw)
	{
		m_nFeatureDim = nf;
		m_nClsNum = nc;
		m_dbWeight = dbw;
	}
	
	public SemanticMerge(int nf, int nc)
	{
		m_nFeatureDim = nf;
		m_nClsNum = nc;
		m_dbWeight = (double)nf/(double)nc * 0.6;;
	}
	
	public void merge(ArrayList<Double> vectors, int nClsId)
	{
		for (int j = 0; j < m_nClsNum; j++)	
			vectors.add(0.0);
		
		vectors.set(m_nFeatureDim + nClsId, m_dbWeight*1.0);
	}
	
	public void merge(ArrayList<Double> vectors, PROB_ESTIMATION_RES res)
	{
		for (int i = 0; i < m_nClsNum; i++)
			vectors.add(0.0);
		
		for (int i = 0; i < res.probRes.size(); i++)
		{
			vectors.set(m_nFeatureDim + res.probRes.get(i).nClsId, m_dbWeight*res.probRes.get(i).dbProb);
		}
	}
	
	private int m_nFeatureDim = 0;
	private int m_nClsNum = 0;
	private	double m_dbWeight = 0.0;
	
}
