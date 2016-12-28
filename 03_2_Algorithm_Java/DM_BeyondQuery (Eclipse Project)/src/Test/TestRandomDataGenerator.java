package Test;

import Algorithm.RandomDataGenerator;

public class TestRandomDataGenerator {

	public static void main(String[] args) {

		System.out.println("Start!");
		
		String outputFileName = "";
		RandomDataGenerator rdg;
		
		int[] numOfItem = {1000};
		int[] numOfTag = {100};
		double[] probOfOne = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.50};
		
		for(int i = 0; i < numOfItem.length; i++) {
			for(int j = 0; j < numOfTag.length; j++) {
				for(int k = 0; k < probOfOne.length; k++) {
					outputFileName = "testSample" + "_"
                                   + numOfItem[i] + "_"
                                   + numOfTag[j] + "_"
                                   + probOfOne[k] + ".txt";
					
					rdg = new RandomDataGenerator(numOfItem[i],
                                                  numOfTag[j],
                                                  probOfOne[k],
                                                  outputFileName);
					rdg.generateDataAndWriteToFile();
				}
			}
		}
		
		System.out.println("Done!");
		
	}
	
}
