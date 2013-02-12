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

import org.ektorp.AttachmentInputStream;
import org.ektorp.CouchDbConnector;

import ServiceInterface.ModelManager;

public class DatabaseInitiation {
	public static String imageRootPath = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005";

	public static HashMap<String, Integer[]> domainClassRelationMap;
	public static HashMap<String, String[]> classIdNameRelationMap;
	public static String[] domainNameArray = { "head", "body", /*"upper limb",
			"lower limb", "chest", "breast"*/ };

	public static Integer[] head = { 1, 2, 3, 24, 25, 26, 44, 45, 46 };
	public static Integer[] body = { 4, 5, 14, 15, 16, 17, 27, 28, 39, 40 };
	public static Integer[] upperLimb = { 6, 7, 8, 9, 10, 11, 29, 30, 31, 32,
			33, 47 };
	public static Integer[] lowerLimb = { 18, 19, 20, 21, 22, 23, 35, 36, 37,
			38, 50, 56 };
	public static Integer[] chest = { 12, 13, 34 };
	public static Integer[] breast = { 41, 42, 48, 49 };

	public static String[] headName = { "Train01", "Train02", "Train03", "Train24", "Train25", "Train26", "Train44", "Train45", "Train46" };
	public static String[] bodyName = { "Train04", "Train05", "Train14", "Train15", "Train16", "Train17", "Train27", "Train28", "Train39", "Train40" };
	public static String[] upperLimbName = { "Train06", "Train07", "Train08", "Train09", "Train10", "Train11", "Train29", "Train30", "Train31", "Train32",
		"Train33", "Train47" };
	public static String[] lowerLimbName = { "Train18", "Train19", "Train20", "Train21", "Train22", "Train23", "Train35", "Train36", "Train37",
		"Train38", "Train50", "Train56" };
	public static String[] chestName = { "Train12", "Train13", "Train34" };
	public static String[] breastName = { "Train41", "Train42", "Train48", "Train49" };
	
	public static String classDBName = "classInfoTest";
	public static String domainDBName = "domainInfoTest";
	public static String medicalImageDBName = "medicalImageTest";
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public static void initDomainClassRelationMap() {
		domainClassRelationMap = new HashMap<String, Integer[]>();
		domainClassRelationMap.put("head", head);
		domainClassRelationMap.put("body", body);
		/*domainClassRelationMap.put("upper limb", upperLimb);
		domainClassRelationMap.put("lower limb", lowerLimb);
		domainClassRelationMap.put("chest", chest);
		domainClassRelationMap.put("breast", breast);*/
	}

	public static void initclassIdNameRelationMap() {
		classIdNameRelationMap = new HashMap<String, String[]>();
		classIdNameRelationMap.put("head", headName);
		classIdNameRelationMap.put("body", bodyName);
		classIdNameRelationMap.put("upper limb", upperLimbName);
		classIdNameRelationMap.put("lower limb", lowerLimbName);
		classIdNameRelationMap.put("chest", chestName);
		classIdNameRelationMap.put("breast", breastName);

	}
	
