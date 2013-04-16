package database;

import imgproc.ImgFeatureExtractionWrapper;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ImgRetrieveServer.ModelManager;

public class DatabaseInitiation 
{
	public static String xmlCfgFile = "testdb_ini.xml";
	public static int dupTimes = 1;
	
	// domain id (domain index) to class ids
	public static HashMap<Integer, Integer[]> domIdToClsId = new HashMap<Integer, Integer[]>();
	
	// domain id (domain index) to domain name
	public static HashMap<Integer, String> domIdToDomName = new HashMap<Integer, String>();
	
	// class id to folder path
	public static HashMap<Integer, String> clsIdToFolder = new HashMap<Integer, String>();
	// class id to class name
	public static HashMap<Integer, String> clsIdToClsName = new HashMap<Integer, String>();

	public static String classDBName = null;
	public static String domainDBName = null;
	public static String medicalImageDBName = null;
	public static String DBUrl = null;
	
	public static boolean parseCfgFile()
	{	
		try 
		{
			File cfgFile = new File(xmlCfgFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(cfgFile);
			doc.getDocumentElement().normalize();
			
			Element root = (Element)doc.getElementsByTagName("DatabaseImport").item(0);
			classDBName = root.getAttribute("classDBName");
			domainDBName = root.getAttribute("domainDBName");
			medicalImageDBName = root.getAttribute("medicalImageDBName");
			DBUrl = root.getAttribute("DBUrl");
			
			NodeList domNodes = root.getElementsByTagName("domain");
			for (int domId = 0; domId < domNodes.getLength(); domId++)
			{
				Element domElem = (Element)domNodes.item(domId);
				String domName = domElem.getAttribute("name");
				domIdToDomName.put(domId, domName);
				
				NodeList clsNodes = domElem.getElementsByTagName("class");
				domIdToClsId.put(domId, new Integer[clsNodes.getLength()]); 
				
				for (int clsIndex = 0; clsIndex < clsNodes.getLength(); clsIndex++)
				{
					Element clsElem = (Element)clsNodes.item(clsIndex);
					String clsName = clsElem.getAttribute("name");
					String clsFolder = clsElem.getTextContent();
					int clsId = ((domId << 16) + clsIndex);
					
					domIdToClsId.get(domId)[clsIndex] = clsId;
					clsIdToClsName.put(clsId, clsName);
					clsIdToFolder.put(clsId, clsFolder);
				}
			}
		} 
		catch ( Exception e1) //ParserConfigurationException | SAXException | IOException
		{
			e1.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private static void insertImgToDB(File file, int domId, int classId, Random randomGenerator
			, MedicalImageRepository imageRepo, CouchDbConnector imageDB) throws IOException
	{
		if (null == file)
			return ;
		
		MedicalImage image = new MedicalImage();
		image.setDomainId(domId);
		image.setClassId(classId);

		System.out.println("Image File: " + file.getName());

		Long imageId = randomGenerator.nextLong();
		image.setImageId(imageId);
		System.out.println("Image Id: " + imageId);

		// TO DO: if doc id can be other type, or create
		// index on imageId
		String docId = Long.toString(imageId);
		image.setId(docId);
		System.out.println("Doc Id: " + docId);

		// create png image attachment to this image
		// document in db
		BufferedImage imageBuffer = ImageIO
				.read(file);
		
		if (null == imageBuffer) 
			return ;
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(imageBuffer, "png", os);
		InputStream data = new ByteArrayInputStream(
				os.toByteArray());

		String contentType = "image/png";

		byte[] imageByteArray = os.toByteArray();
		image.setImage(imageByteArray);
		double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
		ImgFeatureExtractionWrapper.extractFeature(
				imageByteArray, vectors);
		ArrayList<Double> featureV = new ArrayList<Double>();
		for (int k = 0; k < vectors.length; k++) {
			featureV.add(vectors[k]);
		}

		image.setFeatureV(featureV);

		// add image to db without the png file
		imageRepo.add(image);

		AttachmentInputStream attachment = new AttachmentInputStream(
				file.getName(), data, contentType);

		// imageDB
		Boolean docExist = imageRepo.contains(docId);
		if (docExist) {
			MedicalImage temp = imageRepo.get(docId);

			System.out.println("Revision Num: "	+ temp.getRevision());
			imageDB.createAttachment(docId,	temp.getRevision(), attachment);
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {
		
		if (args.length > 2)
		{
			System.out.println(
					"Invalid arguments \n " +
					"First argument is the path of configuration file \n " +
					"Second argument is the duplication times \n");
			
			return;
		}
		
		if (args.length >= 1)
			xmlCfgFile = args[0];
		
		if (args.length == 2)
			dupTimes = Integer.parseInt(args[1]);
		
		if (!parseCfgFile())
			return;
		
		try {
			databaseAPI.getInstance().initDBInstance(domainDBName, classDBName, medicalImageDBName, DBUrl);
			databaseAPI.getInstance().deleteExistedDBs();
			
			// create three databases
			CouchDbConnector imageDB = databaseAPI.getInstance().getImgDBConnection();
			CouchDbConnector domainDB = databaseAPI.getInstance().getDomDBConnection();
			CouchDbConnector classDB = databaseAPI.getInstance().getClsDBConnection();

			// create repository for three databases
			MedicalImageRepository imageRepo = new MedicalImageRepository(imageDB);
			DomainRepository domainRepo = new DomainRepository(domainDB);
			SecondLevelClassRepository classRepo = new SecondLevelClassRepository(classDB);

			// store whole domain info
			Domain domainWhole = new Domain("WholeDomain", ModelManager.WHOLE_DOMAIN_ID);
			domainWhole.setId(Integer.toString(ModelManager.WHOLE_DOMAIN_ID));
			domainRepo.add(domainWhole);

			for (int domId = 0; domId < domIdToDomName.size(); domId++) {

				// store info into domain db
				String domainName = domIdToDomName.get(domId);
				Domain domain = new Domain(domainName, domId);
				domain.setId(Integer.toString(domId));
				domainRepo.add(domain);

				// store info into class database
				Integer[] classIds = domIdToClsId.get(domId);
				for (int clsIndex = 0; clsIndex < classIds.length; clsIndex++) {
					int classId = classIds[clsIndex];
					SecondLevelClass secondClass = new SecondLevelClass(
							clsIdToClsName.get(classId), classId);
					secondClass.setId(Integer.toString(classId));
					classRepo.add(secondClass);
				}
			}

			// store images into image database
			for (int domId = 0; domId < domIdToDomName.size(); domId++) 
			{
				Integer[] classIds = domIdToClsId.get(domId);
				for (int clsIndex = 0; clsIndex < classIds.length; clsIndex++) {
					System.out.println("Domain Id: " + domId);

					int classId = classIds[clsIndex];
					System.out.println("Class Id Num: " + classId);

					String imageClassPath = clsIdToFolder.get(classId);

					System.out.println("Image folder Path: " + imageClassPath);
					File trainFolder = new File(imageClassPath);
					File[] listOfImages = trainFolder.listFiles();
					System.out.println("Image File List Length: "
							+ listOfImages.length);

					// listOfImages.length - 1 to avoid Thumb.db file at the end
					Random randomGenerator = new Random();
					for (int i = 0; i < listOfImages.length; i++) {
						if (listOfImages[i].isFile()) 
						{
							for (int j = 0; j < dupTimes; j++)
								insertImgToDB(listOfImages[i], domId, classId, randomGenerator, imageRepo, imageDB);
						} 
						else if (listOfImages[i].isDirectory()) 
						{
							System.out.println("Directory: "
									+ listOfImages[i].getName());
						}
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
