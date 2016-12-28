package Algorithm;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomDataGenerator {
	
	private String outputFileName = "";
	
	private int numOfItem;
	private int numOfTag;
	private int numOfOne;
	private double probOfOne;
	
	private int[][] itemToTagMatrix;
	private String[] itemName;
	private String[] tagName;
	
	
	public RandomDataGenerator(int numOfItem, int numOfTag,
                               double probOfOne, String outputFileName) {
	
		this.numOfItem = numOfItem;
		this.numOfTag = numOfTag;
		this.probOfOne = probOfOne;
		
		this.outputFileName = outputFileName;
		
		try {
			generateData();
			writeDataMatrixToFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generateData() {
		
		Random rand = new Random();
		
		itemToTagMatrix = new int[numOfItem][numOfTag];
		itemName = new String[numOfItem];
		tagName = new String[numOfTag];
		
		for(int i = 0; i < numOfItem; i++) {
			for(int j = 0; j < numOfTag; j++) {
				if(rand.nextDouble() < probOfOne) {
					itemToTagMatrix[i][j] = 1;
					numOfOne++;
				} else {
					itemToTagMatrix[i][j] = 0;
				}
			}
		}
		
		for(int i = 0; i < numOfItem; i++) {
			itemName[i] = "Item" + i;
		}
		
		for(int j = 0; j < numOfTag; j++) {
			tagName[j] = "Tag" + j;
		}
	}
	
	private void writeDataMatrixToFile() throws IOException {
		
		FileWriter fw = new FileWriter(outputFileName, false);
		
		fw.write(Integer.toString(numOfItem) + "\r\n");
		fw.write(Integer.toString(numOfTag) + "\r\n");
		fw.write(Integer.toString(numOfOne) + "\r\n");
		fw.write("\r\n");
		
		for(int i = 0; i < numOfItem; i++) {
			fw.write(itemName[i] + "\r\n");
		}
		fw.write("\r\n");
		
		for(int j = 0; j < numOfTag; j++) {
			fw.write(tagName[j] + "\r\n");
		}
		fw.write("\r\n");
		
		for(int i = 0; i < numOfItem; i++) {
			for(int j = 0; j < numOfTag; j++) {
				if(itemToTagMatrix[i][j] == 1) {
					fw.write(i + "," + j + "\r\n");
				}
			}
		}
		fw.write("\r\n");
		
		fw.close();
	}
	
}
