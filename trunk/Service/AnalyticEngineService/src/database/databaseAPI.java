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
	
	private static databaseAPI instance = new databaseAPI(); 

	public static databaseAPI getInstance() { 
	       return instance; 
	   } 
	
	public boolean initDBInstance(String domDBName, String clsDBName, String imgDBName, String url)
	{
		classDBName = clsDBName;
		domainDBName = domDBName;
		medicalImageDBName = imgDBName;
		DBUrl = url;
		HttpClient httpClient;
		
		try {
			httpClient = new StdHttpClient.Builder().url(DBUrl).build();
	        dbInstance = new StdCouchDbInstance(httpClient);
	        
	        getDomDBConnection();
	        getClsDBConnection();
	        getImgDBConnection();
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
			return false;
		}

        return true;
	}
	
	public void deleteExistedDBs()
	{
		dbInstance.deleteDatabase(classDBName);
		dbInstance.deleteDatabase(domainDBName);
		dbInstance.deleteDatabase(medicalImageDBName);
	}
	
	public CouchDbConnector getDomDBConnection()
	{
		if (null == dbConDom)
		{
			dbConDom = new StdCouchDbConnector(domainDBName, dbInstance);
			dbConDom.createDatabaseIfNotExists();
		}
		
		return dbConDom;
	}
	
	public CouchDbConnector getClsDBConnection()
	{
		if (null == dbConCls)
		{
			dbConCls = new StdCouchDbConnector(classDBName, dbInstance);
			dbConCls.createDatabaseIfNotExists();
		}
		
		return dbConCls;
	}
	
	public CouchDbConnector getImgDBConnection()
	{
		if (null == dbConImg)
		{
			dbConImg = new StdCouchDbConnector(medicalImageDBName, dbInstance);
			dbConImg.createDatabaseIfNotExists();
		}
		
		return dbConImg;
	}
	
	public boolean AddImage(MedicalImage image)
	{
		try
		{
			MedicalImageRepository imageRepo = new MedicalImageRepository(dbConImg);
			imageRepo.add(image);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}

	//bImgContent:to include byte[] or not
	public ArrayList<MedicalImage> RetrieveImageList(int classId, boolean bImgContent, 
			String strExclusiveStartDocId, int limit)
	{
		if (limit <= 0)
			return null;
		
		new MedicalImageRepository(dbConImg);
		
		int domainId = (classId >> 16);
		ComplexKey key = ComplexKey.of(domainId, classId);
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("image_view")
        .key(key);
		
		query.cacheOk(true);
		
		if (strExclusiveStartDocId == null)
			query.limit(limit);
		else
			query.limit(limit+1);

		if (null != strExclusiveStartDocId)
			query.startDocId(strExclusiveStartDocId);
		
		List<MedicalImage> imageList = dbConImg.queryView(query, MedicalImage.class);
		
		ArrayList<MedicalImage> medicalImageList = new ArrayList<MedicalImage>();
		for(int i = 0; i < imageList.size(); i++)
		{
			MedicalImage image = imageList.get(i);
			if(!bImgContent)
			    image.setImage(null);
			medicalImageList.add(image);
		}
		
		if (null != strExclusiveStartDocId)
			medicalImageList.remove(0);
		
		return medicalImageList;
	}

	public ArrayList<Long> GetImageId(int classId)
	{
		new MedicalImageRepository(dbConImg);
		
		int nDomainId = (classId >> 16);
		ComplexKey key = ComplexKey.of(nDomainId, classId);
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("imageId_view")
        .key(key);
		
		query.cacheOk(true);

		ViewResult result = dbConImg.queryView(query);
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
	public MedicalImage RetrieveImage(long imageId)
	{
		MedicalImageRepository imageRepo = new MedicalImageRepository(dbConImg);
		
		ViewQuery query = new ViewQuery()
        .designDocId("_design/MedicalImage")
        .viewName("imageId_docId_view")
        .key(imageId);
		
		query.cacheOk(true);

		ViewResult result = dbConImg.queryView(query);
		List<Row> rowList = result.getRows();
		
		String docId = rowList.get(0).getValue();
		MedicalImage image = imageRepo.get(docId);
		return image;
	}

	public boolean DeleteImage(long imageId)
	{
		try
		{
			MedicalImageRepository imageRepo = new MedicalImageRepository(dbConImg);

			ViewQuery query = new ViewQuery()
					.designDocId("_design/MedicalImage")
					.viewName("imageId_docId_view").key(imageId);
			
			query.cacheOk(true);

			ViewResult result = dbConImg.queryView(query);
			List<Row> rowList = result.getRows();

			String docId = rowList.get(0).getValue();
			MedicalImage image = imageRepo.get(docId);
			imageRepo.remove(image);
		} 
		catch (Exception e) 
		{
			return false;
		}
		
		return true;
	}

	public ArrayList<Domain> getDomain()
	{
		DomainRepository domainRepo = new DomainRepository(dbConDom);
		List<Domain> domainList = domainRepo.getAll();
		
		//convert to ArrayList
		ArrayList<Domain> domainArrayList = new ArrayList<Domain>();
		for(int i = 0;i < domainList.size(); i++){
			domainArrayList.add(domainList.get(i));
		}
		
		return domainArrayList; 
	}


	public ArrayList<SecondLevelClass> getClass(int nDomainId)
	{
		SecondLevelClassRepository clsRepo = new SecondLevelClassRepository(dbConCls);
		List<SecondLevelClass> clsList = clsRepo.getAll();
		
		ArrayList<SecondLevelClass> res = new ArrayList<SecondLevelClass>();
		for (int i = 0; i < clsList.size(); i++)
		{
			int domId = (clsList.get(i).classId >> 16);
			if (nDomainId < 0 || domId == nDomainId)
				res.add(clsList.get(i));
		}
		
		return res;
	}

	public MedicalParameter getModelParameter(int nDomainId)
	{
		DomainRepository domainRepo = new DomainRepository(dbConDom);
		
		Domain domain = domainRepo.get(Integer.toString(nDomainId));
		return domain.getMedicalParameter();
	}

	public void setModelParameter(int nDomainId, MedicalParameter param)
	{
		DomainRepository domainRepo = new DomainRepository(dbConDom);
		
		Domain newDomain = domainRepo.get(Integer.toString(nDomainId));
		newDomain.setMedicalParameter(param);
		domainRepo.update(newDomain);
	}
	
	private String classDBName = null;
	private String domainDBName = null;
	private String medicalImageDBName = null;
	private String DBUrl = null;
	
	CouchDbInstance dbInstance = null;
	CouchDbConnector dbConDom = null;
	CouchDbConnector dbConCls = null;
	CouchDbConnector dbConImg = null;
}
