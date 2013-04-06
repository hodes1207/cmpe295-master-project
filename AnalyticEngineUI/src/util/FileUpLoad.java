package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemStream;

public class FileUpLoad {

	private static byte[] image;
	
	public static boolean processFile(String path, FileItemStream itemStr)
	{
		try{
			//File f = new File(path+File.separator+"images");
			//if(!f.exists()) f.mkdir();
			//File savedFile = new File(f.getAbsolutePath()+File.separator+itemStr.getName());
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
			InputStream in = itemStr.openStream(); 
			int x = 0; 
			byte[] b = new byte[1024];
			while((x=in.read(b))!= -1)
			{
				out.write(b, 0, x);
			}
			
			image = out.toByteArray();

			//out.flush();
			//out.close();
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public static byte[] getImageByteAry()
	{
		return image;
	}
	

}