	  public static void main(String[] args) {
	 
		Random randomGenerator = new Random();
		try {
			// create three diff dbs
			CouchDbConnector imageDB = databaseAPI
					.getDBConnection(medicalImageDBName);			
			CouchDbConnector domainDB = databaseAPI
					.getDBConnection(domainDBName);		
			CouchDbConnector classDB = databaseAPI.getDBConnection(classDBName);
			
			// create repository for three dbs
			MedicalImageRepository imageRepo = new MedicalImageRepository(
					imageDB);
			DomainRepository domainRepo = new DomainRepository(domainDB);
			SecondLevelClassRepository classRepo = new SecondLevelClassRepository(
					classDB);

			// init
			DatabaseInitiation.initDomainClassRelationMap();
			DatabaseInitiation.initclassIdNameRelationMap();

			// store whole domain info
			Domain domainWhole = new Domain(
					"WholeDomain", ModelManager.WHOLE_DOMAIN_ID);
			domainWhole.setId(Integer.toString(ModelManager.WHOLE_DOMAIN_ID));
			domainRepo.add(domainWhole);
			
			for (int i = 0; i < DatabaseInitiation.domainNameArray.length; i++) {
				
				// store info into domain db
				Domain domain = new Domain(
						DatabaseInitiation.domainNameArray[i], i);
				domain.setId(Integer.toString(i));
				domainRepo.add(domain);
				
				// store info into class db
				String domainName = DatabaseInitiation.domainNameArray[i];
				Integer[] classIdNum = DatabaseInitiation.domainClassRelationMap.get(domainName);
				String[] className = DatabaseInitiation.classIdNameRelationMap.get(domainName);
				for (int j = 0; j < classIdNum.length; j++){
					int classId = (i << 16) + j;
					SecondLevelClass secondClass = new SecondLevelClass(className[j],classId);
					secondClass.setId(Integer.toString(classId));
					classRepo.add(secondClass);
				}
			}

			
			// store images into image db
			for (int i = 0; i < DatabaseInitiation.domainNameArray.length; i++) {
				for (int j = 0; j < DatabaseInitiation.domainClassRelationMap
						.get(DatabaseInitiation.domainNameArray[i]).length; j++) {
					System.out.println("Domain Id: " + i);

					int trainId = DatabaseInitiation.domainClassRelationMap
							.get(DatabaseInitiation.domainNameArray[i])[j];
					int classId = (i<<16) + j;
					System.out
					.println("Class Id Num: "
							+ classId);

					String imageClassPath;
					if (trainId < 10) {
						imageClassPath = "Train0" + trainId;
					} else {
						imageClassPath = "Train" + trainId;
					}

					System.out.println("Image File Path: " + imageClassPath);
					System.out.println("Image Folder Path: "
							+ DatabaseInitiation.imageRootPath + "//"
							+ imageClassPath);
					File trainFolder = new File(
							DatabaseInitiation.imageRootPath + "//"
									+ imageClassPath);
					File[] listOfImages = trainFolder.listFiles();
					System.out.println("Image File List Length: "
							+ listOfImages.length);

					// listOfImages.length - 1 to avoid Thumb.db file at the end
					for (int n = 0; n < listOfImages.length - 1; n++) {
						if (listOfImages[i].isFile()) {
							MedicalImage image = new MedicalImage();
							//image.setFeatureV(featureV);
							image.setDomainId(i);
							image.setClassId(classId);

							System.out.println("Image File: "
									+ listOfImages[n].getName());

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
									.read(listOfImages[n]);
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							ImageIO.write(imageBuffer, "png", os);
							InputStream data = new ByteArrayInputStream(
									os.toByteArray());
							
							String contentType = "image/png";

							byte[] imageByteArray = os.toByteArray();
							image.setImage(imageByteArray);
							double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
							ImgFeatureExtractionWrapper.extractFeature(imageByteArray, vectors);
							ArrayList<Double> featureV = new ArrayList<Double>();
							for (int k = 0; k < vectors.length; k++){
								featureV.add(vectors[k]);
							}
							
							image.setFeatureV(featureV);
							
							// add image to db without the png file
							imageRepo.add(image);
							
							AttachmentInputStream attachment = new AttachmentInputStream(
									listOfImages[n].getName(), data,
									contentType);

							// imageDB
							Boolean docExist = imageRepo.contains(docId);
							if (docExist) {
								MedicalImage temp = imageRepo.get(docId);

								System.out.println("Revision Num: "
										+ temp.getRevision());

								imageDB.createAttachment(docId,
										temp.getRevision(), attachment);

							}
						} else if (listOfImages[i].isDirectory()) {
							System.out.println("Directory: "
									+ listOfImages[n].getName());
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	} 
	

}


