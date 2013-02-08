package testScript;

import imgproc.ImgFeatureExtractionWrapper;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import datamining.Normalizer;

public class Util {

	public static void normalize(ArrayList<ImgFeatureInfo> imgsInfo)
	{
		if (imgsInfo.size() <= 0)
			return;
		
		Normalizer nolizr = new Normalizer(ImgFeatureExtractionWrapper.TOTAL_DIM);
		
		for (int i = 0; i < imgsInfo.size(); i++)
		{
			nolizr.InitialScan(imgsInfo.get(i).features);
		}

		for (int i = 0; i < imgsInfo.size(); i++)
		{
			nolizr.normalizeVector(imgsInfo.get(i).features);
		}
	}
	
	public static String getExtension(String strFile)
	{
		if (null == strFile)
			return "";
		
		int i = strFile.indexOf('.');
		if (i < 0)
			return "";
		
		return strFile.substring(i+1);
	}
	
	public static void recursiveGetFiles(File f, ArrayList<String> files)
	{
		File[] FList = f.listFiles();
		for (int i = 0; i < FList.length; i++)
		{
			if (FList[i].isDirectory()==true)
			{
				recursiveGetFiles(FList[i], files);
			}
			else
			{
				String strPath = FList[i].getAbsolutePath();
				String strExt = getExtension(strPath);
				if (strExt.equalsIgnoreCase("jpg") || strExt.equalsIgnoreCase("bmp")
						|| strExt.equalsIgnoreCase("png") || strExt.equalsIgnoreCase("gif"))
					files.add(strPath);
			}
		}
	}
	
	public static double[] retrieveImgFeature(String strPath) throws IOException
	{
		 BufferedInputStream in = new BufferedInputStream(new FileInputStream(strPath));   
		 ByteArrayOutputStream out = new ByteArrayOutputStream(1024);   
		 byte[] temp = new byte[1024];   
		 int size = 0;   
		 while ((size = in.read(temp)) != -1) 
		 {   
			 out.write(temp, 0, size);   
		 }   
		 in.close();   

		 byte[] content = out.toByteArray();    
		 double[] vectors = new double[ImgFeatureExtractionWrapper.TOTAL_DIM];
		 ImgFeatureExtractionWrapper.extractFeature(content, vectors);
		 
		 content = null;
		 in = null;
		 out = null;
		 
		 return vectors;
	}
	
}
