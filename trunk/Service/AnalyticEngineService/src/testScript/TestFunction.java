package testScript;

import java.util.ArrayList;

import database.Domain;
import database.MedicalImage;
import database.MedicalParameter;
import database.SecondLevelClass;
import database.databaseAPI;

public class TestFunction {

	/*public static void RetrieveImageListTest()
	{
		ArrayList<MedicalImage> imageList = databaseAPI.RetrieveImageList(131074, true);
    	System.out.println("image list size: "+imageList.size());
    	
    	MedicalImage image = imageList.get(0);
    	System.out.println("image 1 domainId: "+image.getDomainId());
    	System.out.println("image 1 classId: "+image.getClassId());
    	System.out.println("image 1 imageId: "+image.getImageId());
    	if(image.getImage() == null)
    		System.out.println("image byte[] is null");
    	else
    		System.out.println(image.getImage().toString());
	}
	
	public static void GetImageIdTest()
	{
		ArrayList<Long> imageIdList = databaseAPI.GetImageId(131074);
    	System.out.println("image list size: "+imageIdList.size());
    	
    	for(int i = 0; i < imageIdList.size(); i++)
    	{
    	System.out.println("image Id: "+imageIdList.get(i));
    	}
	}
	
	//has some problem, imageId != docId??
	public static void RetrieveImageTest()
	{
		MedicalImage image = databaseAPI.RetrieveImage(-1146022178992967000l);
    	System.out.println("image 1 domainId: "+image.getDomainId());
    	System.out.println("image 1 classId: "+image.getClassId());
		
	}
	
	public static void DeleteImageTest()
	{
		databaseAPI.DeleteImage(-1008084297548721999l);
	}
	
	public static void getDomainTest()
	{
		ArrayList<Domain> domainList = databaseAPI.getDomain();
		
		Domain domain = domainList.get(0);
    	System.out.println("domain 1 domainId: "+domain.getDomainId());
    	System.out.println("domain 1 name: "+domain.getDomainName());
    	System.out.println("domain 1 parameter: "+domain.getMedicalParameter().toString());
	}
	
	public static void getClassTest()
	{
		ArrayList<SecondLevelClass> classList = databaseAPI.getClass(2);
		
		SecondLevelClass secondLevelClass = classList.get(0);
    	System.out.println("class 1 classId: "+secondLevelClass.getClassId());
    	System.out.println("class 1 name: "+secondLevelClass.getClassName());
	}
	
	public static void getModelParameterTest()
	{
		MedicalParameter p = databaseAPI.getModelParameter(0);
		System.out.println("domain 1 parameter: "+p.toString());
	}
	
	public static void setModelParameterTest()
	{
		MedicalParameter p = new MedicalParameter();
		p.setbRBF(true);
		databaseAPI.setModelParameter(0, p);
		MedicalParameter m = databaseAPI.getModelParameter(0);
		System.out.println("domain 1 parameter: "+m.toString());
	}
	
	public static void main(String args[])
	{
		TestFunction.getClassTest();
	}*/
}
