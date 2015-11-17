package edu.gmu.TCS.scripts;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class copyTests {

	public static void copy() throws IOException{
		String app = "net.sourceforge.opencamera";
		String srcPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/dump/";
		String dstPath = "/Users/reyhanjb/Documents/Projects/TCS/input/logs_testcases/100_500/"+app+"/";
		int srcTests = 3;
		int dstStart = 97;
		for(int i=0; i<=srcTests; i++){
			File src1 = new File(srcPath+app+"_"+i+"_coverage.html");
			File src2 = new File(srcPath+app+"_"+i+"_coverage.txt");
			File src3 = new File(srcPath+app+"_"+i+"_coverage.xml");
			File src4 = new File(srcPath+app+"_"+i+"_log.txt");
			File dst1 = new File(dstPath+app+"_"+(i+dstStart)+"_coverage.html");
			File dst2 = new File(dstPath+app+"_"+(i+dstStart)+"_coverage.txt");
			File dst3 = new File(dstPath+app+"_"+(i+dstStart)+"_coverage.xml");
			File dst4 = new File(dstPath+app+"_"+(i+dstStart)+"_log.txt");
			FileUtils.copyFile(src1, dst1, true);
			FileUtils.copyFile(src2, dst2, true);
			FileUtils.copyFile(src3, dst3, true);
			FileUtils.copyFile(src4, dst4, true);
		}
		
	}
}
