/**
 * 
 */
package MessageLayer;

import java.io.*;



import java.util.ArrayList;
import database.*;
import datamining.PROB_ESTIMATION_RES;

/**
 * @author pramod
 *
 */
public class CommunicationAPI {

	private MessageObject query;
	private ServerConnection scon;

	public CommunicationAPI(String servIP, int servPort) throws IOException {
		
	    this.init(servIP, servPort);
	}
	
	public void init(String servIP, int servPort) throws IOException {
		query = new MessageObject();
		scon = new ServerConnection(servIP, servPort);
	}
	
	public void close() throws IOException {
		scon.close();
		 // Does this speed up garbage collection ?
		query = null;
		scon = null;
	}
	
	//=================== Image management API =========================

		public ArrayList<Long> GetPicId (int nClassId)
		{
			ArrayList<Long> res = null;
			query.setclassid(nClassId);
			query.settype(MsgId.GET_PICID);
			query.setlist(null);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.LONG_LIST) {
			    	 res = result.getlist();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.GET_PICID);
			}
			
			return res;
		}

		public byte[] RetrieveImg(long nImgId) 
		{
			byte[] res = null;
			
			query.setimageid(nImgId);
			query.settype(MsgId.GET_IMAGE);
			query.setbytes(null);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BYTES) {
			    	 res = result.getbytes();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.GET_IMAGE);
			}
			
			return res;
		}

		public boolean DeleteImg(int nClassId, long nImgId) 
		{
			boolean res = false;
			query.setimageid(nImgId);
			query.setclassid(nClassId);
			query.settype(MsgId.DEL_IMAGE);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
			
				HandleException(e, MsgId.DEL_IMAGE);
			}
			
			return res;
		}

		public boolean AddImg(int nClassId, long nImgId, byte[] byteImg)
		{
			boolean res = false;
			query.setimageid(nImgId);
			query.setclassid(nClassId);
			query.setbytes(byteImg);
			query.settype(MsgId.ADD_IMAGE);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
			
				HandleException(e, MsgId.ADD_IMAGE);
			}
			
			return res;
			
		}

		//scope: first level classification
		public ArrayList<Domain> GetDomain()
		{
			ArrayList<Domain> res = null;
            query.settype(MsgId.GET_DOMAIN);
            query.setdomlist(null);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.DOMAIN_LIST) {
			    	 res = result.getdomlist();
			     }
			} catch (IOException e) {
				HandleException(e, MsgId.GET_DOMAIN);
				
			}
			
			return res;
		}

		//second level classification
		public ArrayList<SecondLevelClass> GetClasses(int nDomainId)
		{
			ArrayList<SecondLevelClass> res = null;
            query.settype(MsgId.GET_CLASS);
            query.setdomid(nDomainId);
            query.setslclist(null);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.CLASS_LIST) {
			    	 res = result.getslclist();
			     }
			} catch (IOException e) {
			
				HandleException(e, MsgId.GET_CLASS);
			}
			
			return res;
		}

		//================== Model tuning API ================================

		public boolean SetRBFKernelParam(int nDomainId, double c, double g, int nMaxSamples)
		{
            boolean res = false;
			query.settype(MsgId.SET_RBFKP);
			query.setdomid(nDomainId);
			query.setc(c);
			query.setg(g);
			query.setmaxsample(nMaxSamples);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.SET_RBFKP);
			}
			
			return res;
		}

		public boolean SetLinearKernelParam(int nDomainId, double c, int nMaxSamples)
		{
			boolean res = false;
			query.settype(MsgId.SET_LKP);
			query.setdomid(nDomainId);
			query.setc(c);
			query.setmaxsample(nMaxSamples);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
			
				HandleException(e, MsgId.SET_LKP);
			}
			
			return res;
		}

		public int GetAutoTuningFoldNum(int nDomainId)
		{
			int res = 0;
            query.settype(MsgId.GET_ATFN);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.INT) {
			    	 res = result.getintval();
			     }
			} catch (IOException e) {
		
				HandleException(e, MsgId.GET_ATFN);
			}
			
			return res;

			
		}

		public boolean SetAutoTuningFoldNum(int nDomainId, int nFold)
		{
			boolean res = false;
            query.settype(MsgId.SET_ATFN);
            query.setdomid(nDomainId);
            query.setintval(nFold);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.SET_ATFN);
			}
			
			return res;
		}
		
		public boolean StartAutoTuning(int nDomainId)
		{
			boolean res = false;
            query.settype(MsgId.START_TUNE);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try 
			{
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
			
				HandleException(e, MsgId.START_TUNE);
			}
			
			return res;
		}
		
		public boolean startTraining(int nDomainId)
		{
			boolean res = false;
            query.settype(MsgId.START_TRAIN);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.START_TRAIN);
			}
			
			return res;
			
		}
		
		public boolean enableRBFTuning(int nDomainId)
		{
			boolean res = false;
            query.settype(MsgId.ENABLE_RBF);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.ENABLE_RBF);
			}
			
			return res;
		}
		
		public boolean disableRBFTuning(int nDomainId)
		{
			boolean res = false;
            query.settype(MsgId.DISABLE_RBF);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.DISABLE_RBF);
			}
			
			return res;
		}
		
		public boolean isRBFTuningEnabled(int nDomainId)
		{
			boolean res = false;
            query.settype(MsgId.CHECK_RBF);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.BOOL) {
			    	 res = result.getboolval();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.CHECK_RBF);
			}
			
			return res;
		}

		//================== Recommendation API ====================================
		//return a list of picture ID (nNum pictures)
		public ArrayList<ImgDisResEntry> SimilaritySearch(byte[] byteImg, int nNum, int domainId)
		{
			ArrayList<ImgDisResEntry> res = null;
			query.setbytes(byteImg);
			query.setintval(nNum);
			query.setlist(null);
			query.setdomid(domainId);
			
			query.settype(MsgId.SEARCH_SIM);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.IMG_DIS_LIST) {
			    	 res = result.imgDisList;
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.SEARCH_SIM);
			}
			
			return res;
		}

		//String format:  classID+¡±:¡±+classification percentage, ranked by percentage
		public PROB_ESTIMATION_RES classificationEstimation(byte[] img, int nDomainId)
		{
			PROB_ESTIMATION_RES res = null;
			query.setbytes(img);
			query.setdomid(nDomainId);
			query.settype(MsgId.GET_CLEST);
			query.setStrval(null);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.CLS_RES) {
			    	 res = result.classifyRes;
			     }
			} catch (IOException e) {
				HandleException(e, MsgId.GET_CLEST);
			}
			
			return res;
		}
		
		public ArrayList<ImgServerInfo> getImgServerInfo()
		{
			ArrayList<ImgServerInfo> ret = null;
            query.settype(MsgId.GET_IMGSERV);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.IMGSERV_LIST) 
			     {
			    	 ret = result.imgServInfo;
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.GET_IMGSERV);
			}
			
			return ret;
		}

		//all function below returns true if api call success, 
		//false if api call failed (usually due to connection failure of image  server)
		//accuracy[0] is the accuracy of the specific model
		public boolean getModelAccuracy(int serverIndex, int modleId, double[] accuracy)
		{
            query.settype(MsgId.GET_MODEL_ACCURACY);
            query.setdomid(modleId);
            query.setintval(serverIndex);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			    
			     if (result.getrettype() == RetID.TIMEOUT)
			    	 return false;
			     
			     accuracy[0] = result.modelAccuracy;
			    
			} catch (IOException e) {
				
				HandleException(e, MsgId.GET_MODEL_ACCURACY);
			}
			
			return true;
		}
		
		public boolean getSysPerfInfo(int serverIndex, SysPerfInfo[] info)
		{
			query.settype(MsgId.GET_PERF_INFO);
			query.setintval(serverIndex);
			query.setrettype(RetID.INVALID);
			
			try 
			{
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.TIMEOUT)
			    	 return false;
			     
			     info[0] = result.sysInfo;
			} 
			catch (IOException e) 
			{
				HandleException(e, MsgId.GET_PERF_INFO);
			}
			
			return true;
		}
		
		public boolean getTuningInfo(int serverIndex, int modleId, String[] info)
		{
            query.settype(MsgId.GET_MODEL_TUNINGINFO);
            query.setintval(serverIndex);
            query.setdomid(modleId);
            query.setrettype(RetID.INVALID);
			
			try 
			{
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.TIMEOUT)
			    	 return false;
			     
			     info[0] = result.getstring();
			} 
			catch (IOException e) 
			{
				HandleException(e, MsgId.GET_MODEL_TUNINGINFO);
			}
			
			return true;
		}

		//info[0] ==> true: training in progress, false: not in  progress
		
		public boolean getTrainingInfo(int serverIndex, int modleId, boolean[] info)
		{
            query.settype(MsgId.GET_MODEL_TRAININGINFO);
            query.setdomid(modleId);
            query.setintval(serverIndex);
            query.setrettype(RetID.INVALID);
			
			try 
			{
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.TIMEOUT)
			    	 return false;
			    
			     info[0] = result.trainingInProgress;
			} 
			catch (IOException e) 
			{
				HandleException(e, MsgId.GET_MODEL_TRAININGINFO);
			}
			
			return true;
		}
		
		/************************************************************************************/
		
		private void HandleException (IOException e, MsgId id) {
			
		     System.out.println("II/O Error "+ e.getMessage() + " processing " + id);
		     e.getStackTrace();
			
		}

}
