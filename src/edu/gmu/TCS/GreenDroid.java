package edu.gmu.TCS;

import java.io.BufferedReader;
import java.io.File;
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

import javax.xml.bind.JAXBException;

import org.xmlpull.v1.XmlPullParserException;

import edu.gmu.TCS.callgraph.AugmentedCallGraphGenerator;
import edu.gmu.TCS.callgraph.CGEdge;
import edu.gmu.TCS.callgraph.CGNode;
import edu.gmu.TCS.callgraph.CGEdge.EdgeType;
import edu.gmu.instrument.Instrumentor;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

public class GreenDroid {

	static Map<String, Double> energyGreedyAPIs = new HashMap<>();
	static Set<EnergyIndicator> indicators = new HashSet<>();

//	static String[] apps = new String[] {"au.com.weatherzone.android.weatherzonefreeapp","com.mobileapp.weather","com.accuweather.android","com.mobilityflow.animatedweather.free","com.acmeaom.android.myradar","com.gau.go.launcherex.gowidget.weatherwidget"};

	public static void main(String[] args) {
		try {
			String in = "/Users/reyhanjb/Documents/Projects/TCS/apps/Fdroid/selected_apks/acr.browser.barebones_67.apk";//Path to the apk file
			String out = "/Users/reyhanjb/Documents/Projects/TCS/output/static_analysis_graphs/";//Path to output graphs
			String map = "/Users/reyhanjb/Documents/Projects/TCS/resources/patterns_length1_score.csv";//Path to the API list and their energy usage
			String sourceSinkFile = "/Users/reyhanjb/Documents/Projects/TCS/resources/SourcesAndSinks.txt";
			// args = new String[] {
			// "/Users/alireza/Documents/GMU/MobileSercurity/Tools/app_repo/energy",
			// "/Users/alireza/Documents/GMU/MobileSercurity/Tools/GreenDroid/output/weather.csv",
			// "/Users/alireza/Documents/GMU/MobileSercurity/Tools/GreenDroid/resources/patterns_length1_score.csv",
			// "/Users/alireza/Documents/GMU/MobileSercurity/Tools/app_repo/energy/tmp/Dictionary/"
			// + apps[1] + ".apk", //
			// "/Users/alireza/Documents/GMU/MobileSercurity/Tools/GreenDroid/output/GreenDroidGraphs"
			// };
			// "/Users/alireza/Documents/GMU/MobileSercurity/Tools/app_repo/energy/Weather/com.accuweather.android.apk"
			// };
			// calcAndSaveIndicators(args[0], args[1], args[2]);
			// Set<EnergyIndicator> loadedIndicators =
			// loadAndRankIndicators(args[1]);
			// instrumentLog(args[3], args[0], args[2]);
		 	augmentCallGraph(in, out, map, sourceSinkFile);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void augmentCallGraph(String input, String output, String apiFileAddr, String sourceSinkFile) throws IOException, XmlPullParserException,
			JAXBException {
		processAPIFile(apiFileAddr, false);
		CGNode root = AugmentedCallGraphGenerator.createGraph(input, output, energyGreedyAPIs, sourceSinkFile);
		cScore(root);
	}
	
	//By Reyhan
	public static void cScore(CGNode node) throws IOException{
		//a2dp.Vol,acr.browser.barebones, anupam.acrylic,at.univie.sensorium,com.nolanlawson.apptracker,com.templaro.opsiz.aka,de.hechler.andfish
		String app = "acr.browser.barebones";
		int tests = 100;
		//List<Integer> wholeGraphBitVector = new ArrayList<>();
		//List<Double> wholeGrapheVector = new ArrayList<>();
		//Set the last argument of getPathsBitVector true if it is for the whole CG. Otherwise set it to false to have bit vector of each test case
		//getPathsBitVector(node, new ArrayList<String>(), null, wholeGraphBitVector, wholeGrapheVector, true, new ArrayList<String>());
		//System.out.println("total "+wholeGraphBitVector.size());
		
		String outPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/";
		//FileWriter fw = new FileWriter(outPath+app+"/eCoverage.txt");
		FileWriter fw1 = new FileWriter(outPath+app+"/bitVectors.txt");
		double y = sScore(node, 0);//The overall score of the energy greedy nodes, no matter covered or not
		System.out.println("sScore: "+y);
		
		for(int i=0; i<tests; i++){
			String inPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/"+app+"_"+i+".txt";
			List<String> called = new ArrayList<String>();
			BufferedReader br = new BufferedReader(new FileReader(inPath));
			String line = br.readLine();
			List<Integer> testBitVector = new ArrayList<>();
			List<Double> testeVector = new ArrayList<>();
			List<String> nVector = new ArrayList<>();
			
			while(line != null){
				String[] tokens = line.split(" -> ");
				for(String token: tokens){
					if(!called.contains(token))
						called.add(token);
				}
				line = br.readLine();
			}
			br.close();
			//Double cScore = 0.0;
			getPathsBitVector(node, new ArrayList<String>(), called, testBitVector, testeVector, nVector);

			//double x = traverse(node, called, new ArrayList<String>(), 0.0);			
			//Collections.sort(called);
			//fw1.write(called.toString()+"\n");
			
			//fw1.write(check.toString()+"\n");
			fw1.write(testBitVector.toString()+"\n");
			//System.out.println("bit: "+testBitVector.size());
			//System.out.println("e: "+testeVector.size());
			fw1.write(testeVector.toString()+"\n");
			//fw1.write(nVector.toString()+"\n");
			//fw.write(i+": "+cScore+", "+y+", "+cScore/y+"\n");
		}
		//fw.close();
		fw1.close();
	}
	
	//By Reyhan
	public static void getPathsBitVector(CGNode node, List<String> visited, List<String> called, List<Integer> bitVector, List<Double> eVector, List<String> nVector){
		for(CGEdge edge: node.outEdges){
			String s = edge.toNode.getShortLabel().split("\n")[0].split("\\(")[0]+"."+
					edge.toNode.getShortLabel().split("\n")[0].split("\\(")[1].split("\\)")[0];
			//if(visited.contains(s))
				//continue;
			//visited.add(s);
			if(edge.type == EdgeType.METHOD && !edge.toNode.useless){//(edge.type == EdgeType.METHOD && !edge.toNode.useless) || edge.type == EdgeType.API
				//visited.add(s);
				/*if(whole_test){
					bitVector.add(1);
					if(edge.toNode.energyValue == null){
						eVector.add(0.0);
						getPathsBitVector(edge.toNode, visited, called, bitVector, eVector, whole_test);
					}
					else{
						eVector.add(edge.toNode.energyValue*edge.toNode.getRScore()*1000000);//eVector.add(edge.toNode.energyValue*edge.toNode.getRScore());
						getPathsBitVector(edge.toNode, visited, called, bitVector, eVector, whole_test);
					}
				}
				else{*/
					if(called.contains(s)){
						bitVector.add(1);
						nVector.add(s);
						if(edge.toNode.energyValue == null){
							eVector.add(0.0);
							getPathsBitVector(edge.toNode, visited, called, bitVector, eVector, nVector);
						}
						else{
							eVector.add(edge.toNode.energyValue*edge.toNode.getRScore());//eVector.add(edge.toNode.energyValue*edge.toNode.getRScore());
							getPathsBitVector(edge.toNode, visited, called, bitVector, eVector, nVector);
						}
					}
					else{
						bitVector.add(0);
						nVector.add(s);
						eVector.add(0.0);
						/*if(edge.toNode.energyValue == null){
							getPathsBitVector(edge.toNode, visited, called, bitVector, eVector, whole_test);
						}
						else{*/
						getPathsBitVector(edge.toNode, visited, called, bitVector, eVector, nVector);
						//}
					}
				//}
			}
		}
	}
	
	//By Reyhan
	//Traverse the graph and calculate the total energy cost of the visited energy greedy node
	public static double traverse(CGNode node, List<String> called, List<String> visited, double cScore){
		for(CGEdge edge: node.outEdges){
			String s = edge.toNode.getShortLabel().split("\n")[0].split("\\(")[0]+"."+
					edge.toNode.getShortLabel().split("\n")[0].split("\\(")[1].split("\\)")[0];
			//if(visited.contains(s))
				//continue;
			//visited.add(s);
			if(edge.type == EdgeType.METHOD && !edge.toNode.useless){
				//String s = edge.toNode.getShortLabel().split("\n")[0].split("\\(")[0]+"."+
						//edge.toNode.getShortLabel().split("\n")[0].split("\\(")[1].split("\\)")[0];
				if(called.contains(s)){
					if(edge.toNode.energyValue == null){
						traverse(edge.toNode, called, visited, cScore);
					}
					else{
						traverse(edge.toNode, called, visited, cScore);//+(edge.toNode.energyValue*edge.toNode.getRScore())
						cScore += (edge.toNode.energyValue*edge.toNode.getRScore());
					}
				}
				else
					traverse(edge.toNode, called, visited, cScore);
			}
		}
		return cScore;
	}
	
	//‌By Reyhan
	//Traverse all the nodes in graph recursively and calculates the Sscore
	//node.energyValue: For methods which contain APIs is the sum over cost of the invoked APIs,
	//for methods which have no edge to any API is null
	public static double sScore(CGNode node, double sScore){
		for(CGEdge edge: node.outEdges){
			if(edge.type == EdgeType.METHOD){
				if(edge.toNode.energyValue == null){
					sScore = sScore(edge.toNode, sScore);
				}
				else{
					sScore = sScore(edge.toNode, sScore)+(edge.toNode.energyValue*edge.toNode.getRScore());//+(edge.toNode.energyValue*edge.toNode.getRScore())
				}
				
			}
		}
		return sScore;
	}

	public static void instrumentLog(String input, String output, String apiFileAddr) throws IOException, XmlPullParserException {
		processAPIFile(apiFileAddr, true);
		ProcessManifest manifest = new ProcessManifest(new File(input));
		Instrumentor.instrumentLogTrace(input, output, manifest.getPackageName(), energyGreedyAPIs);
	}

	public static void calcAndSaveIndicators(String input, String output, String apiFileAddr) throws IOException, XmlPullParserException {
		processAPIFile(apiFileAddr, false);
		calculateIndicators(input);
		EnergyIndicator.rankIndices(new ArrayList<>(indicators));
		EnergyIndicator.toCSV(output, indicators);
	}

	public static Map<String, EnergyIndicator> loadAndRankIndicators(String inputFile) {
		Map<String, EnergyIndicator> result = new HashMap<>();
		Set<EnergyIndicator> loadedIndicators = EnergyIndicator.fromCSV(inputFile);
		EnergyIndicator.rankIndices(new ArrayList<>(loadedIndicators));
		for (EnergyIndicator indicator : loadedIndicators) {
			result.put(indicator.getAppId(), indicator);
		}
		return result;
	}

	private static void calculateIndicators(String root) throws IOException, XmlPullParserException {
		File rootDir = new File(root);
		for (File categoryDir : rootDir.listFiles()) {
			if (!categoryDir.isDirectory())
				continue;
			for (File apk : categoryDir.listFiles()) {
				if (apk.getName().endsWith(".apk")) {
					indicators.add(EnergyConsumptionCalculator.calculate(apk.getAbsolutePath(), categoryDir.getName()));
				}
			}
		}
	}

	private static Map<String, Double> processAPIFile(String apiFileAddr, boolean hasMultiple) {
		Map<String, List<Double[]>> tmp = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(apiFileAddr))) {
			String line = reader.readLine();
			while ((line = reader.readLine()) != null) {
				String[] apiVal = line.split(",");
				String api = apiVal[0];
				if (!hasMultiple) {
					energyGreedyAPIs.put(api, Double.valueOf(apiVal[1]));
				} else {
					if (api.contains("(")) {
						String formattedAPI = api.substring(0, api.indexOf("("));
						List<Double[]> avgValues = tmp.get(formattedAPI);
						if (avgValues == null) {
							avgValues = new ArrayList<>();
							tmp.put(formattedAPI, avgValues);
						}
						avgValues.add(new Double[] { Double.valueOf(apiVal[1]), Double.valueOf(apiVal[2]) });
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (hasMultiple) {
			for (String key : tmp.keySet()) {
				List<Double[]> list = tmp.get(key);
				double sumPrd = 0;
				double sum = 0;
				for (Double[] values : list) {
					sumPrd += values[0] * values[1];
					sum += values[0];
				}
				energyGreedyAPIs.put(key, sum == 0 ? 0 : (sumPrd / sum));
			}
			for (String key : energyGreedyAPIs.keySet()) {
				System.out.println(key + "," + energyGreedyAPIs.get(key));
			}
		}
		return energyGreedyAPIs;
	}

}
