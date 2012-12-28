
import imgproc.ImgFeatureExtractionWrapper;

import java.io.*;   

public class Main {
	public static void main(String[] args) throws IOException
	{
		 BufferedInputStream in = new BufferedInputStream(new FileInputStream("hand.jpg"));   
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
	}
}
