//In current implementation, the program looks at the log file recorded during running of each application. There is trace of API calls from an app which has run before, in the log file of the other apps. 
//For the future, it should be considered as well. In other words, it might be better to have just one log file for a period of time, and then read that log file an grasp all the API calls of the apps.

package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class DScoreCalculator {
	
	public static void dscoreCalculator() throws Exception{
		//For calculating DScore
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		String[] files = {"fr.asterope"};
		
		ArrayList<String> apps = new ArrayList<>();
		for (String file : files)
			apps.add(file);
		//int paths = 10;
		int tests = 100;
		String inPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/100_500/";
		String outPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/";

		String tmpApp;
		int index;
		double energy;
		double[] energyList = new double[files.length];
		for(int j=0; j<files.length; j++){
			String app = files[j];
			FileWriter fw = new FileWriter(outPath+app+"/Dscore.txt");
			fw.write(app+"\n");
			for(int i=0; i<tests; i++){
				energyList[j] = 0;
				BufferedReader br = new BufferedReader(new FileReader(inPath+app+"/"+app+"_"+i+"_log.txt"));
				String line = br.readLine();
				while (line != null){
					if(line.split(" ").length > 4 && (line.split(" ")[5].equals("API") || line.split(" ")[4].equals("API") || line.split(" ")[3].equals("API") || line.split(" ")[2].equals("API"))){
						tmpApp = line.split(" ")[line.split(" ").length-1].split("\n")[0];
						if(app.equals(tmpApp)){
							energy = Double.valueOf(line.split(" ")[line.split(" ").length-5]);
							index = apps.indexOf(app);
							energyList[index] += energy;
						}
					}
					line = br.readLine();
				}
				br.close();
				fw.write("\t"+i+" : "+energyList[j]+"\n");
			}
			fw.write("*****************************************************\n");
			fw.close();
		}
	}
}
