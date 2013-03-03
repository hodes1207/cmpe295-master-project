package testScript;

import imgproc.ImgFeatureExtractionWrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import datamining.CLASSIFY_ENTITY;
import datamining.ClassifyModel;
import datamining.ImgFeatureComparator;
import datamining.PROB_ESTIMATION_RES;
import datamining.SemanticMerge;

public class ImageRetrievalTest {

	public static void main(String[] args) throws IOException
	{
		/********************** parameters ************************************************************/
		String strCmprImg = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train05\\3747.png";
		
		//String strRepFolder1 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train12";
		//String strRepFolder2 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\chest";
		//String strRepFolder3 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\hand";
		/**********************************************************************************************/
		
		//get all images in the specified folders
		//ArrayList<String> folders = new ArrayList<String>();
		//folders.add(strRepFolder1);
		//folders.add(strRepFolder2);
		//folders.add(strRepFolder3);
		
		/************************** All folders *********************************************************/
		ArrayList<String> folders = new ArrayList<String>();
		for (int i = 1; i <= 57; i++)
		{
			String strPrefix = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train";
			if (i < 10)
				strPrefix += "0";
			
			strPrefix += Integer.toString(i);
			folders.add(strPrefix);
		}
		/************************************************************************************************/
		
		ArrayList<ImgFeatureInfo> imgsInfo = new ArrayList<ImgFeatureInfo>();
		
		double[] features = Util.retrieveImgFeature(strCmprImg);
		ImgFeatureInfo entry = new ImgFeatureInfo();
		entry.features = features;
		imgsInfo.add(entry);
		
		for (int i = 0; i < folders.size(); i++)
		{
			File file = new File(folders.get(i));
			
			ArrayList<String> imgs = new ArrayList<String>(); 
			Util.recursiveGetFiles(file, imgs);
			
			for (int j = 0; j < imgs.size(); j++)
			{
				double[] f = Util.retrieveImgFeature(imgs.get(j));
				ImgFeatureInfo e = new ImgFeatureInfo();
				e.nClsId = i;
				e.features = f;
				e.strImgPath = imgs.get(j);
				
				imgsInfo.add(e);
			}
		}
		
		//normalize vectors
		Util.normalize(imgsInfo);
		
		ArrayList<CLASSIFY_ENTITY> entities = new ArrayList<CLASSIFY_ENTITY>(imgsInfo.size());
		for (int i = 0; i < imgsInfo.size(); i++)
		{
			CLASSIFY_ENTITY ent = new CLASSIFY_ENTITY();
			//ent.strInfo = imgsInfo.get(i).strImgPath;
			ent.nClsId = imgsInfo.get(i).nClsId;
			ent.vectors = new ArrayList<Double>(imgsInfo.get(i).features.length);
			for (int k = 0; k < imgsInfo.get(i).features.length; k++)
				ent.vectors.add(imgsInfo.get(i).features[k]);
			
			entities.add(ent);
		}
		
		CLASSIFY_ENTITY entInput = entities.get(0);
		entities.remove(0);
		
		ClassifyModel cls = new ClassifyModel();
		cls.useLinear();
		cls.BuildModel(entities);
		PROB_ESTIMATION_RES res = cls.Classify(entInput.vectors);
		
		//Append semantic vector to input image
		SemanticMerge sm = new SemanticMerge(ImgFeatureExtractionWrapper.TOTAL_DIM, 57);
		sm.merge(entInput.vectors, res);
		
		for (int i = 0; i < entities.size(); i++)
		{
			sm.merge(entities.get(i).vectors, entities.get(i).nClsId);
		}
		
		//sort the images according to vector distance
		ImgFeatureComparator comp = new ImgFeatureComparator(entInput);
		Collections.sort(entities, comp);
		
		/*for (int i = 0; i < entities.size(); i++)
			System.out.println(entities.get(i).strInfo);*/
	}
}
