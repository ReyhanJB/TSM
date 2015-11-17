package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class testGenerator {

	public static void generate() throws IOException{
		
		List<Integer> indices = new ArrayList<>();
		List<Double> energies = new ArrayList<>();
		String[] apps = {"a2dp.Vol"};
		String path = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/";
		Random rand = new Random();
		for(String app: apps){
			List<List<Integer>> baseTests = new ArrayList<>();
			List<List<Double>> baseTestse = new ArrayList<>();
			List<List<Integer>> testSuite = new ArrayList<>();
			List<List<Double>> testSuitee = new ArrayList<>();
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/bitVectors.txt";
			List<returnType1> rts = getVectors(app, bitVectorPath);
			int vectorSize = rts.get(0).bitVector.size();//To get the total number of nodes in graph
			for(returnType1 rt: rts){
				for(int i=0; i<rt.bitVector.size(); i++){
					if(rt.bitVector.get(i) == 1){
						if(!indices.contains(i)){
							indices.add(i);//Contains the indices of all energy-greedy nodes
							energies.add(rt.eVector.get(i));
						}
					}
				}
			}
			
			int count = 0;
			while(count < 18){//58 number of remaining to reach 100 test case
				int random = rand.nextInt(vectorSize);
				if(!indices.contains(random)){
					int ranomTmp = rand.nextInt(indices.size());
					indices.add(ranomTmp,random);
					energies.add(ranomTmp,0.0);
					count++;
				}
			}
			System.out.println(indices.toString());
			System.out.println(energies.toString());
			System.out.println(indices.size());
			
			int start = 0;
			int end = 20;
			int offset = 20;//Size of uniqueness
			
			for(int i=0; i<5; i++){
				List<Integer> tmp = indices.subList(start, end);
				List<Integer> v = new ArrayList<>();
				List<Double> ve = new ArrayList<>();
				for(int j=0; j<vectorSize; j++){
					if(tmp.contains(j)){
						v.add(j, 1);
						int index = indices.indexOf(j);
						ve.add(energies.get(index));
					}
					else{
						v.add(j, 0);
						ve.add(0.0);
					}
				}
				start = end;
				end += offset;
				baseTests.add(v);
				baseTestse.add(ve);
			}

			for(int i=0; i<100; i++){
				List<Integer> subIndices = new ArrayList<>();
				int baseChoice = rand.nextInt(5);//One of the unique base tests
				int addedNodes = rand.nextInt(5);
				List<Integer> v = new ArrayList<Integer>(baseTests.get(baseChoice));
				List<Double> ve = new ArrayList<Double>(baseTestse.get(baseChoice));
				for(int j=0; j<addedNodes; j++){
					int x = rand.nextInt(indices.size());
					subIndices.add(indices.get(x));
				}
				for(int j=0; j<subIndices.size(); j++){
					int y = subIndices.get(j);
					v.remove(y);
					v.add(subIndices.get(j), 1);
					ve.remove(y);
					int index = indices.indexOf(subIndices.get(j));
					ve.add(subIndices.get(j), energies.get(index));
				}
				testSuite.add(v);
				testSuitee.add(ve);
			}
			
			FileWriter fw = new FileWriter(path+app+"/new_bitVectors_6.txt");
			FileWriter fw1 = new FileWriter(path+app+"/new_lineCoverage_6.txt");
			for(int i=0; i<testSuite.size(); i++){
				fw.write(testSuite.get(i).toString()+"\n");
				int lineCovered = 0;
				for(int j:testSuite.get(i)){
					if(j == 1)
						lineCovered++;
				}
				double out = ((lineCovered*100)/testSuite.get(i).size());
				//double ecovered = 0.0;
				fw.write(testSuitee.get(i).toString()+"\n");
				/*for(Double j:testSuitee.get(i)){
					if(j != 0.0)
						ecovered += j;
				}*/
				fw1.write(i+" : "+((int)out+3)+"\n");
				//System.out.println((int)out);
				
			}
			fw.close();
			fw1.close();
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
