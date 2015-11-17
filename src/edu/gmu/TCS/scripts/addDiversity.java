package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class addDiversity {

	public static void add() throws IOException{
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		String[] apps = {"acr.browser.barebones"};
		for(String app:apps){
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/bitVectors.txt";
			String newBitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors.txt";
			FileWriter fw = new FileWriter(newBitVectorPath);
			//System.out.println("\n"+app+"\n");
			List<returnType3> rts = getVectors(app, bitVectorPath);
			for(returnType3 rt:rts){
				for(int i=0; i<rt.bitVector.size(); i++){
					if(rt.bitVector.get(i) == 1){
						if(rt.eVector.get(i) == 0.0){
							rt.eVector.remove(i);
							rt.eVector.add(i, 1e-8);
						}
					}
				}
				fw.write(rt.bitVector.toString()+"\n");
				fw.write(rt.eVector.toString()+"\n");
			}
			fw.close();
		}
	}
	
	public static void addAnthropy() throws IOException{
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		String[] apps = {"a2dp.Vol"};
		Random rand = new Random();
		for(String app:apps){
			//String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors.txt";
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors.txt";
			String newBitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors_1.txt";
			FileWriter fw = new FileWriter(newBitVectorPath);
			//System.out.println("\n"+app+"\n");
			List<returnType3> rts = getVectors(app, bitVectorPath);
			for(returnType3 rt:rts){
				int count = 0;
				//System.out.println(rt.bitVector.toString());
				while(count < 1){//Set "10" random bits to one, in order to add diversity to the test suite
					int random = rand.nextInt(rt.bitVector.size());
					//System.out.println(random);
					if(rt.bitVector.get(random) != 1){
						rt.bitVector.remove(random);
						rt.bitVector.add(random, 1);
						rt.eVector.remove(random);
						rt.eVector.add(random, 1e-8);
						count++;
					}
				}
				System.out.println();
				fw.write(rt.bitVector.toString()+"\n");
				fw.write(rt.eVector.toString()+"\n");
			}
			fw.close();
		}
	}
	
	public static List<returnType3> getVectors(String app, String bitVectorPath) throws IOException{
		
		String bitVector = "";
		String eVector = "";
		List<returnType3> result = new ArrayList<>();
		
		BufferedReader bf = new BufferedReader(new FileReader(bitVectorPath));
		String line = bf.readLine();
		int count = 0;
		while(line != null){
			bitVector = line.split("\n")[0];
			line = bf.readLine();
			eVector = line.split("\n")[0];
			line = bf.readLine();
			result.add(new returnType3(count, bitVector,eVector));
			count++;
		}
		bf.close();
		
		return result;
	}
}

class returnType3{
	
	public int bitCount;
	public List<Integer> bitVector;//Contains 0 and 1
	public List<Double> eVector;
	public int testCaseNumber;
	
	public returnType3(int testCaseNumber, String bitVectorS, String eVectorS){
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
	}
	
	public returnType3(returnType3 rt){
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