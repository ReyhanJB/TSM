package edu.gmu.TCS.Selection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;



public class GreedySelection {
	
	public static void simpleSelect(int total) throws IOException{
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		String[] apps = {"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo"};
		int[] subsets = {1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,70,80,90};
		//int[] subsets = {10,20,30,40,50,60,70,80,90,100,200,300,400,500,600,700,800,900};
		String srcPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/100_500";
		String Path = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/";
		
		for(int subset:subsets){
			String dstPath = "/Users/reyhanjb/Documents/Projects/TCS/results/Selections/greedy/100_500/"+subset;
			int selected = 0;
			List<Integer> tests = new ArrayList<>();
			for(int i=0; i<total; i++)
				tests.add(i);

			for(String app:apps){
				String Path1 = Path+app+"/";
				int i=0;
				List<Integer> lineCoverage = getLineCoverages(Path1);
				List<Integer> lTests = new ArrayList<>(tests);
				sort(lineCoverage,lTests);
				List<Double> eCoverage = getEnergyCoverages(Path1);
				List<Integer> eTests = new ArrayList<>(tests);
				sort(eCoverage,eTests);
				
				while(i<subset){
					selected = lTests.get(total-i-1);
					copy(srcPath, dstPath, app, selected,0);
					//selected = eTests.get(total-i-1);
					//copy(srcPath, dstPath, app, selected,1);
					i++;
				}
			}
		}
		
	}
	
	public static <T extends Comparable<T>> void sort(List<T> list,List<Integer> tests){
		
		for(int i=0; i<list.size(); i++){
			int minIndex = i;
			for(int j=i+1; j<list.size(); j++){
				if(list.get(j).compareTo(list.get(minIndex)) < 0)
					minIndex = j;
			}
			//swap min with current
			T tmp = list.get(i);
			T tmp1 = list.get(minIndex);
			list.remove(i);
			list.add(i, tmp1);
			list.remove(minIndex);
			list.add(minIndex, tmp);
			int tmpi = tests.get(i);
			int tmpi1 = tests.get(minIndex);
			tests.remove(i);
			tests.add(i, tmpi1);
			tests.remove(minIndex);
			tests.add(minIndex, tmpi);	
		}
	}
	
	public static List<Integer> getLineCoverages(String Path) throws IOException{
		
		List<Integer> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(Path+"lineCoverage.txt"));
		String line = br.readLine();
		while(line != null){
			list.add(Integer.parseInt(line.split(":")[0].split(" ")[0]), Integer.parseInt(line.split(":")[1].split(" ")[1]));
			//list.add(Integer.parseInt(line.split(":")[0]), Integer.parseInt(line.split(":")[1]));
			line = br.readLine();
		}
		br.close();
		return list;
	}
	
	public static List<Double> getEnergyCoverages(String Path) throws IOException{
		
		List<Double> list = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(Path+"eCoverage.txt"));
		String line = br.readLine();
		while(line != null){
			String tmp = line.split(" : ")[1];
			tmp = tmp.split(",")[tmp.split(",").length-1];
			tmp = tmp.split(" ")[1];
			list.add(Integer.parseInt(line.split(":")[0].split(" ")[0]), Double.parseDouble(tmp));
			line = br.readLine();
		}
		br.close();
		return list;
	}
	
	public static Map<Integer,boolean[]> getPaths(int total, String Path) throws FileNotFoundException{
		
		return null;
	}
	
	public static void copy(String srcPath, String dstPath, String app, int selected, int coverage) throws IOException{
		
		String coverageType = "";
		if(coverage == 0)
			coverageType = "lineCoverage";
		if(coverage == 1)
			coverageType = "energyCoverage";
		if(coverage == 2)
			coverageType = "DscoreCoverage";
		File srcCov = new File(srcPath+"/"+app+"/"+app+"_"+selected+"_coverage.txt");
		File srcLog = new File(srcPath+"/"+app+"/"+app+"_"+selected+"_log.txt");
		File dstCov = new File(dstPath+"/"+app+"/"+coverageType+"/"+app+"_"+selected+"_coverage.txt");
		File dstLog = new File(dstPath+"/"+app+"/"+coverageType+"/"+app+"_"+selected+"_log.txt");
		FileUtils.copyFile(srcCov, dstCov, true);
		FileUtils.copyFile(srcLog, dstLog, true);
	}
}
