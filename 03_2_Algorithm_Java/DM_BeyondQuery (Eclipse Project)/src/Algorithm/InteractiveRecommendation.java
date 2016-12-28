package Algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class InteractiveRecommendation {
	
	private String inputFileName = "";
	private int maxNumOfQuestion;
	private double gamma;
	
	private int numOfItem;
	private int numOfTag;
	private int numOfOne;
	
	private int[][] itemToTagMatrix;
	private String[] itemName;
	private String[] tagName;
	
	private ArrayList<Integer> tagHasNotBeenAsked;
	private double[] weightOfItem;
	
	
	
	public InteractiveRecommendation(String inputFileName,
                                     int maxNumOfQuestion,
                                     double gamma) {
		
		this.inputFileName = inputFileName;
		this.maxNumOfQuestion = maxNumOfQuestion;
		this.gamma = gamma;
		
		try {
			readDataMatrixFromFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public int autoInteractiveSearchTargetItem_Greedy(int idxOfTargetItem, double userErrorRate) {
		
		Random rand = new Random();
		
		initTagHasNotBeenAsked();
		initWeightOfItem();
		
		for(int k = 0; k < maxNumOfQuestion; k++) {
			int idxOfSelTag = generateNextQuestion_Greedy();
			if(idxOfSelTag == -1) {
				break;
			}
			
			int answer = -1;
			if(rand.nextDouble() > userErrorRate) {
				answer = itemToTagMatrix[idxOfTargetItem][idxOfSelTag];
			} else {
				answer = Math.abs(itemToTagMatrix[idxOfTargetItem][idxOfSelTag] - 1);
			}
			
			if(answer == 1) {
				decreaseWeightOfItemWithoutSelTag(idxOfSelTag);
			} else if(answer == 0) {
				decreaseWeightOfItemWithSelTag(idxOfSelTag);
			}
			
			removeSelTag(idxOfSelTag);
			
			if(idxOfTargetItem == getMostPossibleItem()) {
				return k;
			}
		}
		
		return -1;
	}
	
	public int autoInteractiveSearchTargetItem_IHS_MMAS(int idxOfTargetItem, double userErrorRate) {
		
		Random rand = new Random();
		
		initTagHasNotBeenAsked();
		initWeightOfItem();
		
		for(int k = 0; k < maxNumOfQuestion - 2; k++) {
			int idxOfSelTag = generateNextQuestion_IHS_MMAS();
			if(idxOfSelTag == -1) {
				break;
			}
			
			int answer = -1;
			if(rand.nextDouble() > userErrorRate) {
				answer = itemToTagMatrix[idxOfTargetItem][idxOfSelTag];
			} else {
				answer = Math.abs(itemToTagMatrix[idxOfTargetItem][idxOfSelTag] - 1);
			}
			
			if(answer == 1) {
				decreaseWeightOfItemWithoutSelTag(idxOfSelTag);
			} else if(answer == 0) {
				decreaseWeightOfItemWithSelTag(idxOfSelTag);
			}
			
			removeSelTag(idxOfSelTag);
			
			if(idxOfTargetItem == getMostPossibleItem()) {
				return k;
			}
		}
		
		return -1;
	}
	
	public int autoInteractiveSearchTargetItem_SHS_MMAS(int idxOfTargetItem, double userErrorRate) {
		
		Random rand = new Random();
		
		initTagHasNotBeenAsked();
		initWeightOfItem();
		
		for(int k = 0; k < maxNumOfQuestion/2 -1; k++) {
			int[] idxOfSelTag = generateNextQuestion_SHS_MMAS();
			if(idxOfSelTag == null) {
				break;
			}
			
			int answer = -1;
			int idxOfSelTagOne = idxOfSelTag[0];
			if(rand.nextDouble() > userErrorRate) {
				answer = itemToTagMatrix[idxOfTargetItem][idxOfSelTagOne];
			} else {
				answer = Math.abs(itemToTagMatrix[idxOfTargetItem][idxOfSelTagOne] - 1);
			}
			
			if(answer == 1) {
				decreaseWeightOfItemWithoutSelTag(idxOfSelTagOne);
			} else if(answer == 0) {
				decreaseWeightOfItemWithSelTag(idxOfSelTagOne);
			}
			
			removeSelTag(idxOfSelTagOne);
			
			int idxOfSelTagTwo = idxOfSelTag[1];
			if(rand.nextDouble() > userErrorRate) {
				answer = itemToTagMatrix[idxOfTargetItem][idxOfSelTagTwo];
			} else {
				answer = Math.abs(itemToTagMatrix[idxOfTargetItem][idxOfSelTagTwo] - 1);
			}
			
			if(answer == 1) {
				decreaseWeightOfItemWithoutSelTag(idxOfSelTagTwo);
			} else if(answer == 0) {
				decreaseWeightOfItemWithSelTag(idxOfSelTagTwo);
			}
			
			removeSelTag(idxOfSelTagTwo);
			
			if(idxOfTargetItem == getMostPossibleItem()) {
				return k*2;
			}
		}
		
		return -1;
	}
	
	public void manualInteractiveSearchTargetItem() {
		
		Scanner input = new Scanner(System.in);
		
		initTagHasNotBeenAsked();
		initWeightOfItem();
		
		for(int k = 0; k < maxNumOfQuestion; k++) {
			int idxOfSelTag = generateNextQuestion_Greedy();
			if(idxOfSelTag == -1) {
				break;
			}
			
			displayCurrWeightOfItem();
			System.out.print("你所在找的目標物具有'" + tagName[idxOfSelTag] + "'嗎? ");
			String answer = input.nextLine();
			
			if(answer.compareTo("Y") == 0) {
				decreaseWeightOfItemWithoutSelTag(idxOfSelTag);
			} else if(answer.compareTo("N") == 0) {
				decreaseWeightOfItemWithSelTag(idxOfSelTag);
			}
			
			System.out.println("目前最可能的物品: '" + itemName[getMostPossibleItem()] + "'\n");
			
			removeSelTag(idxOfSelTag);
		}
		
		input.close();
	}
	
	private void initTagHasNotBeenAsked() {
		
		tagHasNotBeenAsked = new ArrayList<Integer>();
		
		for(int j = 0; j < numOfTag; j++) {
			tagHasNotBeenAsked.add(j);
		}
	}
	
	private void initWeightOfItem() {
		
		weightOfItem = new double[numOfItem];
		
		for(int i = 0; i < numOfItem; i++) {
			weightOfItem[i] = 1.0;
		}
	}
	
	private int generateNextQuestion_Greedy() {
		
		int idxOfSelTag = -1;
		
		InteractiveHeuristicSearch_Greedy IHS_Greedy;
		IHS_Greedy = new InteractiveHeuristicSearch_Greedy(itemToTagMatrix,
                                                           weightOfItem,
                                                           tagHasNotBeenAsked);
		idxOfSelTag = IHS_Greedy.generateNextQuestion();
		
		return idxOfSelTag;
	}
	
	private int generateNextQuestion_IHS_MMAS() {
		
		int idxOfSelTag = -1;
		
		InteractiveHeuristicSearch_MMAS IHS_MMAS;
		IHS_MMAS = new InteractiveHeuristicSearch_MMAS(itemToTagMatrix,
                                                       weightOfItem,
                                                       tagHasNotBeenAsked);
		idxOfSelTag = IHS_MMAS.generateNextTwoQuestion()[0];
		
		return idxOfSelTag;
	}
	
	private int[] generateNextQuestion_SHS_MMAS() {
		
		int[] idxOfSelTag = null;
		
		InteractiveHeuristicSearch_MMAS SHS_MMAS;
		SHS_MMAS = new InteractiveHeuristicSearch_MMAS(itemToTagMatrix,
                                                       weightOfItem,
                                                       tagHasNotBeenAsked);
		idxOfSelTag = SHS_MMAS.generateNextTwoQuestion();
		
		return idxOfSelTag;
	}
	
	private void displayCurrWeightOfItem() {
		
		for(int i = 0; i < numOfItem; i++) {
			System.out.printf("%4.2f\t", weightOfItem[i]);
		}
		System.out.println();
	}
	
	private void decreaseWeightOfItemWithSelTag(int idxOfSelTag) {
		
		for(int i = 0; i < numOfItem; i++) {
			if(itemToTagMatrix[i][idxOfSelTag] == 1) {
				weightOfItem[i] *= gamma;
			}
		}
	}
	
	private void decreaseWeightOfItemWithoutSelTag(int idxOfSelTag) {
		
		for(int i = 0; i < numOfItem; i++) {
			if(itemToTagMatrix[i][idxOfSelTag] == 0) {
				weightOfItem[i] *= gamma;
			}
		}
	}
	
	private void removeSelTag(int idxOfSelTag) {
		
		for(int j = 0; j < tagHasNotBeenAsked.size(); j++) {
			if(tagHasNotBeenAsked.get(j) == idxOfSelTag) {
				tagHasNotBeenAsked.remove(j);
			}
		}
	}
	
	private int getMostPossibleItem() {
	
		int idxOfMax = -1;
		double valOfMax = 0;
		
		for(int i = 0; i < numOfItem; i++) {
			if(weightOfItem[i] > valOfMax) {
				valOfMax = weightOfItem[i];
				idxOfMax = i;
			}
		}
		
		return idxOfMax;
	}
	
	private void readDataMatrixFromFile() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(inputFileName));
		
		numOfItem = Integer.parseInt(br.readLine());
		numOfTag = Integer.parseInt(br.readLine());
		numOfOne = Integer.parseInt(br.readLine());
		br.readLine();
		
		itemToTagMatrix = new int[numOfItem][numOfTag];
		itemName = new String[numOfItem];
		tagName = new String[numOfTag];
		
		for(int i = 0; i < numOfItem; i++) {
			itemName[i] = br.readLine();
		}
		br.readLine();
		
		for(int j = 0; j < numOfTag; j++) {
			tagName[j] = br.readLine();
		}
		br.readLine();
		
		String temp;
		String[] temps;
		int i, j;
		for(int k = 0; k < numOfOne; k++) {
			temp = br.readLine();
			temps = temp.split(",");
			i = Integer.parseInt(temps[0]);
			j = Integer.parseInt(temps[1]);
			itemToTagMatrix[i][j] = 1;
		}
		br.readLine();
		
		br.close();
	}
	
}
