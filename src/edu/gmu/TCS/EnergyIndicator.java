package edu.gmu.TCS;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EnergyIndicator implements Comparable<EnergyIndicator> {

	private String appId;
	private String category;
	//private int apiUsageCounter;
	private int index;
	private Double totalEnergyConsumption;
	public static final int NUM_Q = 7;

	private EnergyIndicator(String category, String appId, double totalEnergyConsumption) {
		this.appId = appId;
		this.category = category;
		this.totalEnergyConsumption = totalEnergyConsumption;
	}

	public EnergyIndicator(String appId, String category) {
		this(category, appId, 0);
	}

	private EnergyIndicator(String[] line) {
		this(line[0], line[1], Double.valueOf(line[2]));
	}

	public void addUsage(Double value) {
		totalEnergyConsumption += value;
	}

	public static void toCSV(String outputFile, Set<EnergyIndicator> indicators) {
		try (FileWriter writer = new FileWriter(outputFile)) {
			writer.write("category,appId,usage,rank" + System.lineSeparator());
			for (EnergyIndicator indicator : indicators) {
				writer.write(indicator.toString() + System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Set<EnergyIndicator> fromCSV(String inputFile) {
		Set<EnergyIndicator> indicators = new HashSet<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				indicators.add(new EnergyIndicator(line.split(";")));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return indicators;
	}

	public static void rankIndices(List<EnergyIndicator> indicators) {
		Map<String, List<EnergyIndicator>> appsByCat = new HashMap<>();
		for (EnergyIndicator indicator : indicators) {
			List<EnergyIndicator> indicatorList = appsByCat.get(indicator.getCategory());
			if (indicatorList == null) {
				indicatorList = new ArrayList<>();
				appsByCat.put(indicator.getCategory(), indicatorList);
			}
			indicatorList.add(indicator);
		}
		for (String category : appsByCat.keySet()) {
			List<EnergyIndicator> indicatorsInCategory = appsByCat.get(category);
			Collections.sort(indicatorsInCategory);
			double qSize = Math.ceil(indicatorsInCategory.size() / (double) NUM_Q);
			for (int i = 0; i < indicatorsInCategory.size(); i++) {
				indicatorsInCategory.get(i).index = (int) Math.ceil(i / qSize);
			}
		}
	}

	@Override
	public String toString() {
		return category + "," + appId + "," + totalEnergyConsumption + "," + index;
	}

	@Override
	public int compareTo(EnergyIndicator o) {
		if (this.totalEnergyConsumption > o.totalEnergyConsumption)
			return 1;
		if (this.totalEnergyConsumption < o.totalEnergyConsumption)
			return -1;
		return 0;
	}

	public String getAppId() {
		return appId;
	}

	public String getCategory() {
		return category;
	}

	public int getIndex() {
		return index;
	}

	public Rank getRank() {
		for (Rank rank : Rank.values()) {
			if (rank.rank == this.index)
				return rank;
		}
		return Rank.R7;
	}

	public enum Rank {
		R1(1, "#006600"), R2(2, "#33CC33"), R3(3, "#CCFF66"), R4(4, "#FFFF00"), R5(5, "#FFCC00"), R6(6, "#FF6600"), R7(7, "#FF0000");
		private Rank(int rank, String color) {
			this.rank = rank;
			this.color = color;
		}

		int rank;
		String color;

		public int getRank() {
			return rank;
		}

		public String getColor() {
			return color;
		}

	}

}
