package database;

import java.io.Serializable;
import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties({"id", "revision", "_attachments"})
public class MedicalImage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("_id")
	public String id;

    @JsonProperty("_rev")
    public String revision;
        
    public int domainId; 
	
    public int classId; 
	
    public long imageId;  //using time stamp as the seed to generate random number
	
    public byte[] image = null;
	
    public ArrayList<Double> featureV; 
    
    @SuppressWarnings("unchecked")
	public MedicalImage clone()
    {
    	MedicalImage ret = new MedicalImage();
    	
    	ret.id = id;
    	ret.classId = classId;
    	ret.domainId = domainId;
    	ret.featureV = (ArrayList<Double>)featureV.clone();
    	if (null != image)
    		ret.image = image.clone();
    	ret.revision = revision;
    	ret.imageId = imageId;
    	
    	return ret;
    }
	
	public MedicalImage(){
		domainId = -1;
		classId = -1;
		imageId = -1;
		featureV = null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public long getImageId() {
		return imageId;
	}

	public void setImageId(long imageId) {
		this.imageId = imageId;
	}

	public ArrayList<Double> getFeatureV() {
		return featureV;
	}

	public void setFeatureV(ArrayList<Double> featureV) {
		this.featureV = featureV;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	
}
