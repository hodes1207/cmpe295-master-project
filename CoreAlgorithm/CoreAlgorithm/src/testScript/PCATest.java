package testScript;

import java.io.IOException;
import java.util.Hashtable;

import javastat.StatisticalAnalysis;
import javastat.multivariate.*;

public class PCATest {

	public static void main(String[] args) throws IOException
	{
		double [][] testscores = { 
				{11, 323, 425, 127, 329, 11, 323, 425, 127, 329, -143, 213, -4345, 237, 1239, -143, 213, -4345, 237, 1239, 11, 323, 425, 127, 329, 11, 323, 425, 127, 329, -143, 213, -4345, 237, 1239, -143, 213, -4345, 237, 1239}, 
				{2332, 125, 3215, -237, 219, -123, 2332, 125, 3215, -237, 219, -123, 2332, 125, 3215, -237, 219, -123, 2332, 125, 3215, -237, 219, -123, 323, 1235, 73, 9254, 323, 1235, 73, 9254, 323, 1235, 73, 9254, 323, 1235, 73, 9254}, 
				{21, -232, 21, -232, 535, 147, 49, 121, 3, 21, -232, 535, 147, 49, 121, 3, 435, 127,39, 435, 127,39, 535, 147, 49, 121, 3, 21, -232, 535, 147, 49, 121, 3, 435, 127,39, 435, 127,39}, 
				{121, 121, 121, 121, 23342, 245, -624, 3393, 451, -1423, 152, 127, 439, 23342, 245, -624, 3393, 451, -1423, 152, 127, 439, 23342, 245, -624, 3393, 451, -1423, 152, 127, 439, 23342, 245, -624, 3393, 451, -1423, 152, 127, 439},
				{21, 232, -245, -64, 393, 451, -123, 15, 127, 21, 232, -245, 21, 232, -245, -64, 393, 451, -123, 15, 127, 21, 232, -245, -64, 393, 451, -123, 15, 127, 439, 439, -64, 393, 451, -123, 15, 127, 439, 439}, 
				{142, 31, 153, 142, 31, 153, 631, 129, -233, 33, 415, -1237, 142, 31, 153, 142, 31, 153, 631, 129, -233, 33, 415, -1237, 39, 631, 129, -233, 33, 415, -1237, 39, 39, 631, 129, -233, 33, 415, -1237, 39},
				{453, -311, 453, -311, -513, 61, -139, 121, 453, -311, -513, 61, -139, 121, 346, 35, 57, 96, 346, 35, 57, 96, -513, 61, -139, 121, 453, -311, -513, 61, -139, 121, 346, 35, 57, 96, 346, 35, 57, 96},
				{1431, 321, -5356, 6113, 19, 12, 13, 1431, 321, -5356, 6113, 1431, 321, -5356, 6113, 19, 12, 13, 1431, 321, -5356, 6113, 19, 12, 13, 523, 437, 19, 523, 437, 19, 19, 12, 13, 523, 437, 19, 523, 437, 19}
				}; 

		//Non-null constructor 
		//PCA testclass1 = new PCA(0.95, "covariance", testscores); 
		//double [] firstComponent = testclass1.principalComponents[0]; 
		
		//Null constructor 
		PCA testclass2 = new PCA(); 
		testclass2.level = 0.999;
		double [][] principalComponents = testclass2.principalComponents(testscores); 
		double [] variance = testclass2.variance(testscores);
		
		//Non-null constructor 
		/*Hashtable argument1 = new Hashtable(); 
		argument1.put(LEVEL, 0.95); 
		argument1.put(COVARIANCE_CHOICE, "covariance"); 
		StatisticalAnalysis testclass3 = new PCA(argument1, testscores). statisticalAnalysis; 
		principalComponents = (double[][]) testclass3.output.get(PRINCIPAL_COMPONENTS); 
		variance = (double[]) testclass3.output.get(COMPONENT_VARIANCE); */
		
		//Null constructor 
		Hashtable argument2 = new Hashtable(); 
		PCA testclass4 = new PCA(argument2, null); 
		principalComponents = testclass4.principalComponents(argument2, testscores); 
		variance = testclass4.variance(argument2, testscores); 
		
		//Obtains the information about the output 
		//out.println(testclass3.output.toString()); 
		System.out.println(testclass4.output.toString());
	}
	
}
