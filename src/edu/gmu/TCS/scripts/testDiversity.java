package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class testDiversity {

	public static void measure() throws IOException{
		
		/*"a2dp.Vol","acr.browser.barebones","anupam.acrylic","at.univie.sensorium","com.andrew.apollo","com.github.cetoolbox",
		"com.maxfierke.sandwichroulette","com.nolanlawson.apptracker","com.teleca.jamendo","com.templaro.opsiz.aka",
		"de.hechler.andfish","fr.asterope","net.sourceforge.opencamera","org.blockinger.game","org.quovadit.apps.andof",
		"pro.oneredpixel.l9droid"*/
		String[] apps = {"a2dp.Vol"};
		String path = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/";
		for(String app: apps){
			System.out.println(app);
			Map<String,List<Integer>> map = new HashMap<>();
			//String bitVectorPath = path+app+"/bitVectors.txt";
			String bitVectorPath = path+app+"/bitVectors.txt";
			List<String> rts = getVectors(app, bitVectorPath);
			for(int i=0; i<rts.size(); i++){
				if(map.containsKey(rts.get(i)))
					map.get(rts.get(i)).add(i);
				else{
					List<Integer> tmp = new ArrayList<>();
					tmp.add(i);
					map.put(rts.get(i), tmp);
				}
			}
			System.out.println(map.keySet().size());
			Iterator<String> it = map.keySet().iterator();
			while(it.hasNext())
				System.out.println(map.get(it.next()).toString());
		}
	}
	
	public static List<String> getVectors(String app, String bitVectorPath) throws IOException{
		
		String bitVector = "";
		//String eVector = "";
		List<String> result = new ArrayList<>();
		
		BufferedReader bf = new BufferedReader(new FileReader(bitVectorPath));
		String line = bf.readLine();
		while(line != null){
			bitVector = line.split("\n")[0];
			line = bf.readLine();
			//eVector = line.split("\n")[0];
			line = bf.readLine();
			result.add(bitVector);
		}
		bf.close();
		
		return result;
	}
}
