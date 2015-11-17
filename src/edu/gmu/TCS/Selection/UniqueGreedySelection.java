package edu.gmu.TCS.Selection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UniqueGreedySelection {

	public static void select() throws IOException{
		
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		String[] apps = {"a2dp.Vol"};
		Double[] sScore = {26.408836853200025,20.430489331,9.669502994000004,1.112970202,5.352379292500004,
				6.186863846999999,4.666723664499994,18.68957263700001,3.2836990975,
				3.1565782149999997,16.59873554150001,5.531347406999992,12.548890719000008,6.9937688860000025,
				11.986800738199998};
		
		for(int i=0; i<apps.length; i++){
			String app = apps[i];
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors_1.txt";
			String lineCoveragePath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/lineCoverage.txt";
			System.out.println("\n"+app+"\n");
			List<returnType> rts = getVectors(app, bitVectorPath, lineCoveragePath);
			eCoverage(rts,sScore[i]);
			//codeCoverage(rts);
		}
	}
	
	public static void eCoverage(List<returnType> input, double sScore){
		List<returnType> rts = new ArrayList<returnType>(input);
		List<Integer> currentCoveredNodes = new ArrayList<>();
		List<Double> tmp = new ArrayList<>(); 
		List<returnType> selected = new ArrayList<>();
		for(int i=0; i<rts.get(0).bitVector.size(); i++)
			currentCoveredNodes.add(0);
		
		returnType maxrt = findMaxrt(rts);
		int count = 0;
		while(count <= 98){
			if(maxrt.energyTmp != 0.0)
				selected.add(maxrt);
			//System.out.println(maxrt.testCaseNumber+" : "+maxrt.energyValue+" : "+maxrt.energyTmp);
			tmp.add(maxrt.energyTmp);
			recalculate(maxrt, rts, currentCoveredNodes);
			maxrt = findMaxrt(rts);
			count++;
		}
		int select = 0;
		for(Double i:tmp){
			if(i != 0.0)
				select++;
		}
		System.out.println("*"+select+"*");
		double value = 0.0;
		for(returnType rt:selected){
			System.out.println(rt.testCaseNumber+": "+(rt.energyTmp/sScore));
			value += (1-(rt.energyTmp/sScore));
			//double tmp1 = 0.0;
			/*for(int i= 0; i<rt.bitVector.size(); i++){
				if(rt.bitVector.get(i) == 1){
					if(rt.eVector.get(i) == 1e-8){
						tmp1 += 1e-1;
					}
				}
			}
			value = value+tmp1;*/
		}
		System.out.println(value);
		int re = 0;
		for(int i:currentCoveredNodes){
			if(i == 1)
				re++;
		}
		System.out.println(re+"*");
	}
	
	public static void codeCoverage(List<returnType> input){
		List<returnType> rts = new ArrayList<returnType>(input);
		List<Integer> currentCoveredNodes = new ArrayList<>();
		List<Integer> tmp = new ArrayList<>(); 
		List<returnType> selected = new ArrayList<>();
		for(int i=0; i<rts.get(0).bitVector.size(); i++)
			currentCoveredNodes.add(0);
		
		returnType maxrt = findMaxrt_bit(rts);
		int count = 0;
		while(count <= 98){
			if(maxrt.bitCount != 0.0)
				selected.add(maxrt);
			//System.out.println(maxrt.testCaseNumber+" : "+maxrt.lineCoverage+" : "+maxrt.bitCount+" : "+maxrt.energyValue+" : "+maxrt.energyTmp);
			tmp.add(maxrt.bitCount);
			recalculate(maxrt, rts, currentCoveredNodes);
			//rts.remove(maxrt);
			maxrt = findMaxrt_bit(rts);
			count++;
		}
		int select = 0;
		for(int i:tmp){
			if(i != 0)
				select++;
		}
		System.out.println("*"+select+"*");
		for(returnType rt:selected)
			System.out.println(rt.testCaseNumber);
	}
	
	public static void recalculate(returnType rt, List<returnType> rts, List<Integer> coveredNodes){
		
		List<Integer> tmp = rt.bitVector;
		for(int i=0; i<tmp.size(); i++){
			if(tmp.get(i) == 1){
				if(coveredNodes.get(i) != 1){
					coveredNodes.remove(i);
					coveredNodes.add(i, 1);
				}
			}
		}
		rts.remove(rt);
		//Update the bitvector and evector of the remaining test cases
		for(int i=0; i<rts.size(); i++){
			for(int j=0; j<rts.get(i).bitVector.size(); j++){
				if(coveredNodes.get(j) == 1){
					if(rts.get(i).bitVector.get(j) == 1){
						rts.get(i).bitVector.remove(j);
						rts.get(i).bitVector.add(j, 0);
						rts.get(i).eVector.remove(j);
						rts.get(i).eVector.add(j, 0.0);
						rts.get(i).bitCount = rts.get(i).bitCount-1;
					}
				}
			}
			double e = 0.0;
			for(int j=0; j<rts.get(i).eVector.size(); j++){
				if(rts.get(i).eVector.get(j) != 0.0)
					e += rts.get(i).eVector.get(j);
			}
			rts.get(i).energyTmp = e;
		}
		//System.out.println(coveredNodes);
	}
	
	private static returnType findMaxrt(List<returnType> rts){
		returnType max = rts.get(0);
		for(int i=1; i<rts.size(); i++){
			if(rts.get(i).energyTmp > max.energyTmp)
				max = rts.get(i);
		}
		return max;
	}
	
	private static returnType findMaxrt_bit(List<returnType> rts){
		returnType max = rts.get(0);
		for(int i=1; i<rts.size(); i++){
			if(rts.get(i).lineCoverage >= max.lineCoverage)
			//if(rts.get(i).bitCount > max.bitCount)
				max = rts.get(i);
		}
		return max;
	}
	
	public static List<returnType> getVectors(String app, String bitVectorPath, String lineCoveragePath) throws IOException{
		
		String bitVector = "";
		String eVector = "";
		List<returnType> result = new ArrayList<>();
		
		BufferedReader bf = new BufferedReader(new FileReader(bitVectorPath));
		BufferedReader bf1 = new BufferedReader(new FileReader(lineCoveragePath));
		String line = bf.readLine();
		String line1 = bf1.readLine();
		int count = 0;
		while(line != null){
			bitVector = line.split("\n")[0];
			line = bf.readLine();
			eVector = line.split("\n")[0];
			line = bf.readLine();
			int lineCoverage = Integer.parseInt(line1.split(":")[1].split(" ")[1]);
			result.add(new returnType(count, lineCoverage, bitVector,eVector));
			count++;
			line1 = bf1.readLine();
		}
		bf.close();
		bf1.close();
		
		return result;
	}
}

class returnType{
	
	public Double energyValue;//eCoverage
	public Double energyTmp;//eCoverage after recalculation
	public int bitCount;
	public List<Integer> bitVector;//Contains 0 and 1
	public List<Double> eVector;
	public int testCaseNumber;
	public int lineCoverage;
	
	public returnType(int testCaseNumber, int lineCoverage, String bitVectorS, String eVectorS){
		double ecoverage = 0.0;
		double e = 0.0;
		int bit = 0;
		this.lineCoverage = lineCoverage;
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
	
	public returnType(returnType rt){
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