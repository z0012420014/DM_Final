package Algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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
	
	
	public RandomDataGenerator(int numOfItem,
                               int numOfTag,
                               double probOfOne,
                               String outputFileName) {
	
		this.numOfItem = numOfItem;
		this.numOfTag = numOfTag;
		this.probOfOne = probOfOne;
		
		this.outputFileName = outputFileName;
	}
	
	
	public void selfTest() {
		
		try {
			generateData();
			displayDataMatrix();
			writeDataMatrixToFile();
			readDataMatrixFromFile();
			displayDataMatrix();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void generateDataAndWriteToFile() {
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
			itemName[i] = "item" + i;
		}
		
		for(int j = 0; j < numOfTag; j++) {
			tagName[j] = "tag" + j;
		}
	}
	
	private void displayDataMatrix() {
		
		System.out.println(numOfItem);
		System.out.println(numOfTag);
		System.out.println(numOfOne);
		System.out.println();
		
		for(int i = 0; i < numOfItem; i++) {
			System.out.println(itemName[i]);
		}
		System.out.println();
		
		for(int j = 0; j < numOfTag; j++) {
			System.out.println(tagName[j]);
		}
		System.out.println();
		
		for(int i = 0; i < numOfItem; i++) {
			for(int j = 0; j < numOfTag; j++) {
				System.out.print(itemToTagMatrix[i][j] + "\t");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private void writeDataMatrixToFile() throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
		
		bw.write(Integer.toString(numOfItem) + "\r\n");
		bw.write(Integer.toString(numOfTag) + "\r\n");
		bw.write(Integer.toString(numOfOne) + "\r\n");
		bw.write("\r\n");
		
		for(int i = 0; i < numOfItem; i++) {
			bw.write(itemName[i] + "\r\n");
		}
		bw.write("\r\n");
		
		for(int j = 0; j < numOfTag; j++) {
			bw.write(tagName[j] + "\r\n");
		}
		bw.write("\r\n");
		
		for(int i = 0; i < numOfItem; i++) {
			for(int j = 0; j < numOfTag; j++) {
				if(itemToTagMatrix[i][j] == 1) {
					bw.write(i + "," + j + "\r\n");
				}
			}
		}
		bw.write("\r\n");
		
		bw.close();
	}
	
	private void readDataMatrixFromFile() throws IOException {
		
		BufferedReader br = new BufferedReader(new FileReader(outputFileName));
		
		numOfItem = Integer.parseInt(br.readLine());
		numOfTag = Integer.parseInt(br.readLine());
		numOfOne = Integer.parseInt(br.readLine());
		br.readLine();
		
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
