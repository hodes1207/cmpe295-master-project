package imgproc;

public class ImgFeatureExtractionWrapper 
{	
	public static final int GLCM_DIM = 16;
	public static final int EOHDIM = 37;
	public static final int LBP_DIM = 256;
	public static final int TOTAL_DIM = (5*EOHDIM + 5*GLCM_DIM +LBP_DIM);
	
	public static native void extractFeature(byte[] img, double[] featureVectors);

	static 
	{
	    System.loadLibrary("FeatureExtraction"); 
	}
}