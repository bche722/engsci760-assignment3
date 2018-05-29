package entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

	private static final String fileName= "ProbC" ;
	
	private static double[] weight;
	private static double[][] position;
	private static int[] solution;
	
	private static List<Double> bestMeasures;
	private static List<Double> tempMeasures;

	public static void main(String args[]) {
		weightReader("src/data/"+fileName+".txt");
		positionReader("src/data/Positions.txt");
		bestMeasures = new ArrayList<Double>();
		tempMeasures = new ArrayList<Double>();
		nextDescentSearch200(solution);
	}

	public static void weightReader(String path) {
		File file = new File(path);
		try {
			Scanner scanner = new Scanner(file);
			int lineNumber = Integer.parseInt(scanner.nextLine());
			weight = new double[lineNumber + 1];
			solution = new int[120];
			weight[0] = 0;
			for (int i = 1; i < lineNumber + 1; i++) {
				weight[i] = Double.parseDouble(scanner.nextLine().trim());
				solution[i-1]=i;
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void positionReader(String path) {
		File file = new File(path);
		try {
			Scanner scanner = new Scanner(file);
			int lineNumber = Integer.parseInt(scanner.nextLine().trim());
			position = new double[lineNumber][2];
			for (int i = 0; i < lineNumber; i++) {
				String[] temp = scanner.nextLine().split("\\s+");
				position[i][0] = Double.parseDouble(temp[1]);
				position[i][1] = Double.parseDouble(temp[2]);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static double getMeasure(int[] s) {
		double sumX = 0;
		double sumY = 0;
		double sumWeight = 0;
		for (int i = 0; i < 120; i++) {
			sumX += weight[s[i]] * position[i][0];
			sumY += weight[s[i]] * position[i][1];
			sumWeight += weight[s[i]];
		}
		double measure = Math.abs(sumX / sumWeight) + 5 * Math.abs(sumY / sumWeight);
		return measure;
	}

	public static int[] swap(int[] s, int a, int b) {
		int[] result = s.clone();
		result[a] = s[b];
		result[b] = s[a];
		return result;
	}

	public static void nextDescentSearch2(int[] s) {
		int[] bestSolution = s.clone();
		double bestMeasure = getMeasure(bestSolution);
		int[] temp;
		boolean flag = true;
		while (flag) {
			flag = false;
			for (int a = 0; a < 120; a++) {
				for (int b = 0; b < 120; b++) {
					temp = swap(bestSolution, a, b);
					double tempMeasure = getMeasure(temp);
					if (tempMeasure < bestMeasure) {
						bestSolution = temp;
						bestMeasure = tempMeasure;
						flag = true;
					}
					bestMeasures.add(bestMeasure);
					tempMeasures.add(tempMeasure);
				}
			}
		}
		System.out.println(bestMeasure);
		System.out.println(bestMeasures.size());
	}

	public static void nextDescentSearch200(int[] s) {
		int[] bestSolution = s.clone();
		for (int i = 0; i < 200; i++) {
			shuffle(s);
			int[] newSolution =nextDescentSearch(s);
			if(getMeasure(newSolution) < getMeasure(bestSolution)) {
				bestSolution=newSolution;
			}
		}
		
		try {
			PrintWriter pw = new PrintWriter("src/"+fileName+"output.txt");
			pw.write(getMeasure(bestSolution)+ "\n");
			for (int i = 0; i < bestSolution.length; i++) {
				pw.write(bestSolution[i]+ "\n");
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static int[] nextDescentSearch(int[] s) {
		int[] bestSolution = s.clone();
		boolean flag;
		do {
			flag = false;
			for (int a = 0; a < 120; a++) {
				for (int b = 0; b < 120; b++) {
					int[] temp = swap(bestSolution, a, b);
					if (getMeasure(temp) < getMeasure(bestSolution)) {
						bestSolution = temp;
						flag = true;
					}
				}
			}
		} while (flag);
		return bestSolution;
	}

	public static void write() {
		try {
			PrintWriter pw = new PrintWriter("src/output.txt");
			for (int i = 0; i < bestMeasures.size(); i++) {
				pw.write(bestMeasures.get(i) + "	");
				pw.write(tempMeasures.get(i) + "\n");
			}
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void shuffle(int[] s) {
		for (int i = 0; i < 119; i++) {
			int j = i + 1 + (int) (Math.random() * (118 - i));
			int temp = s[i];
			s[i] = s[j];
			s[j] = temp;
		}
	}
}
