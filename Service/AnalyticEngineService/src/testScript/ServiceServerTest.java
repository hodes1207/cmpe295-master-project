package testScript;

import java.io.IOException;
import java.util.ArrayList;

import database.Domain;
import database.SecondLevelClass;
import datamining.PROB_ESTIMATION_RES;

import MessageLayer.CommunicationAPI;
import MessageLayer.ImgServerInfo;

public class ServiceServerTest {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		String ip = "127.0.0.1";
		int port = 3456;
		if (args.length == 2)
		{
			ip = args[0];
			port = Integer.parseInt(args[1]);
		}
		
		CommunicationAPI comAPI = new CommunicationAPI(ip, port);
		
		long startTime, endTime;
		
		startTime = System.currentTimeMillis();
		ArrayList<Domain> doms = comAPI.GetDomain();
		endTime = System.currentTimeMillis();
		System.out.println("GetDomain() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		comAPI.SetLinearKernelParam(1, 0.5, 1001);
		endTime = System.currentTimeMillis();
		System.out.println("SetLinearKernelParam() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		ArrayList<SecondLevelClass> clses = comAPI.GetClasses(0);
		endTime = System.currentTimeMillis();
		System.out.println("GetClasses() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		ArrayList<Long> picIds = comAPI.GetPicId((1 << 16) + 1);
		endTime = System.currentTimeMillis();
		System.out.println("GetPicId() : " + (endTime - startTime));

		startTime = System.currentTimeMillis();
		byte[] content = comAPI.RetrieveImg(picIds.get(33));
		endTime = System.currentTimeMillis();
		System.out.println("RetrieveImg() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		PROB_ESTIMATION_RES info = comAPI.classificationEstimation(content, 1);
		endTime = System.currentTimeMillis();
		System.out.println("classificationEstimation() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		ArrayList<Long> pics = comAPI.SimilaritySearch(content, 100, 1);
		endTime = System.currentTimeMillis();
		System.out.println("SimilaritySearch() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		ArrayList<ImgServerInfo> infos = comAPI.getImgServerInfo();
		endTime = System.currentTimeMillis();
		System.out.println("getImgServerInfo() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		boolean b = comAPI.AddImg((1 << 16) + 1, 1234567, content);
		endTime = System.currentTimeMillis();
		System.out.println("AddImg() : " + (endTime - startTime));
		
		startTime = System.currentTimeMillis();
		b = comAPI.DeleteImg((1 << 16) + 1, 1234567);
		endTime = System.currentTimeMillis();
		System.out.println("DeleteImg() : " + (endTime - startTime));
		
		//comAPI.startTraining(1);
		//comAPI.StartAutoTuning(1);
		
		/*double[] accuracy = new double[1];
		boolean b1 = comAPI.getModelAccuracy(1, 1, accuracy);
		
		boolean res[] = new boolean[1];
		boolean b2 = comAPI.getTrainingInfo(1, 1, res);
		
		String[] str = new String[1];
		boolean b3 = comAPI.getTuningInfo(1, 1, str);
		
		int t = 0;
		int i = t;
		/*picIds = comAPI.SimilaritySearch(content, 100);

		boolean b = comAPI.AddImg((1 << 16) + 1, 1234567, content);
		b = comAPI.DeleteImg((1 << 16) + 1, 1234567);
		
		//training 
		b = comAPI.SetLinearKernelParam(1, 0.5, 1001);
		b = comAPI.SetRBFKernelParam(1, 0.03129, 0.002, 1002);
		
		comAPI.startTraining(1);
		
		//tuning 
		comAPI.enableRBFTuning(1);
		b = comAPI.isRBFTuningEnabled(1);
		b = comAPI.enableRBFTuning(1);
		
		int x = comAPI.GetAutoTuningFoldNum(1);
		b = comAPI.SetAutoTuningFoldNum(1, 7);
		comAPI.StartAutoTuning(1);*/
	}

}
