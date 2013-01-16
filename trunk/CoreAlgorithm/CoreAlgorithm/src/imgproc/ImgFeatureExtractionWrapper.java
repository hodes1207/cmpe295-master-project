package imgproc;

public class ImgFeatureExtractionWrapper 
{	
	public static final int GLCM_DIM = 16;
	public static final int EOH_DIM = 37;
	public static final int LBP_DIM = 256;
	public static final int TOTAL_DIM = (5*EOH_DIM + 5*GLCM_DIM +LBP_DIM);
	
	public static final int EOH_WEIGHT = 3;
	public static final int GLCM_WEIGHT = 7;
	public static final int LBP_WEIGHT = 2;
	
	public static native void extractFeature(byte[] img, double[] featureVectors);
	
	public static void assignWeight(int nwEOH, int nwGLCM, int nwLBP, double[] featureVectors)
	{
		if (featureVectors == null || featureVectors.length != TOTAL_DIM)
			return;
		
		int i = 0;
		for (; i < 5*EOH_DIM; i++)
			featureVectors[i] *= nwEOH;
		for (; i < 5*GLCM_DIM; i++)
			featureVectors[i] *= nwGLCM;
		for (; i < LBP_DIM; i++)
			featureVectors[i] *= nwLBP;
	}
	
	public static void assignWeight(double[] featureVectors)
	{
		assignWeight(EOH_WEIGHT, GLCM_WEIGHT, LBP_WEIGHT, featureVectors);
	}

	static 
	{
	    System.loadLibrary("FeatureExtraction"); 
	}
}
