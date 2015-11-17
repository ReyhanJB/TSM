package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ParseReviews {
	public static void parseReviews() throws IOException{
		
		HashMap<String,ArrayList<String>> Appreviews = new HashMap<String, ArrayList<String>>();
		String path = "/Users/reyhanjb/Documents/Projects/GreenDroid/reviews/crawlingResults/";
		String outPath = "/Users/reyhanjb/Documents/Projects/GreenDroid/reviews/reviews/";
		String outPath1 = "/Users/reyhanjb/Documents/Projects/GreenDroid/reviews/relatedReviews/";
		String outPath2 = "/Users/reyhanjb/Documents/Projects/GreenDroid/reviews/reviewedApp.txt";
		File folder = new File(path);
		String fname = "";
		String line = "";
		File[] files = folder.listFiles();
		int count = 0;
		double rating = 0;
		int realCount = 0;
		double appReviewPercent = 0;
		int allApps = 0;
		double averageStars = 0;
		double allStars = 0;
		int appCount = 0;
		FileWriter fw2 = new FileWriter(outPath2);
		DecimalFormat df = new DecimalFormat("#.##");

		for(int i=0; i<files.length; i++){
			count = 0;
			realCount = 0;
			rating = 0;
			if(files[i].isFile()){
				ArrayList<String> reviews = new ArrayList<String>();
				BufferedReader br = new BufferedReader(new FileReader(path+files[i].getName()));
				FileWriter fw = new FileWriter(outPath+files[i].getName());
				FileWriter fw1 = new FileWriter(outPath1+files[i].getName());
				fname = files[i].getName().split(".txt")[0];
				line = br.readLine();
				while(line != null){
					if(line.contains("comment:")){
						count++;
						line = line.split("\"")[1];
						if(line.contains("battery") || line.contains("batteries") || line.contains("drain") || line.contains("power") || line.contains("energy") || line.contains("drains") || line.contains("consumes") || line.contains("consume")
								|| line.contains("consumed") || line.contains("drained"))
						{
							realCount++;
							fw1.write(line+"\n");
							fw1.write("******************************************\n");
						}
						reviews.add(line);
						fw.write(line+"\n");
					}
					if(line.contains("starRating:")){
						rating += Double.parseDouble(line.split(":")[1].split(" ")[1]);
					}
					line = br.readLine();
				}
				double ratio = (realCount*100)/count;
				if(ratio != 0.0){
					fw2.write(fname+"\n");
					fw2.write("Rating: "+df.format(rating/count)+"\n");
					fw2.write("Energy related reviews: "+(int) ratio+"%\n");
					fw2.write("-------------------------------------------\n");
					appReviewPercent += ratio;
					appCount++;
					averageStars += rating/count;
				}
				br.close();
				fw.close();
				fw1.close();
				Appreviews.put(fname, reviews);
				allStars += rating/count;
				allApps++;
			}
		}
		fw2.write("Number of studied Apps: "+allApps+"\n");
		fw2.write("Number of apps that contain at least one review related to the energy issue: "+appCount+"\n");
		fw2.write("Average percentage of reviews related to energy consumption of Apps is: "+df.format(appReviewPercent/allApps)+"%\n");
		fw2.write("The average rating of Apps that contain at least one review related to the energy issue: "+df.format(averageStars/appCount)+"\n");
		fw2.write("The average rating of all Apps: "+df.format(allStars/allApps)+"\n");
		fw2.close();
	}
	
}
