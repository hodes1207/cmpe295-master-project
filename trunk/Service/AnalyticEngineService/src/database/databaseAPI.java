package database;

import java.util.ArrayList;

public class databaseAPI {

	static public boolean AddImage(MedicalImage image)
	{
		return false;
	}

	static public ArrayList<MedicalImage> RetrieveImageList
	(int domainId, int classId, boolean bImgContent)
	{
		return null;
	}

	static public ArrayList<Long> GetImageId(int nDomainId, int classId)
	{
		return null;
	}

	static public MedicalImage RetrieveImage(long imageId)
	{
		return null;
	}

	static public boolean DeleteImage(long imageId)
	{
		return false;
	}

	static public ArrayList<Domain> getDomain()
	{
		return null;
	}

	static public ArrayList<SecondLevelClass> getClass(int nDomainId)
	{
		return null;
	}

	static public ModelParameter getModelParameter(int nDomainId)
	{
		return null;
	}

	static public void setModelParameter(int nDomainId, ModelParameter param)
	{
		
	}

	static public ArrayList<MedicalImage> similaritySearch(int nDomainId, byte[] image)
	{
		return null;
	}
	
}
