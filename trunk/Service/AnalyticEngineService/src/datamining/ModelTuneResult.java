package datamining;

public class ModelTuneResult {

	public 	boolean m_bRBF = true;
	public 	double m_dbRBF_C = 0.03125;
	public 	double m_dbRBF_G = 0.001;
	public 	double m_dbLinear_C = 0.0315;
	public 	double m_dbCurAccuracy = -1.0;
	
	public String getResInfo()
	{
		String strRet = new String();
		if (m_bRBF)
		{
			strRet += "RBF kernel: C -> ";
			strRet += Double.toString(m_dbRBF_C);
			strRet += "    G -> ";
			strRet += Double.toString(m_dbRBF_G);
		}
		else
		{
			strRet += "Linear kernel: C -> ";
			strRet += Double.toString(m_dbLinear_C);
		}
		
		strRet += "  Accuracy -> ";
		return strRet += Double.toString(m_dbCurAccuracy);
	}
}
