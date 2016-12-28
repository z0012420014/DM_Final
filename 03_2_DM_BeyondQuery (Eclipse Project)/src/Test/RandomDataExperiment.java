package Test;

import java.util.Random;

import Algorithm.InteractiveRecommendation;

public class RandomDataExperiment {

	public static void main(String[] args) {
		
		System.out.println("Enter!");
		
		int numOfItem = 1000;
		int numOfTag = 100;
		
		double[] gamma = {0.0};
		double[] userErrorRate = {0.0, 0.02, 0.04, 0.06, 0.08, 0.1};
		double[] probOfOne = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.50};
		for(int g = 0; g < gamma.length; g++) {
			System.out.print("uer/pob" + "\t");
			for(int i = 0; i < probOfOne.length; i++) {
				System.out.printf("%4.2f\t", probOfOne[i]);;
			}
			System.out.println();
			for(int e = 0; e < userErrorRate.length; e++) {
				System.out.printf("%4.2f\t", userErrorRate[e]);
				for(int p = 0; p < probOfOne.length; p++) {
					
					int avgNumOfQuestionNeeded = 0;
					int numOfQuestionNeeded = 0;
					int numOfTarget = 300;
					int[] idxOfTargetItem = generateIdxOfTargetItem(numOfItem, numOfTarget);
					
					String inputFileName = "testSample" + "_" + numOfItem + "_" + numOfTag + "_" + probOfOne[p] + ".txt";
					int maxNumOfQuestion = numOfTag;
					
					InteractiveRecommendation ir = new InteractiveRecommendation(inputFileName,
                                                                                 maxNumOfQuestion,
                                                                                 gamma[g]);
					for(int t = 0; t < numOfTarget; t++) {
						numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_Greedy(idxOfTargetItem[t], userErrorRate[e]);
						//numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_IHS_MMAS(idxOfTargetItem[t], userErrorRate[e]);
						//numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_SHS_MMAS(idxOfTargetItem[t], userErrorRate[e]);
						if(numOfQuestionNeeded == -1) {
							numOfQuestionNeeded = numOfTag;
						}
	
						avgNumOfQuestionNeeded += numOfQuestionNeeded;
					}
					avgNumOfQuestionNeeded /= numOfTarget;
		
					System.out.printf("%4d\t", avgNumOfQuestionNeeded);
				}
				System.out.println();
			}
			System.out.println();
		}
		
		for(int g = 0; g < gamma.length; g++) {
			System.out.print("uer/pob" + "\t");
			for(int i = 0; i < probOfOne.length; i++) {
				System.out.printf("%4.2f\t", probOfOne[i]);;
			}
			System.out.println();
			for(int e = 0; e < userErrorRate.length; e++) {
				System.out.printf("%4.2f\t", userErrorRate[e]);
				for(int p = 0; p < probOfOne.length; p++) {
					
					int avgNumOfQuestionNeeded = 0;
					int numOfQuestionNeeded = 0;
					int numOfTarget = 300;
					int[] idxOfTargetItem = generateIdxOfTargetItem(numOfItem, numOfTarget);
					
					String inputFileName = "testSample" + "_" + numOfItem + "_" + numOfTag + "_" + probOfOne[p] + ".txt";
					int maxNumOfQuestion = numOfTag;
					
					InteractiveRecommendation ir = new InteractiveRecommendation(inputFileName,
                                                                                 maxNumOfQuestion,
                                                                                 gamma[g]);
					for(int t = 0; t < numOfTarget; t++) {
						//numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_Greedy(idxOfTargetItem[t], userErrorRate[e]);
						numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_IHS_MMAS(idxOfTargetItem[t], userErrorRate[e]);
						//numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_SHS_MMAS(idxOfTargetItem[t], userErrorRate[e]);
						if(numOfQuestionNeeded == -1) {
							numOfQuestionNeeded = numOfTag;
						}
	
						avgNumOfQuestionNeeded += numOfQuestionNeeded;
					}
					avgNumOfQuestionNeeded /= numOfTarget;
		
					System.out.printf("%4d\t", avgNumOfQuestionNeeded);
				}
				System.out.println();
			}
			System.out.println();
		}

		for(int g = 0; g < gamma.length; g++) {
			System.out.print("uer/pob" + "\t");
			for(int i = 0; i < probOfOne.length; i++) {
				System.out.printf("%4.2f\t", probOfOne[i]);;
			}
			System.out.println();
			for(int e = 0; e < userErrorRate.length; e++) {
				System.out.printf("%4.2f\t", userErrorRate[e]);
				for(int p = 0; p < probOfOne.length; p++) {
					
					int avgNumOfQuestionNeeded = 0;
					int numOfQuestionNeeded = 0;
					int numOfTarget = 300;
					int[] idxOfTargetItem = generateIdxOfTargetItem(numOfItem, numOfTarget);
					
					String inputFileName = "testSample" + "_" + numOfItem + "_" + numOfTag + "_" + probOfOne[p] + ".txt";
					int maxNumOfQuestion = numOfTag;
					
					InteractiveRecommendation ir = new InteractiveRecommendation(inputFileName,
                                                                                 maxNumOfQuestion,
                                                                                 gamma[g]);
					for(int t = 0; t < numOfTarget; t++) {
						//numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_Greedy(idxOfTargetItem[t], userErrorRate[e]);
						//numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_IHS_MMAS(idxOfTargetItem[t], userErrorRate[e]);
						numOfQuestionNeeded = ir.autoInteractiveSearchTargetItem_SHS_MMAS(idxOfTargetItem[t], userErrorRate[e]);
						if(numOfQuestionNeeded == -1) {
							numOfQuestionNeeded = numOfTag;
						}
	
						avgNumOfQuestionNeeded += numOfQuestionNeeded;
					}
					avgNumOfQuestionNeeded /= numOfTarget;
		
					System.out.printf("%4d\t", avgNumOfQuestionNeeded);
				}
				System.out.println();
			}
			System.out.println();
		}

		
		
		
		System.out.println("Done!");
	}
	
	public static int[] generateIdxOfTargetItem(int numOfItem, int numOfTarget) {
		
		Random rand = new Random();
		
		int[] idxOfTargetItem = new int[numOfTarget];
		int[] idxOfItem = new int[numOfItem];
		
		for(int i = 0; i < numOfItem; i++) {
			idxOfItem[i] = i;
		}
		
		for(int i = 0; i < numOfTarget; i++) {
			int dart = rand.nextInt(numOfItem - i);
			idxOfTargetItem[i] = idxOfItem[dart];
			idxOfItem[dart] = idxOfItem[numOfItem - i - 1];
		}
		
		return idxOfTargetItem;
	}
	
}
