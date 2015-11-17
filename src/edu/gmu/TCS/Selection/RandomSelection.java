package edu.gmu.TCS.Selection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public class RandomSelection {
	public static void select(int total,int subset) throws IOException{
		
		/*
		"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo",
				"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
				"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
				"pro.oneredpixel.l9droid"
		 */
		
		Random rand = new Random();
		String dstPath = "/Users/reyhanjb/Documents/Projects/TCS/results/Selections/random/"+subset;
		String srcPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/100_500";
		String[] apps = {"a2dp.Vol"};
		File srcCov, srcLog;
		File dstCov, dstLog;
		List<Integer> duplicate = new ArrayList<>();
		
		for(String app: apps){
			int i = 0;
			while(i < subset){
				int j = rand.nextInt(total+1);
				if(!duplicate.contains(j)){
					duplicate.add(j);
					//Copy the selected test case to path
					srcCov = new File(srcPath+"/"+app+"/"+app+"_"+j+"_coverage.txt");
					srcLog = new File(srcPath+"/"+app+"/"+app+"_"+j+"_log.txt");
					dstCov = new File(dstPath+"/"+app+"/"+app+"_"+j+"_coverage.txt");
					dstLog = new File(dstPath+"/"+app+"/"+app+"_"+j+"_log.txt");
					FileUtils.copyFile(srcCov, dstCov, true);
					FileUtils.copyFile(srcLog, dstLog, true);
					i++;
				}
				
			}
		}
		
	}
	
	public static void cummulativeRandom(int total) throws IOException{
		Random rand = new Random();
		int subset = 10;//100
		int repeat = 30;
		int minSubset = 10;//10
		String dstPath = "/Users/reyhanjb/Documents/Projects/TCS/results/Selections/cummulativeRandom/";
		String srcPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/100_500";
		//"a2dp.Vol","anupam.acrylic","at.univie.sensorium","de.hechler.andfish","com.templaro.opsiz.aka","com.nolanlawson.apptracker"
		String[] apps = {"pro.oneredpixel.l9droid"};
		File srcCov, srcLog;
		File dstCov, dstLog;
		for(String app: apps){
			for(int x=0; x<repeat; x++){
				List<List<Integer>> selections = new ArrayList<>();
				List<Integer> tests = new ArrayList<>();
				for(int i=0; i<total; i++)
					tests.add(i);
				int selectionIndex = 0;
				while(tests.size() > 0){
					int i=0;
					List<Integer> tmp = new ArrayList<>();
					if(selectionIndex != 0)
						tmp.addAll(selections.get(selectionIndex-1));
					if(selectionIndex < minSubset){
						int t = 0;
						while(t < minSubset){
							int j = rand.nextInt(tests.size());
							tmp.add(tests.get(j));
							tests.remove(j);
							t++;
						}
					}
					else{
						while(i<subset){
							int j = rand.nextInt(tests.size());
							tmp.add(tests.get(j));
							tests.remove(j);
							i++;
						}
					}
					selections.add(tmp);
					selectionIndex++;
				}
				for(List<Integer> selection: selections){
					for(int j: selection){
						srcCov = new File(srcPath+"/"+app+"/"+app+"_"+j+"_coverage.txt");
						srcLog = new File(srcPath+"/"+app+"/"+app+"_"+j+"_log.txt");
						dstCov = new File(dstPath+app+"/"+x+"/"+selection.size()+"/"+app+"_"+j+"_coverage.txt");
						dstLog = new File(dstPath+app+"/"+x+"/"+selection.size()+"/"+app+"_"+j+"_log.txt");
						FileUtils.copyFile(srcCov, dstCov, true);
						FileUtils.copyFile(srcLog, dstLog, true);
					}
				}
			}
		}
	}
}
