/**
 * 
 */
package MessageLayer;
import database.*;
import datamining.PROB_ESTIMATION_RES;

import java.util.*;
import java.io.*;


/**
 * @author pramod
 *
 */
public class MessageObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	// The clientid is unique to every client connection and is used to
	// identify multiple client connections.
	private static int clientid = new Random().hashCode();
	public long len;
	public MsgId qtype;
	public ArrayList<Long> longlist = null;
	public ArrayList<Domain> domlist = null;
	public ArrayList<SecondLevelClass> slclist = null;
	public byte[] retbyte = null;
	public String sval = null;
	public RetID rtype;
	public int domid;
	public int classid;
	public int maxsamples;
	public long imageid;
	public boolean bval;
	public int ival;
	public double dval;
	public double rbf_c, rbf_g;
	public double modelAccuracy;
	public boolean trainingInProgress;
	public ArrayList<ImgServerInfo> imgServInfo = null;
	public PROB_ESTIMATION_RES classifyRes = null;
	
	/**
	 * 
	 */
	public MessageObject(MsgId id) {
		// TODO Auto-generated constructor stub
		this.qtype = id;
		
	}
	
	public MessageObject(MsgId id, RetID rid) {
		// TODO Auto-generated constructor stub
		this.qtype = id;
		this.rtype = rid;
	}
	
	
	public MessageObject() {
		this.qtype = MsgId.UNINIT;
		this.len = 0;
		this.rtype = RetID.INVALID;
	}
	
	public void setclassid(int id) {
		classid = id;
	}
	
	public int getclassid() {
		return classid;
	}
	
	public void setlen (long l) {
		len = l;
	}
	
	public void settype (MsgId id) {
		qtype = id;
	}
	
	public MsgId gettype () {
		return qtype;
	}

	public long getlen () {
		return len;
	} 
	
	public long getimageid() {
		return imageid;
	}
	
	public void setimageid(long id) {
		imageid = id;
	}
	
	public ArrayList<Long> getlist () {
		return longlist;
	}
	
	public void setlist (ArrayList<Long> l) {
		longlist = l;
	}
	
	public ArrayList<Domain> getdomlist () {
		return domlist;
	}
	
	public void setdomlist (ArrayList<Domain> dl) {
		domlist = dl;
	}
	
	public int getdomid () {
		return domid;
	}
	
	public void setdomid (int id) {
		domid = id;
	}
	public int getmaxsample () {
		return maxsamples;
	}
	
	public void setmaxsample (int id) {
		maxsamples = id;
	}
	public byte[] getbytes () {
		return retbyte;
	}
	
	public void setbytes(byte[] inbyte) {
		retbyte = inbyte;
	}
	
	public String getstring() {
		return sval;
	}
	
	public void setrettype (RetID type) {
		rtype = type;
	}
	
	public RetID getrettype () {
		return rtype;
	}
	
	public String getStrVal() {
		return sval;
	}
	
	public void setStrval (String val) {
		sval = val;
	}
	
	public boolean getboolval () {
		return bval;
	}
	
	public void setboolval(boolean b) {
		bval = b;
	}
	
	public int getintval() {
		return ival;
	}
	
	public void setintval (int i) {
		ival = i;
	}
	
	public void setslclist (ArrayList<SecondLevelClass> list) {
		slclist = list;
	}
	
	public ArrayList<SecondLevelClass> getslclist() {
		return slclist;
	}
	
	public void setc (double c) {
		rbf_c = c;
	}
	
	public void setg (double g) {
		rbf_g = g;
	}
	
	public double getc () {
		return rbf_c;
	}
	
	public double getg () {
		return rbf_g;
	}
	
	public double getdval() {
		return dval;
	}
	
	public void setdval(double d) {
		dval = d;
	}
	
	public int getclientid() {
		return clientid;
	}
}