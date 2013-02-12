package database;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.CouchDbRepositorySupport;

public class SecondLevelClassRepository extends CouchDbRepositorySupport<SecondLevelClass>{
	public SecondLevelClassRepository(CouchDbConnector db) {
		super(SecondLevelClass.class, db);
	}
	
}
