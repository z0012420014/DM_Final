package Algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.xml.namespace.QName;

public class InteractiveHeuristicSearch_MMAS {

	private int[][] itemToTagMatrix;
	private double[] weightOfItem;
	private ArrayList<Integer> tagHasNotBeenAsked;
	private double totalWeightOfItem;

	private int numOfQuestToConsider = 2;
	private int numOfItem;
	private int numOfTag;
	
	private double gamma = 0.8;

	private int numOfItr = 30;
	private int numOfAnt = 10;

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
			
			numOfItem = itemToTagMatrix.length;
			numOfTag = tagHasNotBeenAsked.size();
			
			for(int i = 0; i < numOfItem; i++) {
				totalWeightOfItem += weightOfItem[i];
			}
	}
	
	
	public int generateNextQuestion() {
		
		int idxOfSelTag = -1;
		
		maxMinAntSystem();
		
		idxOfSelTag = tagHasNotBeenAsked.get(bestAntTour[0]);
		
		return idxOfSelTag;
	}
	
	public int[] generateNextTwoQuestion() {
		
		int[] idxOfSelTag = null;
		
		maxMinAntSystem();
		
		idxOfSelTag = bestAntTour;
		
		return idxOfSelTag;
	}
	
	private void maxMinAntSystem() {
		
		createDataStructureOfAntSystem();
		
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
		
		antTour = new int[numOfAnt][numOfQuestToConsider];
		bestAntTour = new int[numOfQuestToConsider];
		
		antFitness = new double[numOfAnt];
		bestAntFitness = 0.0;
		
		pheromone = new double[numOfQuestToConsider][tagHasNotBeenAsked.size()];
	}
	
	private void initPheromoneToMaximum() {
		
		for(int i = 0; i < numOfQuestToConsider; i++) {
			for(int j = 0; j < tagHasNotBeenAsked.size(); j++) {
				pheromone[i][j] = pheromoneMaximum;
			}
		}
	}
	
	private void constructSolution() {
		
		int lenOfCandidateCity = tagHasNotBeenAsked.size();
		int[] candidateCity = new int[lenOfCandidateCity];
		
		for(int i = 0; i < lenOfCandidateCity; i++) {
			candidateCity[i] = i;
		}
		
		for(int idxOfAnt = 0; idxOfAnt < numOfAnt; idxOfAnt++) {
			antTour[idxOfAnt] = buildTour(candidateCity, lenOfCandidateCity);
		}
	}
	
	private int[] buildTour(int[] candidateCity, int lenOfCandidateCity) {
		
		Random rand = new Random();
		
		int[] antTour = new int[numOfQuestToConsider];
		
		for(int i = 0; i < numOfQuestToConsider; i++) {
			
			if(lenOfCandidateCity == 1) {
				antTour[i] = candidateCity[0];
				break;
			}
			
			int dartA = rand.nextInt(lenOfCandidateCity);
			int candidateCityA = candidateCity[dartA];
			lenOfCandidateCity--;
			candidateCity[dartA] = candidateCity[lenOfCandidateCity];
			
			int dartB = rand.nextInt(lenOfCandidateCity);
			int candidateCityB = candidateCity[dartB];
			candidateCity[lenOfCandidateCity] = candidateCity[dartA];
			candidateCity[dartA] = candidateCityA;
			
			
			if(pheromone[i][candidateCityA] >= pheromone[i][candidateCityB]) {
				antTour[i] = candidateCityA;
				candidateCity[dartA] = candidateCity[lenOfCandidateCity];
			} else {
				antTour[i] = candidateCityB;
				candidateCity[dartB] = candidateCity[lenOfCandidateCity];
			}
		}
		
		for(int i = 0; i < numOfQuestToConsider; i++) {
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
		
		int[] firstPossibleAnswer = {0, 0};
		int[] secondPossibleAnswer = {0, 1};
		int[] thirdPossibleAnswer = {1, 0};
		int[] forthPossibleAnswer = {1, 1};

		double firstTotalDecline = computeTotalDecline(antTour, firstPossibleAnswer);
		double secondTotalDecline = computeTotalDecline(antTour, secondPossibleAnswer);
		double thirdTotalDecline = computeTotalDecline(antTour, thirdPossibleAnswer);
		double forthTotalDecline = computeTotalDecline(antTour, forthPossibleAnswer);
		
		double minimunDecline = Double.MAX_VALUE;
		if(firstTotalDecline < minimunDecline) {
			minimunDecline = firstTotalDecline;
		}
		if(secondTotalDecline < minimunDecline) {
			minimunDecline = secondTotalDecline;
		}
		if(thirdTotalDecline < minimunDecline) {
			minimunDecline = thirdTotalDecline;
		}
		if(forthTotalDecline < minimunDecline) {
			minimunDecline = forthTotalDecline;
		}
		
		fitness = minimunDecline;
		
		return fitness;
	}
	
	private double computeTotalDecline(int[] antTour, int[] answer) {
		
		double totalDecline = 0;
		
		double[] declineGamma = new double[numOfItem];
		for(int i = 0; i < numOfItem; i++) {
			declineGamma[i] = gamma;
		}
		
		for(int a = 0; a < numOfQuestToConsider; a++) {
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
			
			for(int j = 0; j < numOfQuestToConsider; j++) {
				bestAntTour[j] = antTour[idxOfItrBestAnt][j];
			}
		}
	}
	
	private int getIndexOfItrBestAnt() {
		
		int idx = -1;
		
		double maxFitness = Integer.MIN_VALUE;
		for(int i = 0; i < numOfAnt; i++) {
			if(antFitness[i] >= maxFitness) {
				maxFitness = antFitness[i];
				idx = i;
			}
		}
		
		return idx;
	}
	
	private void determinePheromoneMaximumAndMinimum() {
		
		pheromoneMaximum = 1.0/(volatilizeRate*bestAntFitness);
		
		pheromoneMinimum = pheromoneMaximum/(2*numOfTag);
		
	}
	
	
	private void volatilizePheromone() {
		
		for(int i = 0; i < numOfQuestToConsider; i++) {
			for(int j = 0; j < tagHasNotBeenAsked.size(); j++) {
				pheromone[i][j] *= (1.0 - volatilizeRate);
			}
		}
	}
	
	private void addPheromone(int[] antTour, double antFitness) {
		
		double q = 1.0/totalWeightOfItem;

		for(int j = 0; j < numOfQuestToConsider; j++) {
			pheromone[j][antTour[j]] += q*antFitness;
		}
	}
	
	private void checkPheromoneMaximumAndMinimum() {
		
		for(int i = 0; i < numOfQuestToConsider; i++) {
			for(int j = 0; j < tagHasNotBeenAsked.size(); j++) {
				if(pheromone[i][j] > pheromoneMaximum) {
					pheromone[i][j] = pheromoneMaximum;
				} else if(pheromone[i][j] < pheromoneMinimum) {
					pheromone[i][j] = pheromoneMinimum;
				}
			}
		}	
	}
	
}
