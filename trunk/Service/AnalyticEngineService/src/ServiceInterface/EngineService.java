package ServiceInterface;

import java.util.ArrayList;
import imgproc.ImgFeatureExtractionWrapper;
import database.*;

public class EngineService 
{

	/*******************  Service API group ***********************************/
	//=================== Image management API =========================

	public ArrayList<Long> GetPicId(int nClassId)
	{
		ArrayList<Long> res = databaseAPI.getInstance().GetImageId(nClassId);
		return res;
	}

	public byte[] RetrieveImg(long nImgId) 
	{
		MedicalImage img = databaseAPI.getInstance().RetrieveImage(nImgId);
		return img.image;
	}

	public boolean DeleteImg(int nClassId, long nImgId) 
	{
		boolean res = databaseAPI.getInstance().DeleteImage(nImgId);
		return res;
	}

	public boolean AddImg(int nClassId, long nImgId, byte[] byteImg)
	{
		int nDomainId = (nClassId >> 16);
		MedicalImage img = new MedicalImage();
		img.domainId = nDomainId;
		img.classId = nClassId;
		img.imageId = nImgId;
		img.image = byteImg;
		img.featureV = new ArrayList<Double>();
		
		double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
		ImgFeatureExtractionWrapper.extractFeature(byteImg, vectors);
		for (int i = 0; i < vectors.length; i++)
			img.featureV.add(vectors[i]);
		
		//the feature vector stored in the database is not normalized
		boolean bSuc = databaseAPI.getInstance().AddImage(img);
		
		return bSuc;
	}

	//scope: first level classification
	public ArrayList<Domain> GetDomain()
	{
		ArrayList<Domain> res = databaseAPI.getInstance().getDomain();
		return res;
	}

	//second level classification
	public ArrayList<SecondLevelClass> GetClasses(int nDomianId)
	{
		ArrayList<SecondLevelClass> res = databaseAPI.getInstance().getClass(nDomianId);
		return res;
	}

	//================== Model tuning API ================================

	public boolean SetRBFKernelParam(int nDomainId, double c, double g, int nMaxSamples)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (null == param)
			return false;
		
		param.bRBF = true;
		param.dbRBF_c = c;
		param.dbRBF_g = g;
		param.nMaxSampleNum = nMaxSamples;
		
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		return true;
	}

	public boolean SetLinearKernelParam(int nDomainId, double c, int nMaxSamples)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (param == null)
			return false;
		
		param.bRBF = false;
		param.dbLinear_c = c;
		param.nMaxSampleNum = nMaxSamples;
		
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		
		return true;
	}

	public int GetAutoTuningFoldNum(int nDomainId)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		
		return null == param ? 0 : param.nFold;
	}

	public boolean SetAutoTuningFoldNum(int nDomainId, int nFold)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (param == null)
			return false;
		
		param.nFold = nFold;
		
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		return true;
	}
	
	public boolean enableRBFTuning(int nDomainId)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (null == param)
			return false;
		
		param.bRBF = true;
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		
		return true;
	}
	
	public boolean disableRBFTuning(int nDomainId)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (null == param)
			return false;
		
		param.bRBF = false;
		databaseAPI.getInstance().setModelParameter(nDomainId, param);
		
		return true;
	}
	
	public boolean isRBFTuningEnabled(int nDomainId)
	{
		MedicalParameter param = databaseAPI.getInstance().getModelParameter(nDomainId);
		if (null == param)
			return false;
		
		return param.bRBF;
	}
	
	/************************************************************************************/
	
	public void shutdownServer() 
	{
		
	}
}
