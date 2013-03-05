/**
 * 
 */
package CommunicationInterface;

import java.io.*;



import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import database.*;



/**
 * @author pramod
 *
 */
public class CommunicationAPI implements Serializable {

	private MessageObject query;
	private ServerConnection scon;
	/**
	 * 
	 */
	public CommunicationAPI() throws IOException {
		// TODO Auto-generated constructor stub
	    this.init();
	}
	
	public void init() throws IOException {
		query = new MessageObject();
		scon = new ServerConnection();
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
		
		public double getAutoTuningProgress(int nDomainId)
		{
			double res = 0.0;
            query.settype(MsgId.GET_ATP);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.DOUBLE) {
			    	 res = result.getdval();
			     }
			} catch (IOException e) {
	
				HandleException(e, MsgId.GET_ATP);
			}
			
			return res;
			
		}
		
		public String getAutoTuningInfo(int nDomainId)
		{
			String res =  null;
            query.settype(MsgId.GET_ATI);
            query.setdomid(nDomainId);
            query.setStrval(null);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.STRING) {
			    	 res = result.getStrVal();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.GET_ATI);
			}
			
			return res;
		
		}
		
		//not in the auto tuning process
		public String GetCurrentModelInfo(int nDomainId)
		{
			String res =  null;
            query.settype(MsgId.GET_CMI);
            query.setdomid(nDomainId);
            query.setStrval(null);
            query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.STRING) {
			    	 res = result.getStrVal();
			     }
			} catch (IOException e) {
		
				HandleException(e, MsgId.GET_ATP);
			}
			
			return res;
		}
		
		

		public boolean StartAutoTuning(int nDomainId)
		{
			boolean res = false;
            query.settype(MsgId.START_TUNE);
            query.setdomid(nDomainId);
            query.setrettype(RetID.INVALID);
			
			try {
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
		public ArrayList<Long> SimilaritySearch(byte[] byteImg, int nNum)
		{
			ArrayList<Long> res = null;
			query.setbytes(byteImg);
			query.setintval(nNum);
			query.setlist(null);
			
			query.settype(MsgId.SEARCH_SIM);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.LONG_LIST) {
			    	 res = result.getlist();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.SEARCH_SIM);
			}
			
			return res;
		}

		//String format:  classID+¡±:¡±+classification percentage, ranked by percentage
		public String classificationEstimation(byte[] img, int nDomainId)
		{
			String res = null;
			query.setbytes(img);
			query.setdomid(nDomainId);
			query.settype(MsgId.GET_CLEST);
			query.setStrval(null);
			query.setrettype(RetID.INVALID);
			
			try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.STRING) {
			    	 res = result.getStrVal();
			     }
			} catch (IOException e) {
				
				HandleException(e, MsgId.GET_CLEST);
			}
			
			return res;
		}
		
		//================== Initialize status API =================================
		public double getInitProgress()
		{
		   double res = 0.0;
		   
		   query.settype(MsgId.GET_PROG);
		   query.setrettype(RetID.INVALID);
		   
		   try {
			     scon.sendmsg(query);
			     MessageObject result = (MessageObject) scon.getmsg(query);
			     
			     if (result.getrettype() == RetID.DOUBLE) {
			    	 res = result.getdval();
			     }
			} catch (IOException e) {
				HandleException(e, MsgId.GET_PROG);
			}
			
			return res;
		}
		/************************************************************************************/
		
		private void HandleException (IOException e, MsgId id) {
			
		     System.out.println("I/O Error "+ e.getMessage() + " processing " + id);
		     e.getStackTrace();
			
		}

}
