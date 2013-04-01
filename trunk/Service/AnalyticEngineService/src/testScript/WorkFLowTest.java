package testScript;

import java.util.ArrayList;

import database.Domain;
import database.MedicalParameter;
import database.SecondLevelClass;

import ServiceInterface.EngineService;;

public class WorkFLowTest {

	public static void main(String[] args)
	{
		try 
		{
			EngineService serv = new EngineService();
			serv.startService();
			
			while (serv.getInitProgress() < 1.0)
			{
				System.out.println(serv.getInitProgress());
				Thread.sleep(3000);
			}
			
			//basic function test
			ArrayList<Domain> domains = serv.GetDomain();
			ArrayList<SecondLevelClass> clses = serv.GetClasses(domains.get(1).domainId);
			
			ArrayList<Long> ids = serv.GetPicId((1<<16)+1);
			Long testId = ids.get(0);
			byte[] content = serv.RetrieveImg(testId);
			//boolean bRes = serv.DeleteImg((1<<16)+1, testId);
			//bRes = serv.AddImg((1<<16)+1, testId, content);
			
			//ArrayList<Long> res = serv.SimilaritySearch(content, 100);
			//for (int i = 0; i < res.size(); i++)
			//	System.out.println(res.get(i));
			
			//String clsRes = serv.classificationEstimation(content, 1);
			//System.out.println(clsRes);
			
			/*serv.SetRBFKernelParam(-1, 0.03124, 0.0011, 998);
			serv.SetAutoTuningFoldNum(-1, 6);
			int nNum = serv.GetAutoTuningFoldNum(-1);
			double dbp = serv.getAutoTuningProgress(-1);
			String strInfo = serv.getAutoTuningInfo(-1);*/
			MedicalParameter strInfo = serv.GetCurrentModelInfo(-1);
			double dbacu = serv.GetCurrentModelAccuracy(-1);
			serv.SetLinearKernelParam(-1, 0.03124, 3000);
			
			serv.StartAutoTuning(-1);
			//serv.StartAutoTuning(0);
			//serv.StartAutoTuning(1);
			
			while (serv.getAutoTuningProgress(-1) < 1.0
					//|| serv.getAutoTuningProgress(0) < 1.0
					//|| serv.getAutoTuningProgress(1) < 1.0
					)
			{
				System.out.println("\n*********************************************************");
				System.out.println(serv.getAutoTuningInfo(-1));
				//System.out.println(serv.getAutoTuningInfo(0));
				//System.out.println(serv.getAutoTuningInfo(1));
				System.out.println("*********************************************************\n");
				Thread.sleep(4000);
			}
			
			Thread.sleep(1000000000);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
	}
	
}
