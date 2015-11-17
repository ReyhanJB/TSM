package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EDTSO {

	public static void generate() throws IOException{
		
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		
		/*String[] apps = {"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo",
				"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
				"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
				"pro.oneredpixel.l9droid"};*/
		String[] apps = {"org.blockinger.game"};
		for(String app:apps){
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors_1.txt";
			//String eCoveragePath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/eCoverage.txt";
			String energyPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/eCoverage.txt";
			Map<Integer,List<returnType_2>> constraints = new HashMap<>();
			List<returnType_2> rts = getVectors(app, bitVectorPath);
			//System.out.println(rts.get(0).bitVector);
			//System.out.println(rts.get(0).eVector);
			int[] greedyList = new int[rts.get(0).bitVector.size()];
			String lastLine = "bin ";
			String firstLine = "min: ";
			String constraint = "";
			FileWriter fw = new FileWriter("/Users/reyhanjb/Documents/Projects/TCS/LP_Solve/models/EDTSO/reverse/"+app+".lp");
			BufferedReader br = new BufferedReader(new FileReader(energyPath));
			String tmp = br.readLine();
			//tmp = br.readLine();
			
			int count = 0;
			//while(!tmp.equals("*****************************************************")){
			while(tmp != null){
				tmp = tmp.split(",")[1].split(" ")[1];
				//tmp = tmp.split(":")[1].split(" ")[1];
				//***Double energy = 1-Double.valueOf(tmp);
				Double energy = 1/Double.valueOf(tmp);
				//Double energy = Double.valueOf(tmp);
				System.out.println(energy);
				firstLine = firstLine+energy+" t"+count+" + "; 
				//firstLine = firstLine+" t"+count+" + ";
				constraint = constraint+" t"+count+" +";
				tmp = br.readLine();
				count++;
			}
			
			firstLine = firstLine.substring(0, firstLine.length()-3);
			constraint = constraint.substring(0, constraint.length()-3);
			constraint = constraint+" = 1;";
			fw.write(firstLine+";\n\n");
			
			for(returnType_2 rt:rts){
				lastLine = lastLine + "t" + rt.testCaseNumber+", ";
				for(int i=0; i<rt.bitVector.size(); i++){
					if(rt.bitVector.get(i) == 1){// && rt.eVector.get(i) != 0.0){
						greedyList[i] = 1;
					}
				}
			}
			lastLine = lastLine.substring(0, lastLine.length()-2);
			System.out.println(greedyList.length);
			
			int constraintCount = 0;
			for(int i=0; i<greedyList.length; i++){
				if(greedyList[i] == 1){
					String line = "";
					constraintCount++;
					line += "c"+constraintCount+": ";
					List<returnType_2> tests = new ArrayList<>();
					for(returnType_2 rt:rts){
						if(rt.bitVector.get(i) == 1){
							tests.add(rt);
							line += "t"+rt.testCaseNumber+" + ";
						}
					}
					constraints.put(i, tests);
					line = line.substring(0, line.length()-3);
					fw.write(line+" >= 1;\n");
				}
			}
			constraintCount++;
			//fw.write("c"+constraintCount+": "+constraint+"\n");
			fw.write("\n"+lastLine+";");
			fw.close();
			br.close();
		}
	}
	
	public static List<returnType_2> getVectors(String app, String bitVectorPath) throws IOException{
		
		String bitVector = "";
		String eVector = "";
		List<returnType_2> result = new ArrayList<>();
		
		BufferedReader bf = new BufferedReader(new FileReader(bitVectorPath));
		String line = bf.readLine();
		int count = 0;
		while(line != null){
			bitVector = line.split("\n")[0];
			line = bf.readLine();
			eVector = line.split("\n")[0];
			line = bf.readLine();
			result.add(new returnType_2(count, bitVector,eVector));
			count++;
		}
		bf.close();
		
		return result;
	}
}

class returnType_2{
	
	Double energyValue;
	Double energyTmp;
	int bitCount;
	List<Integer> bitVector;//Contains 0 and 1
	List<Double> eVector;
	int testCaseNumber;
	
	public returnType_2(int testCaseNumber, String bitVectorS, String eVectorS){
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
			if(bit == 1)
				bitCount++;
			if(e != 0.0)
				ecoverage += e;			
		}
		energyValue = ecoverage;
		energyTmp = ecoverage;
	}
	
	public returnType_2(returnType_2 rt){
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
