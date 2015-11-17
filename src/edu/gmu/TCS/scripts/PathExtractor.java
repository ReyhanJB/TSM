package edu.gmu.TCS.scripts;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class PathExtractor {
	
	public static void extractPaths() throws Exception{
		
		//"a2dp.Vol","acr.browser.barebones","anupam.acrylic",""at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		//"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		//"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		//"pro.oneredpixel.l9droid"
		String[] files = {"pro.oneredpixel.l9droid"};
		
		String inPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/100_500/"; //Path to the log file
		String outPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"; //Path to the output
		String tmpApp, tmp;
		String[] tmp1;
		String line = "";
		String outLine = "";
		boolean read = false;
		int tests = 100;
		
		for(String app:files){
			for(int i=0; i<tests; i++){
				BufferedReader br = new BufferedReader(new FileReader(inPath+"/"+app+"/"+app+"_"+i+"_log.txt"));
				FileWriter fwAllPaths = new FileWriter(outPath+"/"+app+"/"+app+"_"+i+".txt");
				FileWriter fwUniquePaths = new FileWriter(outPath+"/"+app+"/"+app+"_"+i+"_unique.txt");
				line = br.readLine();
				List<String> visited = new ArrayList<>();
				while (line != null){
					//System.out.println(line);
					//System.out.println(line.split(" ").length);
					if(line.split(" ").length > 4 && (line.split(" ")[5].equals("API") || line.split(" ")[4].equals("API") || line.split(" ")[3].equals("API") || line.split(" ")[2].equals("API"))){
						tmpApp = line.split(" ")[line.split(" ").length-1].split("\n")[0];
						if(app.equals(tmpApp)){
							if(line.split(" ").length == 13){
								tmp1 = line.split(" ")[5].split("\\(")[0].split("\\.");
								tmp = tmp1[tmp1.length-2]+"."+tmp1[tmp1.length-1];
								outLine = tmp+" -> ";
								fwAllPaths.write(tmp+" -> ");
								line = br.readLine();
								while(line != null && line.split(" ")[4].equals("Caller")){
									tmp1 = line.split(" ")[6].split("\\(")[0].split("\\.");
									tmp = tmp1[tmp1.length-2]+"."+tmp1[tmp1.length-1];
									outLine += tmp+" -> ";
									fwAllPaths.write(tmp+" -> ");
									line = br.readLine();
									read = true;
								}
							}
							else {
								if(line.split(" ").length == 12){
									tmp1 = line.split(" ")[4].split("\\(")[0].split("\\.");
									tmp = tmp1[tmp1.length-2]+"."+tmp1[tmp1.length-1];
									outLine = tmp+" -> ";
									fwAllPaths.write(tmp+" -> ");
									line = br.readLine();
									while(line != null && line.split(" ")[3].equals("Caller")){
										tmp1 = line.split(" ")[5].split("\\(")[0].split("\\.");
										tmp = tmp1[tmp1.length-2]+"."+tmp1[tmp1.length-1];
										outLine += tmp+" -> ";
										fwAllPaths.write(tmp+" -> ");
										line = br.readLine();
										read = true;
									}
								}
								else if (line.split(" ").length == 14){
									tmp1 = line.split(" ")[6].split("\\(")[0].split("\\.");
									tmp = tmp1[tmp1.length-2]+"."+tmp1[tmp1.length-1];
									outLine = tmp+" -> ";
									fwAllPaths.write(tmp+" -> ");
									line = br.readLine();
									while(line != null && line.split(" ")[5].equals("Caller")){
										tmp1 = line.split(" ")[7].split("\\(")[0].split("\\.");
										tmp = tmp1[tmp1.length-2]+"."+tmp1[tmp1.length-1];
										outLine += tmp+" -> ";
										fwAllPaths.write(tmp+" -> ");
										line = br.readLine();
										read = true;
									}
								}
							}
							outLine += "*"+"\n";
							fwAllPaths.write("*");
							fwAllPaths.write("\n");
							if(!visited.contains(outLine))
								visited.add(outLine);
						}
					}
					if(!read){
						line = br.readLine();
					}
					read = false;
				}
				Collections.sort(visited);
				for(String s: visited)
					fwUniquePaths.write(s);
				fwUniquePaths.close();
				fwAllPaths.close();
				br.close();
			}
		}

	}
	

	
	public static void rank() throws IOException{
		String inPath = "/Users/reyhanjb/Documents/Projects/GreenDroid/output/rankings/result.txt";
		String outPath = "/Users/reyhanjb/Documents/Projects/GreenDroid/output/rankings/ranking.txt";
		BufferedReader br = new BufferedReader(new FileReader(inPath));
		FileWriter fw = new FileWriter(outPath);
		String line;
		Double energy = 0.0;
		Integer freq = 0;
		HashMap<String, Double> listEnergy = new HashMap<>();
		HashMap<String, Integer> listFreq = new HashMap<>();
		line = br.readLine();
		
		while(line != null){
			String[] lineSplit = line.split(" ");
			if(lineSplit.length > 1){
				if(listEnergy.containsKey(lineSplit[0])){
					energy = listEnergy.get(lineSplit[0]);
					energy += Double.valueOf(lineSplit[2]);
					freq = listFreq.get(lineSplit[0])+1;
					listEnergy.remove(lineSplit[0]);
					listFreq.remove(lineSplit[0]);
				}
				else{
					energy = Double.valueOf(lineSplit[2]);
				}
				listEnergy.put(lineSplit[0], energy);
				listFreq.put(lineSplit[0], freq);
			}
			line = br.readLine();
		}
		br.close();
		
		String[] apps = new String[listEnergy.size()];
		double[] energies = new double[listEnergy.size()];
		int counter = 0;
		
		java.util.Iterator<String> it = listEnergy.keySet().iterator();
		while (it.hasNext()){
			apps[counter] = it.next();
			energies[counter] = listEnergy.get(apps[counter]);//(listEnergy.get(apps[counter]))/(listFreq.get(apps[counter]));
			//System.out.println(apps[counter]+" "+energies[counter]);
			counter++;
		}
		
		sort(energies,apps);
		for(int i=0; i<apps.length; i++)
			fw.write(apps[i]+" : "+energies[i]+"\n");
		fw.close();
		
	}
	
	public static void sort(double[] list, String[] files){
		for (int i = 0; i < list.length - 1; i++)
        {
            int index = i;
            for (int j = i + 1; j < list.length; j++)
                if (list[j] < list[index]) 
                    index = j;
      
            double smallerNumber = list[index];  
            list[index] = list[i];
            list[i] = smallerNumber;
            String smaller = files[index];  
            files[index] = files[i];
            files[i] = smaller;
            
        }
	}
	

	public static void listOfApps(){
		File[] files = new File("/Users/reyhanjb/Documents/Projects/GreenDroid/apps/Dictionary").listFiles();
		
		String s = "{";
		for (File file : files) {
		    if (file.isFile()) {
		        s += "\""+file.getName().split(".apk")[0]+"\""+",";
		    }
		}
		s = s.substring(0, s.length()-2);
		s = s.concat("\"};");
		System.out.println(s);
	}

}
