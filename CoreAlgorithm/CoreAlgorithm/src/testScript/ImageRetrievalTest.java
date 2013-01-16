package testScript;

import java.io.*;   
import java.util.ArrayList;
import java.util.Collections;

public class ImageRetrievalTest {

	public static void main(String[] args) throws IOException
	{
		/********************** parameters ************************************************************/
		String strCmprImg = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train12\\5072.png";
		
		String strRepFolder1 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\IRMA\\2005\\Train12";
		//String strRepFolder2 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\chest";
		//String strRepFolder3 = "C:\\Users\\yhx176781066\\Desktop\\Master Project\\images\\hand";
		/**********************************************************************************************/
		
		//get all images in the specified folders
		ArrayList<String> folders = new ArrayList<String>();
		folders.add(strRepFolder1);
		//folders.add(strRepFolder2);
		//folders.add(strRepFolder3);
		
		ArrayList<String> imgs = new ArrayList<String>();
		imgs.add(strCmprImg);
		for (int i = 0; i < folders.size(); i++)
		{
			File file = new File(folders.get(i));
			Util.recursiveGetFiles(file, imgs);
		}
		
		ArrayList<ImgFeatureInfo> imgsInfo = new ArrayList<ImgFeatureInfo>();
		for (int i = 0; i < imgs.size(); i++)
		{
			double[] f = Util.retrieveImgFeature(imgs.get(i));
			ImgFeatureInfo info = new ImgFeatureInfo();
			info.strImgPath = imgs.get(i);
			info.features = f;
			imgsInfo.add(info);
			
			if (i%100 == 0)
				System.gc();
		}
		
		//normalize vectors
		Util.normalize(imgsInfo);
		
		//sort the images according to vector distance
		ImgFeatureInfo cmprImgInfo = imgsInfo.get(0);
		imgsInfo.remove(0);
		
		ImgFeatureComparator comp = new ImgFeatureComparator(cmprImgInfo);
		Collections.sort(imgsInfo, comp);
		
		for (int i = 0; i < imgsInfo.size(); i++)
			System.out.println(imgsInfo.get(i).strImgPath);
	}
}
