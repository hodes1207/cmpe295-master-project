package database;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class DomainRepository extends CouchDbRepositorySupport<Domain>{

	public DomainRepository(CouchDbConnector db) {
		super(Domain.class, db);
		initStandardDesignDocument();
	}
	
}
