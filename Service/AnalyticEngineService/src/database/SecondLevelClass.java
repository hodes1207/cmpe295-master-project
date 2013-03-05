package database;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import java.io.*;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties({"id", "revision"})
public class SecondLevelClass implements Serializable {

	@JsonProperty("_id")
	private String id;

    @JsonProperty("_rev")
	private String revision;
    public String className;
	public int classId;
	
	public SecondLevelClass(String classN, int id)
	{
		className = classN;
		classId = id;
	}
	
	public SecondLevelClass(){}

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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}
	
	
	
}
