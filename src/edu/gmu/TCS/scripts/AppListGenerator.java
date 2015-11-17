package edu.gmu.TCS.scripts;

import java.io.File;

public class AppListGenerator {

	public static void listGenerator(){
		
		String path = "/Users/reyhanjb/Documents/Projects/TCS/apps/Fdroid/APKs";
		StringBuffer output = new StringBuffer();
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		int count = 0;
		output.append("[");
		for(File f: listOfFiles){
			count++;
			if( count >= 20){
				String s = f.getName();
				s = s.split(".apk")[0];
				output.append("\""+s+"\", ");
			}
		}
		output.append("]");
		System.out.println(output);
	}
}
