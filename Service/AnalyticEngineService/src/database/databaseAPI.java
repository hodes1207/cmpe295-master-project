package database;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.impl.StdCouchDbInstance;

public class databaseAPI {
	
	public static CouchDbConnector getDBConnection(String dbName){
		HttpClient httpClient;
		try {
			httpClient = new StdHttpClient.Builder().url("http://localhost:5984").build();
	        CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
	        CouchDbConnector db = new StdCouchDbConnector(dbName, dbInstance);
	        
	        db.createDatabaseIfNotExists();
	        
			return db;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

        return null;
	}

	static public boolean AddImage(MedicalImage image)
	{
		try{
		CouchDbConnector imageDB = databaseAPI
				.getDBConnection(DatabaseInitiation.medicalImageDBName);
		
		
		MedicalImageRepository imageRepo = new MedicalImageRepository(
				imageDB);
		
		imageRepo.add(image);
		
		return true;
		}catch(Exception e){
		return false;
		}
	}

	//bImgContent:to include byte[] or not
	static public ArrayList<MedicalImage> RetrieveImageList
	(int classId, boolean bImgContent)
	{
		CouchDbConnector imageDB = databaseAPI
				.getDBConnection(DatabaseInitiation.medicalImageDBName);
		
		new MedicalImageRepository(imageDB);
		
		int domainId = (classId >> 16);
		ComplexKey key = ComplexKey.of(domainId, classId);
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("image_view")
        .key(key);
		
		List<MedicalImage> imageList= imageDB.queryView(query, MedicalImage.class);
		
		ArrayList<MedicalImage> medicalImageList = new ArrayList<MedicalImage>();
		for(int i = 0; i < imageList.size(); i++)
		{
			MedicalImage image = imageList.get(i);
			if(!bImgContent)
			    image.setImage(null);
			medicalImageList.add(image);
		}
		return medicalImageList;
	}

	static public ArrayList<Long> GetImageId(int classId)
	{
		CouchDbConnector imageDB = databaseAPI
				.getDBConnection(DatabaseInitiation.medicalImageDBName);
		
		new MedicalImageRepository(imageDB);
		
		int nDomainId = (classId >> 16);
		ComplexKey key = ComplexKey.of(nDomainId, classId);
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("imageId_view")
        .key(key);

		ViewResult result = imageDB.queryView(query);
		List<Row> rowList = result.getRows();
		
		ArrayList<Long> imageIdList = new ArrayList<Long>();
		for(int i = 0; i < rowList.size(); i++)
		{
			String id = rowList.get(i).getValue();
			Long imageId = Long.valueOf(id);
			imageIdList.add(imageId);
		}
		
		return imageIdList;
	}

	//include image byte[] in the image object
	static public MedicalImage RetrieveImage(long imageId)
	{
		CouchDbConnector imageDB = databaseAPI
				.getDBConnection(DatabaseInitiation.medicalImageDBName);
		
		
		MedicalImageRepository imageRepo = new MedicalImageRepository(
				imageDB);
		
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("imageId_docId_view")
        .key(imageId);

		ViewResult result = imageDB.queryView(query);
		List<Row> rowList = result.getRows();
		
		String docId = rowList.get(0).getValue();
		MedicalImage image = imageRepo.get(docId);
		return image;
	}

	static public boolean DeleteImage(long imageId)
	{
		try{
		CouchDbConnector imageDB = databaseAPI
				.getDBConnection(DatabaseInitiation.medicalImageDBName);
		
		
		MedicalImageRepository imageRepo = new MedicalImageRepository(
				imageDB);
		
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("imageId_docId_view")
        .key(imageId);

		ViewResult result = imageDB.queryView(query);
		List<Row> rowList = result.getRows();
		
		String docId = rowList.get(0).getValue();
		MedicalImage image = imageRepo.get(docId);
		imageRepo.remove(image);
		return true;
		}catch(Exception e){
			return false;
		}
	}

	static public ArrayList<Domain> getDomain()
	{
		CouchDbConnector domainDB = databaseAPI
				.getDBConnection(DatabaseInitiation.domainDBName);
		
		
		DomainRepository domainRepo = new DomainRepository(domainDB);
		List<Domain> domainList = domainRepo.getAll();
		
		//convert to ArrayList
		ArrayList<Domain> domainArrayList = new ArrayList<Domain>();
		for(int i = 0;i < domainList.size(); i++){
			domainArrayList.add(domainList.get(i));
		}
		return domainArrayList; 
	}


	static public ArrayList<SecondLevelClass> getClass(int nDomainId)
	{
		CouchDbConnector imageDB = databaseAPI
				.getDBConnection(DatabaseInitiation.medicalImageDBName);
		CouchDbConnector classDB = databaseAPI.getDBConnection(DatabaseInitiation.classDBName);
		
		new MedicalImageRepository(imageDB);
		SecondLevelClassRepository classRepo = new SecondLevelClassRepository(
				classDB);
		
		ComplexKey startKey = ComplexKey.of(nDomainId);
		String domainN = DatabaseInitiation.domainNameArray[nDomainId];
		DatabaseInitiation.initDomainClassRelationMap();
		int classSize = DatabaseInitiation.domainClassRelationMap.get(domainN).length;
		int totalClassNumBelongToThisDomain = (nDomainId << 16) + classSize;
		//System.out.println(classSize + " , " + totalClassNumBelongToThisDomain);
		ComplexKey endKey = ComplexKey.of(nDomainId, totalClassNumBelongToThisDomain);
		
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("domainId_classId_view")
        .group(true)
        .startKey(startKey)
        .endKey(endKey)
        .inclusiveEnd(true);

		ViewResult result = imageDB.queryView(query);
		List<Row> rowList = result.getRows();
		
		/*for(int i = 0; i < rowList.size(); i++){
	    	System.out.println(i+". [domainId, classId]: "+rowList.get(i).getKey());
	    	System.out.println();
		}*/
		
		ArrayList<SecondLevelClass> classList = new ArrayList<SecondLevelClass>();
		for(int i = 0; i < rowList.size(); i++)
		{
			String  str = rowList.get(i).getKey();
			String strClass = str.split(",")[1];
			String classId = strClass.split("]")[0];
			//System.out.println("classId :" +classId);
			SecondLevelClass classObj = classRepo.get(classId);
			classList.add(classObj);
		}
		
		return classList; 
	}

	static public MedicalParameter getModelParameter(int nDomainId)
	{
		CouchDbConnector domainDB = databaseAPI
				.getDBConnection(DatabaseInitiation.domainDBName);
		DomainRepository domainRepo = new DomainRepository(domainDB);
		
		Domain domain = domainRepo.get(Integer.toString(nDomainId));
		return domain.getMedicalParameter();
	}

	static public void setModelParameter(int nDomainId, MedicalParameter param)
	{
		CouchDbConnector domainDB = databaseAPI
				.getDBConnection(DatabaseInitiation.domainDBName);
		
		DomainRepository domainRepo = new DomainRepository(domainDB);
		
		Domain newDomain = domainRepo.get(Integer.toString(nDomainId));
		newDomain.setMedicalParameter(param);
		domainRepo.update(newDomain);
		
	}
	
}
