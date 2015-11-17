package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class NewILPInputGenerator {

	public static void generate() throws IOException{
		/*
		 "a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo",
				"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
				"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
				"pro.oneredpixel.l9droid"
		 */
		String[] apps = {"org.blockinger.game"};
		Double[] sScores = {12.548890719000008};
		for(int j=0; j<apps.length; j++){
			String app = apps[j];
			System.out.println(app);
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors_1.txt";
			//String eCoveragePath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/eCoverage.txt";
			//Map<Integer,List<returnType2>> constraints = new HashMap<>();
			List<String> constraints = new ArrayList<>();
			List<returnType4> rts = getVectors(app, bitVectorPath);
			Map<Integer,String> map = new HashMap<Integer, String>();
			Map<Integer,String> mapTest = new HashMap<Integer, String>();
			//System.out.println(rts.get(0).bitVector);
			//System.out.println(rts.get(0).eVector);
			//int[] greedyList = new int[rts.get(0).bitVector.size()];
			String lastLine = "bin ";
			String firstLine = "min: ";
			int threshold = 100;//size of test suite
			int constCount = 0;
			//String constraint = "";
			FileWriter fw = new FileWriter("/Users/reyhanjb/Documents/Projects/TCS/LP_Solve/models/IP/reverse/"+app+"_"+threshold+".lp");
			//BufferedReader br = new BufferedReader(new FileReader(eCoveragePath));
			//String tmp = br.readLine();
			Double sScore = sScores[j];
			Double min = 1e-8;
			int conditions = 0;
			
			int x = 0;
			for(returnType4 rt:rts){
				String x_var =  "x_"+(rt.testCaseNumber);
				x++;
				/***if(rt.testCaseNumber == 0)
					firstLine += x_var;
				else
					firstLine += " + "+x_var;***/
				lastLine += x_var+", ";
				for(int i=0; i< rt.bitVector.size(); i++){
					if(rt.bitVector.get(i) != 0){
						constCount++;
						String e_prime_var = "";
						String n_var = "n_"+(rt.testCaseNumber)+"_"+i;
						lastLine += n_var+", ";
						String y_var = "y_"+(rt.testCaseNumber)+"_"+i;
						lastLine += y_var+", ";
						if(rt.eVector.get(i) != 0.0){//If the covered node is an energy greedy node
							//***e_prime_var = (rt.eVector.get(i)/sScore)+"";
							e_prime_var = (sScore/rt.eVector.get(i))+"";
							//***firstLine += " - "+e_prime_var+" "+y_var;
							firstLine += " + "+e_prime_var+" "+y_var;
							constraints.add(y_var+" <= "+x_var+";"); //Formula 6 in the paper
							constraints.add(y_var+" <= "+n_var+";");//It should be ignored?
							constraints.add(y_var+" >= "+x_var+" + "+n_var+" - 1"+";");
							String s = map.get(i);
							if(s == null)
								map.put(i, y_var+" + ");
							else
								map.put(i, s+y_var+" + ");
							String sTest = mapTest.get(i);
							if(sTest == null){
								mapTest.put(i, x_var+" + ");
							}
							else{
								mapTest.put(i, sTest+x_var+" + ");
							}
						}
						else{//It will not execute, since we have assumed all the nodes consume some sort of energy. For those who are not greedy, we assume 1e-8 energy cost
							System.out.println("hi");
							e_prime_var = (min/sScore)+"";
						}
					}
				}
				if(x>threshold){
					break;
				}
			}
				
			//System.out.println(firstLine+";");
			fw.write(firstLine+";\n");
			
			Iterator<Integer> itTest = mapTest.keySet().iterator();
			while(itTest.hasNext()){
				String s = mapTest.get(itTest.next());
				//System.out.println("c"+conditions+": "+s.substring(0, s.length()-3)+ " = 1;");
				fw.write("c"+conditions+": "+s.substring(0, s.length()-3)+ " >= 1;\n");//Formula 7
				conditions++;
			}
			Iterator<Integer> it = map.keySet().iterator();
			while(it.hasNext()){
				String s = map.get(it.next());
				//System.out.println("c"+conditions+": "+s.substring(0, s.length()-3)+ " = 1;");
				fw.write("c"+conditions+": "+s.substring(0, s.length()-3)+ " = 1;\n");//Formula 7
				conditions++;
			}
			for(String s:constraints){
				//System.out.println("c"+conditions+": "+s);
				fw.write("c"+conditions+": "+s+"\n");
				conditions++;
			}
			//System.out.println(lastLine.substring(0, lastLine.length()-2)+";");
			fw.write(lastLine.substring(0, lastLine.length()-2)+";\n");
			
			fw.close();
			System.out.println(conditions);
			System.out.println(x);
		}
	}
	
	public static List<returnType4> getVectors(String app, String bitVectorPath) throws IOException{
		
		String bitVector = "";
		String eVector = "";
		List<returnType4> result = new ArrayList<>();
		
		BufferedReader bf = new BufferedReader(new FileReader(bitVectorPath));
		String line = bf.readLine();
		int count = 0;
		while(line != null){
			bitVector = line.split("\n")[0];
			line = bf.readLine();
			eVector = line.split("\n")[0];
			line = bf.readLine();
			result.add(new returnType4(count, bitVector,eVector));
			count++;
		}
		bf.close();
		
		return result;
	}
}

class returnType4{
	
	Double energyValue;
	Double energyTmp;
	int bitCount;
	List<Integer> bitVector;//Contains 0 and 1
	List<Double> eVector;
	int testCaseNumber;
	
	public returnType4(int testCaseNumber, String bitVectorS, String eVectorS){
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
	
	public returnType4(returnType4 rt){
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
