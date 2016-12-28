package Test;

import Algorithm.InteractiveRecommendation;

public class TestInteractiveRecommendation {

	public static void main(String[] args) {
		
		System.out.println("Start!");
		
		String inputFileName = "testSample_20_10_0.2.txt";
		int maxNumOfQuestion = 10;
		double gamma = 0.5;
		
		InteractiveRecommendation ir;
		ir = new InteractiveRecommendation(inputFileName,
                                           maxNumOfQuestion,
                                           gamma);
		ir.manualInteractiveSearchTargetItem();
		
		System.out.println("Done!");
		
	}
	
}
