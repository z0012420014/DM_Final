package Algorithm;

import java.util.ArrayList;

public class InteractiveHeuristicSearch_Greedy {
	
	private int[][] itemToTagMatrix;
	private double[] weightOfItem;
	private ArrayList<Integer> tagHasNotBeenAsked;
	private int numOfTagHasNotBeenAsked;
	
	private int numOfItem;
	private int numOfTag;
	
	private double[] sumOfWeightForEachTag;
	private double totalWeightOfItem;

	public InteractiveHeuristicSearch_Greedy(int[][] itemToTagMatrix,
                                             double[] weightOfItem,
                                             ArrayList<Integer> tagHasNotBeenAsked) {
		
		this.itemToTagMatrix = itemToTagMatrix;
		this.weightOfItem = weightOfItem;
		this.tagHasNotBeenAsked = tagHasNotBeenAsked;

		numOfTagHasNotBeenAsked = tagHasNotBeenAsked.size();
		
		numOfItem = itemToTagMatrix.length;
		numOfTag = itemToTagMatrix[0].length;
		
	}
	
	public int generateNextQuestion() {
		
		computeSumOfWeightForEachTag();
		
		computeTotalWeightOfItem();
		
		return  selectTagAccordingScoreAlpha();
	}
	
	private void computeSumOfWeightForEachTag() {
		
		sumOfWeightForEachTag = new double[numOfTag];
		
		for(int j = 0; j < numOfTagHasNotBeenAsked; j++) {
			int idxOfTag = tagHasNotBeenAsked.get(j);
			
			for(int i = 0; i < numOfItem; i++) {
				if(itemToTagMatrix[i][idxOfTag] == 1) {
					sumOfWeightForEachTag[idxOfTag] += weightOfItem[i];
				}
			}
		}
	}
	
	private void computeTotalWeightOfItem() {
		
		for(int i = 0; i < numOfItem; i++) {
			totalWeightOfItem += weightOfItem[i];
		}
	}
	
	private int selectTagAccordingScoreAlpha() {
		
		int idxOfSelTag = -1;
		double scoreOfSelTag = 1.0;
		
		for(int j = 0; j < numOfTagHasNotBeenAsked; j++) {
			double weightRatio = sumOfWeightForEachTag[tagHasNotBeenAsked.get(j)]/totalWeightOfItem;
			double scoreOfCurrTag = Math.abs(weightRatio - 0.5);
			if(scoreOfCurrTag < scoreOfSelTag) {
				scoreOfSelTag = scoreOfCurrTag;
				idxOfSelTag = tagHasNotBeenAsked.get(j);
			}
		}
		
		return idxOfSelTag;
	}

}
