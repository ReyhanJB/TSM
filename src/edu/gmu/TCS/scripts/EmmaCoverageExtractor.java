package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class EmmaCoverageExtractor {

	public static void coverageExtractor() throws IOException{
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		String[] apps = {"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"};
		String inPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/100_500";
		String outPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500";
		int testSuiteSize = 100;
		BufferedReader br;
		Writer writer;
		String line;
		for(String app:apps){
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath+"/"+app+"/"+"EmmaCoverage.txt"), "utf-8"));
			for(int i=0; i<testSuiteSize; i++){
				File f = new File(inPath+"/"+app+"/"+app+"_"+i+"_coverage.txt");
				if(f.exists()){
					br = new BufferedReader(new FileReader(inPath+"/"+app+"/"+app+"_"+i+"_coverage.txt"));
					line = br.readLine();
					while(line != null){
						if(line.length() != 0 && Character.isDigit(line.charAt(0))){
							writer.write(i+" "+line.split("all classes")[0]+"\n");
							break;
						}
						line = br.readLine();
					}
					br.close();
				}		
			}
			writer.close();
		}
	}
	
	public static void lineCoverageExtractor() throws IOException{
		String[] apps = {"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
				"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
				"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
				"pro.oneredpixel.l9droid"};
		String path = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500";
		BufferedReader br;
		Writer writer;
		String line, coverage, tmp;
		for(String app:apps){
			br = new BufferedReader(new FileReader(path+"/"+app+"/"+"EmmaCoverage.txt"));
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path+"/"+app+"/"+"lineCoverage.txt"), "utf-8"));
			line = br.readLine();
			int count = 0;
			while(line != null){
				tmp = line.split("%")[line.split("%").length-2];
				coverage = tmp.split("!")[tmp.split("!").length-1];
				coverage = coverage.split("\t")[1];
				writer.write(count+" : "+coverage+"\n");
				line = br.readLine();
				count++;
			}
			br.close();
			writer.close();
		}
		
	}
}
