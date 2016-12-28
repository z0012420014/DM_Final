package Algorithm;

import java.util.ArrayList;
import java.util.Random;

public class InteractiveHeuristicSearch_MMAS {

	private int[][] itemToTagMatrix;
	private double[] weightOfItem;
	private ArrayList<Integer> tagHasNotBeenAsked;
	private int numOfTagHasNotBeenAsked;
	
	private double totalWeightOfItem;
	
	double gamma = 0.7;

	private static int numOfQuestionToConsider = 2;
	
	private int numOfItem;
	@SuppressWarnings("unused")
	private int numOfTag; 
	
	private int numOfItr = 20;
	private int numOfAnt = 10;
	
	int lenOfCandidateCity;
	int[] candidateCity;
	
	private int[][] antTour;
	private int[] bestAntTour;
	
	private double[] antFitness;
	private double bestAntFitness;
	
	private double[][] pheromone;
	private double volatilizeRate = 0.1;
	private double pheromoneMaximum = 1.0;
	private double pheromoneMinimum = 0.0;

	
	public InteractiveHeuristicSearch_MMAS(int[][] itemToTagMatrix,
                                           double[] weightOfItem,
                                           ArrayList<Integer> tagHasNotBeenAsked) {

			this.itemToTagMatrix = itemToTagMatrix;
			this.weightOfItem = weightOfItem;
			this.tagHasNotBeenAsked = tagHasNotBeenAsked;
			
			numOfTagHasNotBeenAsked = tagHasNotBeenAsked.size();
			
			numOfItem = itemToTagMatrix.length;
			numOfTag = itemToTagMatrix[0].length;
			
			computeTotalWeightOfItem();
			
			numOfAnt = (int) Math.sqrt(numOfTagHasNotBeenAsked) + 1;
	}
	
	private void computeTotalWeightOfItem() {
		
		for(int i = 0; i < numOfItem; i++) {
			totalWeightOfItem += weightOfItem[i];
		}
	}
	
	public int[] generateNextTwoQuestion() {
		
		int[] idxOfSelTag = new int[numOfQuestionToConsider];
		
		maxMinAntSystem();
		for(int i = 0; i < numOfQuestionToConsider; i++) {
			idxOfSelTag[i] = tagHasNotBeenAsked.get(bestAntTour[i]);
		}
		
		return idxOfSelTag;
	}
	
	
	private void maxMinAntSystem() {
		
		createDataStructureOfAntSystem();
		
		createCandidateCity();
		
		initPheromoneToMaximum();
		
		for(int idxOfItr = 0; idxOfItr < numOfItr; idxOfItr++) {
			
			constructSolution();
			
			evaluateSolution();
			
			renewGlobalBestAnt();
			
			determinePheromoneMaximumAndMinimum();
			
			volatilizePheromone();
			
			addPheromone(bestAntTour, bestAntFitness);
			
			checkPheromoneMaximumAndMinimum();
		}
	}
	
	private void createDataStructureOfAntSystem() {
		
		antTour = new int[numOfAnt][numOfQuestionToConsider];
		bestAntTour = new int[numOfQuestionToConsider];
		
		antFitness = new double[numOfAnt];
		bestAntFitness = 0.0;
		
		pheromone = new double[numOfQuestionToConsider][numOfTagHasNotBeenAsked];
	}
	
	private void createCandidateCity() {
		
		lenOfCandidateCity = numOfTagHasNotBeenAsked;
		candidateCity = new int[lenOfCandidateCity];
		
		for(int i = 0; i < lenOfCandidateCity; i++) {
			candidateCity[i] = i;
		}
	}
	
	private void initPheromoneToMaximum() {
		
		for(int i = 0; i < numOfQuestionToConsider; i++) {
			for(int j = 0; j < numOfTagHasNotBeenAsked; j++) {
				pheromone[i][j] = pheromoneMaximum;
			}
		}
	}
	
	private void constructSolution() {
		
		for(int idxOfAnt = 0; idxOfAnt < numOfAnt; idxOfAnt++) {
			antTour[idxOfAnt] = buildTour();
		}
	}
	
