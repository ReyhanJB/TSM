package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class eCoverageCalculator {

	public static void calculate() throws IOException{
		
		/*
		"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
				"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
				"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
				"pro.oneredpixel.l9droid"
		26.408836853200025,20.430489331,9.669502994000004,1.112970202,5.352379292500004,8.415134741000003,
				6.186863846999999,4.666723664499994,18.68957263700001,3.2836990975,
				3.1565782149999997,16.59873554150001,5.531347406999992,12.548890719000008,6.9937688860000025,
				11.986800738199998
		 */
		String[] apps = {"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
				"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
				"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
				"pro.oneredpixel.l9droid"};
		String path = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/";
		Double[] sScore = {26.408836853200025,20.430489331,9.669502994000004,1.112970202,5.352379292500004,8.415134741000003,
				6.186863846999999,4.666723664499994,18.68957263700001,3.2836990975,
				3.1565782149999997,16.59873554150001,5.531347406999992,12.548890719000008,6.9937688860000025,
				11.986800738199998};
		
		for(int i=0; i<apps.length; i++){
			String app = apps[i];
			//String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors.txt";
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors_1.txt";
			
			FileWriter fw = new FileWriter(path+app+"/new_eCoverage_1.txt");
			//System.out.println("\n"+app+"\n");
			List<returnType1> rts = getVectors(app, bitVectorPath);
			
			for(returnType1 rt: rts){
				Double eCov = 0.0;
				for(Double d:rt.eVector){
					eCov += d;
				}
				fw.write(rt.testCaseNumber+" : "+eCov+", "+eCov/sScore[i]+"\n");
			}
			fw.close();
		}
	}
	
	public static List<returnType1> getVectors(String app, String bitVectorPath) throws IOException{
		
		String bitVector = "";
		String eVector = "";
		List<returnType1> result = new ArrayList<>();
		
		BufferedReader bf = new BufferedReader(new FileReader(bitVectorPath));
		String line = bf.readLine();
		int count = 0;
		while(line != null){
			bitVector = line.split("\n")[0];
			line = bf.readLine();
			eVector = line.split("\n")[0];
			line = bf.readLine();
			result.add(new returnType1(count, bitVector,eVector));
			count++;
		}
		bf.close();
		
		return result;
	}
}

class returnType1{
	
	Double energyValue;
	Double energyTmp;
	List<Integer> bitVector;//Contains 0 and 1
	List<Double> eVector;
	int testCaseNumber;
	
	public returnType1(int testCaseNumber, String bitVectorS, String eVectorS){
		double ecoverage = 0.0;
		double e = 0.0;
		int bit = 0;
		this.testCaseNumber = testCaseNumber;
		String[] btokens = bitVectorS.split(",");
		String[] etokens = eVectorS.split(",");
		bitVector = new ArrayList<>();
		eVector = new ArrayList<>();
			
		for(int j=0; j<btokens.length; j++){
			bit = Integer.parseInt(btokens[j].substring(1, 2));
			if(etokens[j].contains("["))
				e = Double.parseDouble(etokens[j].substring(1, etokens[j].length()));
			else{
				if(etokens[j].contains("]"))
					e = Double.parseDouble(etokens[j].substring(1, etokens[j].length()-1));
				else
					e = Double.parseDouble(etokens[j].substring(1, etokens[j].length()));
			}
			eVector.add(e);
			bitVector.add(bit);
			if(e != 0.0)
				ecoverage += e;			
		}
		energyValue = ecoverage;
		energyTmp = ecoverage;
	}
	
	public returnType1(returnType1 rt){
		this.energyValue = new Double(rt.energyValue);
		this.energyTmp = new Double(rt.energyTmp);
		this.testCaseNumber = rt.testCaseNumber;
		bitVector = new ArrayList<>();
		eVector = new ArrayList<>();
		for(int i=0; i<rt.bitVector.size(); i++){
			bitVector.add(new Integer(rt.bitVector.get(i)));
			eVector.add(new Double(rt.eVector.get(i)));
		}
	}
	
	public String toString(){//Prints the testcase number of the returnType
		return testCaseNumber+"";
	}
}