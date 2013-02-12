package database;

public class MedicalParameter {

	public boolean bRBF;
	public double dbRBF_c;
	public double dbRBF_g;
	public double dbLinear_c;
	public int nMaxSampleNum;
	public int nFold;
	
	public MedicalParameter()
	{
		bRBF = false;
		dbRBF_c = 0.03125;
		dbRBF_g = 0.001;
		dbLinear_c = 0.03125;
		nMaxSampleNum = 1000;
		nFold = 5;
	}

	public boolean isbRBF() {
		return bRBF;
	}

	public void setbRBF(boolean bRBF) {
		this.bRBF = bRBF;
	}

	public double getDbRBF_c() {
		return dbRBF_c;
	}

	public void setDbRBF_c(double dbRBF_c) {
		this.dbRBF_c = dbRBF_c;
	}

	public double getDbRBF_g() {
		return dbRBF_g;
	}

	public void setDbRBF_g(double dbRBF_g) {
		this.dbRBF_g = dbRBF_g;
	}

	public double getDbLinear_c() {
		return dbLinear_c;
	}

	public void setDbLinear_c(double dbLinear_c) {
		this.dbLinear_c = dbLinear_c;
	}

	public int getnMaxSampleNum() {
		return nMaxSampleNum;
	}

	public void setnMaxSampleNum(int nMaxSampleNum) {
		this.nMaxSampleNum = nMaxSampleNum;
	}

	public int getnFold() {
		return nFold;
	}

	public void setnFold(int nFold) {
		this.nFold = nFold;
	}
	
	@Override
	public String toString()
	{
		StringBuilder temp = new StringBuilder();
		temp.append("Medical Parameters:"+"\n");
		temp.append("bRBF:"+bRBF+"\n");
		temp.append("dbRBF_c:"+dbRBF_c+"\n");
		temp.append("dbRBF_g:"+dbRBF_g+"\n");
		temp.append("dbLinear_c:"+dbLinear_c+"\n");
		temp.append("nMaxSampleNum:"+nMaxSampleNum+"\n");
		temp.append("nFold:"+nFold+"\n");
		return temp.toString();
		
	}
}
