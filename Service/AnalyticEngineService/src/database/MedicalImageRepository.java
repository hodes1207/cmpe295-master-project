package database;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.View;
import org.ektorp.support.Views;
@Views({
@View( name="image_view", map = "function(doc) { if (doc.domainId >= 0 && doc.classId >= 0)  emit([doc.domainId,doc.classId], doc)  }"),
@View( name="imageId_view", map = "function(doc) { if (doc.domainId >= 0 && doc.classId >= 0)  emit([doc.domainId,doc.classId], doc.imageId)  }"),
@View( name="domainId_classId_view", map = "function(doc) { emit([doc.domainId, doc.classId],1)  }", reduce = "function (keys, values) { return sum(values);}"),
@View( name="imageId_docId_view", map = "function(doc) { if (doc.domainId >= 0)  emit(doc.imageId, doc._id)  }")
})
public class MedicalImageRepository extends CouchDbRepositorySupport<MedicalImage>{
	public MedicalImageRepository(CouchDbConnector db) {
		super(MedicalImage.class, db);
		initStandardDesignDocument();
	}

}