	private int[] buildTour() {
		
		Random rand = new Random();
		
		int[] antTour = new int[numOfQuestionToConsider];
		
		for(int i = 0; i < numOfQuestionToConsider; i++) {
			int firstDart = rand.nextInt(lenOfCandidateCity);
			int secondDart = rand.nextInt(lenOfCandidateCity);
			if(firstDart == secondDart) {
				secondDart = (secondDart + rand.nextInt(lenOfCandidateCity) + 1)%lenOfCandidateCity;
			}
			
			int candidateCityA = candidateCity[firstDart];
			int candidateCityB = candidateCity[secondDart];

			if(pheromone[i][candidateCityA] > pheromone[i][candidateCityB]) {
				antTour[i] = candidateCityA;
				candidateCity[firstDart] = candidateCity[--lenOfCandidateCity];
			} else {
				antTour[i] = candidateCityB;
				candidateCity[secondDart] = candidateCity[--lenOfCandidateCity];
			}
		}
		
		for(int i = 0; i < numOfQuestionToConsider; i++) {
			candidateCity[lenOfCandidateCity++] = antTour[i];
		}
		
		return antTour;
	}
	
	
	private void evaluateSolution() {
		
		for(int idxOfAnt = 0; idxOfAnt < numOfAnt; idxOfAnt++) {
			antFitness[idxOfAnt] = getFitness(antTour[idxOfAnt]);
		}
	}
	
	private double getFitness(int[] antTour) {
		
		double fitness = 0;
		
		int[] possibleAnswer = new int[numOfQuestionToConsider];
		double minimunDecline = Double.MAX_VALUE;
		double totalDecline = 0;
		
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				possibleAnswer[0] = i;
				possibleAnswer[1] = j;
				totalDecline = computeTotalDecline(antTour, possibleAnswer);
				if(totalDecline < minimunDecline) {
					minimunDecline = totalDecline;
				}
			}
		}
		
		fitness = (minimunDecline + gamma)/(totalWeightOfItem + gamma);
		
		return fitness;
	}
	
	private double computeTotalDecline(int[] antTour, int[] answer) {
		
		double totalDecline = 0;

		double[] declineGamma = new double[numOfItem];
		for(int i = 0; i < numOfItem; i++) {
			declineGamma[i] = gamma;
		}
		
		for(int a = 0; a < numOfQuestionToConsider; a++) {
			if(answer[a] == 1) {
				for(int i = 0; i < numOfItem; i++) {
					if(itemToTagMatrix[i][tagHasNotBeenAsked.get(antTour[a])] == 0) {
						declineGamma[i] *= gamma;
					}
				}
			} else {
				for(int i = 0; i < numOfItem; i++) {
					if(itemToTagMatrix[i][tagHasNotBeenAsked.get(antTour[a])] == 1) {
						declineGamma[i] *= gamma;
					}
				}
			}
		}
		
		for(int i = 0; i < numOfItem; i++) {
			totalDecline += weightOfItem[i]*(1 - declineGamma[i]);
		}
		
		return totalDecline;
	}
	
	
	private void renewGlobalBestAnt() {
		
		int idxOfItrBestAnt = getIndexOfItrBestAnt();
		if(antFitness[idxOfItrBestAnt] > bestAntFitness) {
			bestAntFitness = antFitness[idxOfItrBestAnt];
			
			for(int j = 0; j < numOfQuestionToConsider; j++) {
				bestAntTour[j] = antTour[idxOfItrBestAnt][j];
			}
		}
	}
	
	private int getIndexOfItrBestAnt() {
		
		int idx = -1;
		
		double maxFitness = Double.MIN_VALUE;
		for(int i = 0; i < numOfAnt; i++) {
			if(antFitness[i] > maxFitness) {
				maxFitness = antFitness[i];
				idx = i;
			}
		}
		
		return idx;
	}
	
	private void determinePheromoneMaximumAndMinimum() {
		
		pheromoneMaximum = 1.0/(1.0 - bestAntFitness);
		pheromoneMinimum = pheromoneMaximum/(2*numOfTagHasNotBeenAsked);
		
		//System.out.println(pheromoneMaximum + "\t" + pheromoneMinimum + "\t" + bestAntFitness);
	}
	
	
	private void volatilizePheromone() {
		
		for(int i = 0; i < numOfQuestionToConsider; i++) {
			for(int j = 0; j < numOfTagHasNotBeenAsked; j++) {
				pheromone[i][j] *= (1.0 - volatilizeRate);
			}
		}
	}
	
	private void addPheromone(int[] antTour, double antFitness) {
		
		for(int j = 0; j < numOfQuestionToConsider; j++) {
			pheromone[j][antTour[j]] += antFitness;
		}
	}
	
	private void checkPheromoneMaximumAndMinimum() {
		
		for(int i = 0; i < numOfQuestionToConsider; i++) {
			for(int j = 0; j < numOfTagHasNotBeenAsked; j++) {
				if(pheromone[i][j] > pheromoneMaximum) {
					pheromone[i][j] = pheromoneMaximum;
				} else if(pheromone[i][j] < pheromoneMinimum) {
					pheromone[i][j] = pheromoneMinimum;
				}
			}
		}	
	}
	
}
