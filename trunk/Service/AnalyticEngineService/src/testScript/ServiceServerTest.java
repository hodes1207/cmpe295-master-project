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
		CommunicationAPI comAPI = new CommunicationAPI("127.0.0.1", 3456);
		
		ArrayList<Domain> doms = comAPI.GetDomain();
		//comAPI.SetLinearKernelParam(1, 0.5, 1001);
		//Thread.sleep(20000);
		
		ArrayList<SecondLevelClass> clses = comAPI.GetClasses(0);
		ArrayList<Long> picIds = comAPI.GetPicId((1 << 16) + 1);

		/*for(int i = 0; i < picIds.size(); i++)
		{
			byte[] content = comAPI.RetrieveImg(picIds.get(0));
			System.out.println("content "+i+": "+content);
		}*/
		
		byte[] content = comAPI.RetrieveImg(picIds.get(33));
		
		//double dbTmp = comAPI.getModelAccuracy(1);
		/*for (int i = 0; i < 1000; i++)
		{
			System.out.println(i);
			PROB_ESTIMATION_RES info = comAPI.classificationEstimation(content, 1);
			ArrayList<Long> pics = comAPI.SimilaritySearch(content, 100);
		}*/
		
		ArrayList<ImgServerInfo> infos = comAPI.getImgServerInfo();
		
		comAPI.startTraining(1);
		comAPI.StartAutoTuning(1);
		
		double[] accuracy = new double[1];
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
