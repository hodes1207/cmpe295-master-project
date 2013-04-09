package testScript;

import java.io.IOException;
import java.util.ArrayList;

import database.Domain;
import database.MedicalParameter;
import database.SecondLevelClass;

import MessageLayer.CommunicationAPI;

public class ServiceServerTest {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		CommunicationAPI comAPI = new CommunicationAPI("127.0.0.1", 3456);
		
		ArrayList<Domain> doms = comAPI.GetDomain();
		//Thread.sleep(20000);
		
		ArrayList<SecondLevelClass> clses = comAPI.GetClasses(0);
		ArrayList<Long> picIds = comAPI.GetPicId((1 << 16) + 1);

		/*for(int i = 0; i < picIds.size(); i++)
		{
			byte[] content = comAPI.RetrieveImg(picIds.get(0));
			System.out.println("content "+i+": "+content);
		}*/
		
		byte[] content = comAPI.RetrieveImg(picIds.get(0));
		
		double dbTmp = comAPI.getModelAccuracy(1);
		String info = comAPI.classificationEstimation(content, 1);
		
		picIds = comAPI.SimilaritySearch(content, 100);

		boolean b = comAPI.AddImg((1 << 16) + 1, 1234567, content);
		b = comAPI.DeleteImg((1 << 16) + 1, 1234567);
		
		//training 
		b = comAPI.SetLinearKernelParam(1, 0.03137, 1001);
		b = comAPI.SetRBFKernelParam(1, 0.03129, 0.002, 1002);
		MedicalParameter mdInfo = comAPI.GetCurrentModelInfo(1);
		comAPI.startTraining(1);
		
		//tuning 
		comAPI.enableRBFTuning(1);
		b = comAPI.isRBFTuningEnabled(1);
		b = comAPI.disableRBFTuning(1);
		
		int x = comAPI.GetAutoTuningFoldNum(1);
		b = comAPI.SetAutoTuningFoldNum(1, 7);
		comAPI.StartAutoTuning(1);
		
		while (comAPI.getAutoTuningProgress(1) < 1.0)
		{
			System.out.println(comAPI.getAutoTuningInfo(1));
			System.out.println(comAPI.getAutoTuningProgress(1));
			System.out.println("*******************************************");
			Thread.sleep(3000);
		}
	}

}
