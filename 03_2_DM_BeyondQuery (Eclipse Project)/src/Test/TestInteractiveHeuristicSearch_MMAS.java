package Test;

import java.util.ArrayList;
import java.util.Arrays;

import Algorithm.InteractiveHeuristicSearch_MMAS;

public class TestInteractiveHeuristicSearch_MMAS {

	public static void main(String[] args) {

		System.out.println("Start!");
		
		int[][] itemToTagMatrix = {{0, 1, 0, 1, 0},
                                   {1, 1, 0, 0, 1},
                                   {1, 0, 1, 0, 1},
                                   {1, 0, 1, 0, 0},
                                   {1, 0, 1, 0, 1},
                                   {1, 0, 0, 0, 0},
                                   {0, 0, 1, 0, 1},
                                   {0, 0, 1, 0, 0},
                                   {1, 0, 0, 0, 0},
                                   {1, 0, 0, 0, 0}};
		
		double[] weightOfItem = {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0};
		
		ArrayList<Integer> tagHasNotBeenAsked = new ArrayList<Integer>();
		tagHasNotBeenAsked.add(0);
		tagHasNotBeenAsked.add(1);
		tagHasNotBeenAsked.add(2);
		tagHasNotBeenAsked.add(3);
		tagHasNotBeenAsked.add(4);
		
		InteractiveHeuristicSearch_MMAS IHS_MMAS;
		IHS_MMAS = new InteractiveHeuristicSearch_MMAS(itemToTagMatrix,
                                                       weightOfItem,
                                                       tagHasNotBeenAsked);
		
		System.out.println(Arrays.toString(IHS_MMAS.generateNextTwoQuestion()));
		
		System.out.println("Done!");
		
	}
	
}
