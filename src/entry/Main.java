package entry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javafx.util.Pair;

public class Main {

	private static final String fileName = "ProbA";

	private static double[] weight;
	private static double[][] position;
	private static int[] solution;

	private static List<Double> bestMeasures;
	private static List<Double> tempMeasures;

	public static void main(String args[]) {
		weightReader("src/data/" + fileName + ".txt");
		positionReader("src/data/Positions.txt");
		bestMeasures = new ArrayList<Double>();
		tempMeasures = new ArrayList<Double>();
		// nextDescentSearch200(solution);
		tabuSearch(solution);
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
				solution[i - 1] = i;
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

	public static void tabuSearch(int[] s) {
		boolean[][] flag = new boolean[120][120];
		int banListSize = Math.min(20, weight.length / 3);
		int[][] banList = new int[banListSize][2];
		int count = 0;
		int worsen = 50000;

		double currentX = 0;
		double currentY = 0;
		double sumWeight = 0;
		for (int i = 0; i < 120; i++) {
			currentX += weight[s[i]] * position[i][0];
			currentY += weight[s[i]] * position[i][1];
			sumWeight += weight[s[i]];
		}
		int[] current = s.clone();
		while (count < 100000 && count < worsen * 2) {
			System.out.println((Math.abs(currentX) + 5 * Math.abs(currentY)) / sumWeight);
			int[] currentPosition = new int[2];
			double iterationBestX = Double.POSITIVE_INFINITY;
			double iterationBestY = Double.POSITIVE_INFINITY;
			for (int a = 0; a < 120; a++) {
				for (int b = a + 1; b < 120; b++) {
					if (flag[a][b] || (weight[current[a]] == 0 && weight[current[b]] == 0) || a == b - 60) {
						continue;
					} else {
						double tempX = currentX + weight[current[a]] * position[b][0]
								+ weight[current[b]] * position[a][0] - weight[current[a]] * position[a][0]
								- weight[current[b]] * position[b][0];
						double tempY = currentY + weight[current[a]] * position[b][1]
								+ weight[current[b]] * position[a][1] - weight[current[a]] * position[a][1]
								- weight[current[b]] * position[b][1];
						double tempMeasure = Math.abs(tempX) + 5 * Math.abs(tempY);
						double iterationBestMeasure = Math.abs(iterationBestX) + 5 * Math.abs(iterationBestY);
						if (tempMeasure < iterationBestMeasure) {
							iterationBestX = tempX;
							iterationBestY = tempY;
							currentPosition[0] = a;
							currentPosition[1] = b;
						}
					}
				}
			}
			int temp = current[currentPosition[0]];
			current[currentPosition[0]] = current[currentPosition[1]];
			current[currentPosition[1]] = temp;
			flag[banList[count % banListSize][0]][banList[count % banListSize][1]] = false;
			flag[currentPosition[0]][currentPosition[1]] = true;
			banList[count % banListSize][0] = currentPosition[0];
			banList[count % banListSize][1] = currentPosition[1];
			double currentMeasure = Math.abs(currentX) + 5 * Math.abs(currentY);
			double iterationBestMeasure = Math.abs(iterationBestX) + 5 * Math.abs(iterationBestY);
			if (currentMeasure < iterationBestMeasure && worsen == 50000) {
					worsen = count;
			}
			currentX = iterationBestX;
			currentY = iterationBestY;
			count++;
		}
		System.out.println(count);
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
			int[] newSolution = nextDescentSearch(s);
			if (getMeasure(newSolution) < getMeasure(bestSolution)) {
				bestSolution = newSolution;
			}
		}

		try {
			PrintWriter pw = new PrintWriter("src/" + fileName + "output.txt");
			pw.write(getMeasure(bestSolution) + "\n");
			for (int i = 0; i < bestSolution.length; i++) {
				pw.write(bestSolution[i] + "\n");
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
