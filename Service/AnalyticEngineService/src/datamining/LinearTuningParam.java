package datamining;

public class LinearTuningParam {
	
	static private double candidateParamC[] = {0.03125, 	0.125, 	0.5, 	2, 	8, 	32, 	
		128, 	512, 	2048, 	8192, 	32768};
	
	public void DefaultInit()
	{
		Init(candidateParamC);
	}
	
	public void Init(double[] c)
	{
		if (c.length <= 0)
			return;
		
		matrixC = c;
		matrixRes = new double[c.length];
	}
	
	String getTuneInfo(int cIndex)
	{
		String strInfo = String.format("C: %f  Accuracy:  %f  ", 
				matrixC[cIndex], matrixRes[cIndex]);
		
		return strInfo;
	}
	
	public String getBestRes()
	{
		String strInfo = String.format(" !!!!! Best result C: %f  Accuracy:  %f  !!!!!", 
				dbBestC, dbBestAccuracy);

		return strInfo;
	}

	public double[] matrixC = null;
	public double[] matrixRes = null;
	
	public double dbBestC = 0.0;
	public double dbBestAccuracy = 0.0;
	
}
