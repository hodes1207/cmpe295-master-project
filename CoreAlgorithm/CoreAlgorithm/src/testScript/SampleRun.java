package testScript;

import Jama.Matrix;
import PCA.PCA;

/** An example program using the library */
public class SampleRun {
	public static void main(String[] args){
		System.out.println("Running a demonstrational program on some sample data ...");
		Matrix trainingData = new Matrix(new double[][] {
			{-1, 2, 3, -4, 5, -6, 1, 2, 3, 4, 5, 6, 1, 2, -3, 64, 5, 6, 1, 2, 83, -4, 5, 6 , -2, 3, 4, -85, 6, 51, 2, 3, 4, 75, 6, 1, 2, 3, 4, -95, 6, 1, -2, 73, 4, 5, 6 , 72, 3, 4, 5, -6, 1, 2, 3, 54, 5, 6, 91, 2, 3, 4,85, 96, 1, -2, 3, 4, 5, 6 , -452, 3, 4, -5, 6, 891, 2, -3, 4, 5, 576, 1, 2, 3, -4, 5, 86, 1, -92, 3, -4, 95, 6},
			{6, -5, 4, 673, 2, 1, -6, 5, 984, 3, -2, 1, 66, 5, 84, 33, 2, 18, 6, 59, -4, 3, 2, 551, 5, -784, 3, 2, 71, -96, 5, -4, 563, 2, -91, 6, 995, -4, 3, 452, -91, -6, 65, 84, 3, 92, 1, 56, 4, -93, 2, 881, 6, 95, 4, 783, 92, 1, 56, 59, 4, -3, 2, 61, 6, 85, -64, 3, 892, 1, 65, -4, 763, 2, 81, 6, 905, 4, 73, 2, -1, 86, -505, -94, 73, 2, 1, 96, 5, 54, 83, -2, 1},
			{2, 243, 23, 2, 2, 21, 2, 67, 2, 23, -32, 2, 2, 6542, 2, -452, 792, 72, 2, 452, 72, 2, 342, 2, 762, 2, 452, 2, 762, -562, 32, 2, 232, 2, -872, 32, 2, -872, 2, 452, -762, 2, 342, 2, 542, 2, 232, 52, 32, 2, 452, 22, 892, 342, -872, 452, 2, 562, 42, -82, 52, -762, 232, 452, 25, 223, 232, 42, -52, 21, 245, -342, 52, 2, 232, 2, 542, 22, 24, 122, 23, 44, 232, 23, 2, 2, 2, -32, 52, 2, 43, 232, -42}});
		PCA pca = new PCA(trainingData);
		Matrix testData = new Matrix(new double[][] {
				{1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6, 1, 2, 3, 4, 5, 6},
				{1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2}});
		Matrix transformedData =
			pca.transform(testData, PCA.TransformationType.WHITENING);
		System.out.println("Transformed data:");
		for(int r = 0; r < transformedData.getRowDimension(); r++){
			for(int c = 0; c < transformedData.getColumnDimension(); c++){
				System.out.print(transformedData.get(r, c));
				if (c == transformedData.getColumnDimension()-1) continue;
				System.out.print(", ");
			}
			System.out.println("");
		}
	}
}