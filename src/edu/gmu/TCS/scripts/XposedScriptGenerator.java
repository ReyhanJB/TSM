package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;

public class XposedScriptGenerator {
	
	public static void XposedMethodGenerator() throws Exception{
		
		HashMap<String,Double> APIEnergy = new HashMap<String, Double>();
		HashMap<String,Integer> APIInstance = new HashMap<String, Integer>();
		HashMap<String, String> APIparams= new HashMap<String, String>();
		HashMap<String, String> APItype= new HashMap<String, String>();
		String APIsPath = "/Users/reyhanjb/Documents/Projects/GreenDroid/resources/APIs.txt";
		String energyPath = "/Users/reyhanjb/Documents/Projects/GreenDroid/resources/energy.txt";
		String instancePath = "/Users/reyhanjb/Documents/Projects/GreenDroid/resources/instance.txt";
		String outPath = "/Users/reyhanjb/Documents/Projects/GreenDroid/resources/Xposed.txt";
		String API, APImethod, method, params, APIline, type;
		Double energy;
		int instance;

		BufferedReader br = new BufferedReader(new FileReader(APIsPath));
		BufferedReader br1 = new BufferedReader(new FileReader(energyPath));
		BufferedReader br2 = new BufferedReader(new FileReader(instancePath));
		FileWriter fw = new FileWriter(outPath);
		APIline = br.readLine();
		
		while (APIline !=  null) {
			params = "";
			energy = Double.parseDouble(br1.readLine());
			instance = Integer.parseInt(br2.readLine());
			String tmp = APIline.split("\\(")[0];
			APImethod = tmp.split("\\.")[tmp.split("\\.").length-1];
			API = tmp.substring(0, tmp.length()-APImethod.length()-1);
			
			if(!API.split("\\.")[API.split("\\.").length-1].toLowerCase().equals(APImethod.toLowerCase())){
				type = "Method";
			}
			else{
				type = "Constructor";
			}
			
			String tmp1 = APIline.split("\\(")[1].split("\\)")[0];
			if(!tmp1.equals("")){
				String[] tmp2 = tmp1.split("#");
				for(int i=0; i<tmp2.length; i++)
					params += tmp2[i]+".class, ";
			}
			
			if(APIEnergy.containsKey(API+"*"+APImethod)){
				double etemp = APIEnergy.get(API+"*"+APImethod);
				int itemp = APIInstance.get(API+"*"+APImethod);
				energy = (((energy*instance)+(etemp*itemp))/(itemp+instance));
				instance += itemp;
				APIEnergy.remove(API+"*"+APImethod);
				APIInstance.remove(API+"*"+APImethod);
				APIparams.remove(API+"*"+APImethod);
				APItype.remove(API+"*"+APImethod);
			}
			
			APIEnergy.put(API+"*"+APImethod, energy);
			APIInstance.put(API+"*"+APImethod, instance);
			APIparams.put(API+"*"+APImethod, params);
			APItype.put(API+"*"+APImethod, type);
			APIline = br.readLine();
		}
		br.close();
		br1.close();
		br2.close();

		java.util.Iterator<String> it = APIEnergy.keySet().iterator();
		while (it.hasNext()){
			String tmp = it.next();
			APImethod = tmp.split("\\*")[1];
			API = tmp.split("\\*")[0];
			energy = APIEnergy.get(tmp);
			params = APIparams.get(tmp);
			type = APItype.get(tmp);
			if(type.equals("Method")){
				method = "XposedHelpers.findAndHookMethod(\""+API+"\", lpparam.classLoader, \""+APImethod+"\", "+params+"new XC_MethodHook() { \n "
						+ "\t@Override\n"
						+ "\tprotected void afterHookedMethod(MethodHookParam param) throws Throwable {\n"
						+ "\t\tStackTraceElement[] stes = Thread.currentThread().getStackTrace();\n"
						+ "\t\tif(stes[5].toString().contains(lpparam.packageName)){\n"
						+ "\t\t\tXposedBridge.log(\"API "+API+"."+APImethod+"() with energy "+energy+" called in app: \" + lpparam.packageName);\n"
				 		+ "\t\t\tfor(StackTraceElement ste:stes){\n"
						+ "\t\t\t\tif(ste.toString().contains(lpparam.packageName))\n"
				 		+ "\t\t\t\t\tXposedBridge.log(\"Caller Method: \"+ste.toString()+\"\");\n"
						+ "\t\t\t}\n"
				 		+ "\t\t}\n"
						+ "\t}\n"
						+ "});";
				fw.write(method+"\n");
			}
			else{
				method = "XposedHelpers.findAndHookConstructor(\""+API+"\", lpparam.classLoader, "+params+"new XC_MethodHook() { \n "
						+ "\t@Override\n"
						+ "\tprotected void afterHookedMethod(MethodHookParam param) throws Throwable {\n"
						+ "\t\tStackTraceElement[] stes = Thread.currentThread().getStackTrace();\n"
						+ "\t\tif(stes[5].toString().contains(lpparam.packageName)){\n"
						+ "\t\tXposedBridge.log(\"API "+API+"."+APImethod+"() with energy "+energy+" called in app: \" + lpparam.packageName);\n"
				 		+ "\t\t\tfor(StackTraceElement ste:stes){\n"
						+ "\t\t\t\tif(ste.toString().contains(lpparam.packageName))\n"
				 		+ "\t\t\t\t\tXposedBridge.log(\"Caller Method: \"+ste.toString()+\"\");\n"
						+ "\t\t\t}\n"
				 		+ "\t\t}\n"
						+ "\t}\n"
						+ "});";
				fw.write(method+"\n");
			}
		}
		fw.close();
	}
}
