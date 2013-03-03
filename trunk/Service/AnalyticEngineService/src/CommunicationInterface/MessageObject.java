/**
 * 
 */
package CommunicationInterface;
import database.*;
import java.util.*;
import java.io.*;

/**
 * @author pramod
 *
 */
public class MessageObject implements Serializable {
	
	public long len;
	public MsgId qtype;
	public ArrayList<Long> longlist;
	public ArrayList<Domain> domlist;
	public byte[] retbyte;
	public String sval;
	public RetID rtype;
	public int domid;
	




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
	
	public ArrayList<Long> getlist () {
		return longlist;
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
	public byte[] getbytes () {
		return retbyte;
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
}
