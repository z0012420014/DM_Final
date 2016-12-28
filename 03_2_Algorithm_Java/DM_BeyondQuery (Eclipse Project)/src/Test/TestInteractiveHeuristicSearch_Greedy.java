package Test;

import java.util.ArrayList;

import Algorithm.InteractiveHeuristicSearch_Greedy;

public class TestInteractiveHeuristicSearch_Greedy {
	
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
		
		InteractiveHeuristicSearch_Greedy IHS_Greedy;
		IHS_Greedy	= new InteractiveHeuristicSearch_Greedy(itemToTagMatrix,
                                                            weightOfItem,
                                                            tagHasNotBeenAsked);
		System.out.println(IHS_Greedy.generateNextQuestion());
		
		System.out.println("Done!");
		
	}
	
}
