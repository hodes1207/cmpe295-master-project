package database;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties({"id", "revision"})
public class Domain {

	@JsonProperty("_id")
	public String id;

    @JsonProperty("_rev")
	private String revision;
    
    public String domainName;
	public int domainId;
	public MedicalParameter medicalParameter;
	
	public Domain(String domoainN, int id){
		domainName = domoainN;
		domainId = id;
		medicalParameter = new MedicalParameter();
	}


	public Domain(){}

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

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public int getDomainId() {
		return domainId;
	}

	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public MedicalParameter getMedicalParameter() {
		return medicalParameter;
	}

	public void setMedicalParameter(MedicalParameter medicalParameter) {
		this.medicalParameter = medicalParameter;
	}
	
	
	
}
