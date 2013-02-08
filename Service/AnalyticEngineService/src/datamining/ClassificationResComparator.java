package datamining;

import java.util.Comparator;

public class ClassificationResComparator implements Comparator<CLASSIFY_RES>{
	
	public int compare(CLASSIFY_RES a, CLASSIFY_RES b) 
	{
        return a.dbProb < b.dbProb ? -1 : 1;
	}
}

