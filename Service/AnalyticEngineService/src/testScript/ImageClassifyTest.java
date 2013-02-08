package testScript;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import datamining.CLASSIFY_ENTITY;
import datamining.ClassifyModel;
import datamining.ModelTuneResult;
import datamining.PROB_ESTIMATION_RES;
import java.util.Collections;

import Jama.Matrix;
import PCA.PCA;

public class ImageClassifyTest {
	
	public static void main(String[] args) throws IOException
	{
		/********************** parameters ************************************************************/
		String strClsImg = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train04\\3711.png";
		
		/*************************** selected folders *************************************************
		String strRepFolder1 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train01";
		String strRepFolder2 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train02";
		String strRepFolder3 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train03";
		
		ArrayList<String> folders = new ArrayList<String>();
		folders.add(strRepFolder1);
		folders.add(strRepFolder2);
		folders.add(strRepFolder3);
		**********************************************************************************************/
		
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
		
		ArrayList<ImgFeatureInfo> imgInfos = new ArrayList<ImgFeatureInfo>();
		double[] features = Util.retrieveImgFeature(strClsImg);
		ImgFeatureInfo entry = new ImgFeatureInfo();
		entry.features = features;
		imgInfos.add(entry);
		
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
				
				imgInfos.add(e);
			}
		}
		
		Util.normalize(imgInfos);
		
		ArrayList<CLASSIFY_ENTITY> entities = new ArrayList<CLASSIFY_ENTITY>(imgInfos.size());
		for (int i = 0; i < imgInfos.size(); i++)
		{
			CLASSIFY_ENTITY ent = new CLASSIFY_ENTITY();
			ent.nClsId = imgInfos.get(i).nClsId;
			ent.vectors = new ArrayList<Double>(imgInfos.get(i).features.length);
			for (int k = 0; k < imgInfos.get(i).features.length; k++)
				ent.vectors.add(imgInfos.get(i).features[k]);
			
			entities.add(ent);
		}
		
		imgInfos = null;
		
		/************************************ PCA ***********************************
		
		int nRow = entities.size();
		int nCol = entities.get(0).vectors.size();
		double[][] a = new double[nRow][nCol];
		for (int i = 0; i < nRow; i++)
			for (int j = 0; j < nCol; j++)
				a[i][j] = entities.get(i).vectors.get(j);
		Matrix trainingData = new Matrix(a);
		
		PCA pca = new PCA(trainingData);
		
		for (int i = 0; i < nRow; i++)
		{
			double[][] v = new double[1][entities.get(i).vectors.size()];
			for (int j = 0; j < v[0].length; j++)
				v[0][j] = entities.get(i).vectors.get(j);
			Matrix vm = new Matrix(v);
			
			Matrix transformedData =
					pca.transform(vm, PCA.TransformationType.ROTATION);
			
			int nDim = transformedData.getColumnDimension();
			entities.get(i).vectors = new ArrayList<Double>(nDim);
			for (int j = 0; j < nDim; j++)
				entities.get(i).vectors.add(transformedData.get(0, j));
		}
		
		/**************************************************************************************/
		
		CLASSIFY_ENTITY entInput = entities.get(0);
		entities.remove(0);
		
		/////////////////////////////////////////////////////////////////////////////////////
		Collections.shuffle(entities);
		ArrayList<CLASSIFY_ENTITY> testDataSet = new ArrayList<CLASSIFY_ENTITY>();
		ArrayList<CLASSIFY_ENTITY> buildDataSet = new ArrayList<CLASSIFY_ENTITY>();
		for (int i = 0; i < entities.size(); i++)
		{
			if (i < entities.size()/5)
				testDataSet.add(entities.get(i));
			else 
				buildDataSet.add(entities.get(i));
		}
		
		entities = null;
		System.gc();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException exp) {
			exp.printStackTrace();
		}
		/////////////////////////////////////////////////////////////////////////////////////
		
		ClassifyModel cls = new ClassifyModel();
		//ModelTuneResult res = cls.modelTuning(testDataSet, buildDataSet);
		//System.out.println(res.m_dbCurAccuracy);
		/****************************************************************************************/
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		String tmStart = df.format(new Date());
		cls.useLinear();
		cls.BuildModel(buildDataSet);
		String tmEnd = df.format(new Date());
		
		System.out.println("\n*****************  Training time  *****************\n");
		System.out.println(tmStart);
		System.out.println(tmEnd);
		
		PROB_ESTIMATION_RES res = cls.Classify(entInput.vectors);
		
		System.out.println("\n***************************************\n");
		System.out.print("  The predicted class is  ");
		System.out.println(res.nClsId);
		
		for (int i = 0; i < res.probRes.size(); i++)
		{
			System.out.print("   ");
			System.out.print(res.probRes.get(i).nClsId);
			System.out.print("  ===============>  ");
			System.out.println(res.probRes.get(i).dbProb);
		}
		
		System.out.println("cross validation result : ");
		double validationRes = cls.crossValidation(testDataSet);
		System.out.print(validationRes);
		System.out.println("");
		/********************************************************************************************/
	}
} 