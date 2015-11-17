package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import soot.baf.internal.BVirtualInvokeInst;

public class cumulativeCoveredNodeCalculator {
	
	public static int getCoverage() throws IOException{
		//String[] apps = {"a2dp.Vol","anupam.acrylic","at.univie.sensorium","de.hechler.andfish","com.templaro.opsiz.aka","com.nolanlawson.apptracker"};
		/*
		"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo",
				"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
				"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
				"pro.oneredpixel.l9droid"
		 */
		String[] apps = {"pro.oneredpixel.l9droid"};
		//int[] sels = {10,20,30,40,50,60,70,80,90, 100, 200, 300, 400, 500, 600, 700, 800, 900};
		//int[] sels = {1,2,3,4,5,6,7,8,9,10,20,30,40,50,60,70,80,90};
		int[] sels = {10,20,30,40,50,60,70,80,90};
		int iteration = 1;
		
		for(String app:apps){
			System.out.println(app+"\n");
			for(int sel:sels){
				//System.out.println(sel+":");
				double totalSum = 0;
				for(int x=0; x<iteration; x++){
					int bcoverage = 0;
					double ecoverage = 0.0;
					returnType rt = getVectors(app, sel, x);
					List<String> bitVectors = new ArrayList<>();
					List<String> eVectors = new ArrayList<>();
					bitVectors = rt.bitVector;
					eVectors = rt.eVector;
					boolean[] bcovered = new boolean[bitVectors.get(0).split(",").length];
					boolean[] ecovered = new boolean[eVectors.get(0).split(",").length];
					for(int i=0; i<bitVectors.size(); i++){
						//System.out.println(bitVectors.get(i).toString());
						int btmp = 0;
						int etmp = 0;
						double etmp1 = 0.0;
						String[] btokens = bitVectors.get(i).split(",");
						String[] etokens = eVectors.get(i).split(",");
						int bit = 0;
						double e = 0.0;
						for(int j=0; j<btokens.length; j++){
							bit = Integer.parseInt(btokens[j].substring(1, 2));
							if(bit == 1){
								if(bcovered[j] == false){
									bcovered[j] = true;
									btmp++;
								}
								
							}
							if(etokens[j].contains("["))
								e = Double.parseDouble(etokens[j].substring(1, etokens[j].length()));
							else{
								if(etokens[j].contains("]"))
									e = Double.parseDouble(etokens[j].substring(1, etokens[j].length()-1));
								else
									e = Double.parseDouble(etokens[j].substring(1, etokens[j].length()));
							}
							if(e != 0.0){
								if(ecovered[j] == false){
									ecovered[j] = true;
									ecoverage += e;
									etmp++;
								}
								etmp1 += e;
							}
						}
						//System.out.println("new covered bits: "+btmp);
						//System.out.println("*"+etmp1);
						//System.out.println("new covered ebits: "+etmp);
					}
					for(int i=0; i<bcovered.length; i++){
						if(bcovered[i] == true)
							bcoverage++;
					}
					//System.out.println("*"+bcoverage);
					totalSum += ecoverage;
					//System.out.println("*"+ecoverage);
				}
				System.out.println(totalSum/iteration);
			}
		}
		return 0;
	}
	
	public static returnType getVectors(String app, int selected, int iteration) throws IOException{
		
		//cummulativeRandom, greedy
		//String selectionType = "greedy";
		String selectionType = "cummulativeRandom";
		String selectionPath = "/Users/reyhanjb/Documents/Projects/TCS/results/Selections/"+selectionType+"/"+app+"/"+iteration+"/"+selected+"/";
		//String selectionPath = "/Users/reyhanjb/Documents/Projects/TCS/results/Selections/"+selectionType+"/"+selected+"/"+app+"/";
		//String selectionPath = "/Users/reyhanjb/Documents/Projects/TCS/results/Selections/"+selectionType+"/100_500/"+selected+"/"+app+"/lineCoverage/";
		//String selectionPath = "/Users/reyhanjb/Documents/Projects/TCS/results/Selections/"+selectionType+"/100_500/"+selected+"/"+app+"/energyCoverage/";
		String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors_1.txt";
		List<Integer> tests = new ArrayList<>();
		List<String> bitVectors = new ArrayList<>();
		List<String> eVectors = new ArrayList<>();
		File folder = new File(selectionPath);
		//System.out.println(folder.getCanonicalPath());
		File[] files = folder.listFiles();
		for(File file:files){
			if(file.getName().contains("coverage.txt")){
				tests.add(Integer.parseInt(file.getName().split("_")[1]));
			}
		}
		BufferedReader bf = new BufferedReader(new FileReader(bitVectorPath));
		String line = bf.readLine();
		int count = 0;
		while(line != null){
			if(tests.contains(count)){
				bitVectors.add(line.split("\n")[0]);
				//System.out.println(count+" : "+line);
				line = bf.readLine();
				eVectors.add(line.split("\n")[0]);
				//System.out.println(count+" : "+line);
				line = bf.readLine();
				count++;
			}
			else{
				line = bf.readLine();
				line = bf.readLine();
				count++;
			}
		}
		bf.close();
		return new returnType(bitVectors, eVectors);
	}
}

class returnType{
	
	List<String> bitVector;
	List<String> eVector;
	
	public returnType(List<String> bitVector,List<String> eVector){
		this.bitVector = bitVector;
		this.eVector = eVector;
	}
}
