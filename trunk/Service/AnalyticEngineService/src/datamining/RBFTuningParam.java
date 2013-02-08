package datamining;

public class RBFTuningParam {
	
	static private double candidateParamC[] = {0.03125, 	0.125, 	0.5, 	2, 	8, 	32, 	
		128, 	512, 	2048, 	8192, 	32768};

	static private double candidateParamG[] = {0.000030517578125, 	0.0001220703125, 	0.00048828125, 	
		0.001953125, 	0.0078125, 	0.03125, 	0.125, 	0.5, 	2, 	8, 	32};
	
	public void DefaultInit()
	{
		Init(candidateParamC, candidateParamG);
	}
	
	public void Init(double[] c, double[] g)
	{
		if (c.length <= 0 || g.length <= 0)
			return;
		
		matrixC = c;
		matrixG = g;
		matrixRes = new double[c.length][g.length];
	}
	
	public String getTuneInfo(int cIndex, int gIndex)
	{
		String strInfo = String.format("C: %f  G:  %f  Accuracy:  %f  ", 
				matrixC[cIndex], matrixG[gIndex], matrixRes[cIndex][gIndex]);
		
		return strInfo;
	}
	
	public String getBestRes()
	{
		String strInfo = String.format(" !!!!! Best result C: %f  G:  %f  Accuracy:  %f  !!!!!", 
				dbBestC, dbBestG, dbBestAccuracy);

		return strInfo;
	}
	
	public double[] matrixC = null;
	public double[] matrixG = null;
	public double[][] matrixRes = null;
	
	public double dbBestC = 0.0;
	public double dbBestG = 0.0;
	public double dbBestAccuracy = 0.0;
	
}
