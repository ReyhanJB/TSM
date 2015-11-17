package edu.gmu.TCS.scripts;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import edu.gmu.TCS.Selection.GreedySelection;
import edu.gmu.TCS.Selection.RandomSelection;
import edu.gmu.TCS.Selection.UniqueExhaustiveSelection;
import edu.gmu.TCS.Selection.UniqueGreedySelection;


public class main {

	public static void main(String[] args) throws Exception{
		
		//PathExtractor.extractPaths();
		//EmmaCoverageExtractor.coverageExtractor();
		//EmmaCoverageExtractor.lineCoverageExtractor();
		//DScoreCalculator.dscoreCalculator();
		//GreenDroid
		//addDiversity.add();
		//addDiversity.addAnthropy();
		//eCoverageCalculator.calculate();
		
		//RandomSelection.select(99,100);
		//RandomSelection.cummulativeRandom(100);
		
		//For greedy line coverage
		//GreedySelection.simpleSelect(100);//Input argument is the number of test cases
		//cumulativeCoveredNodeCalculator.getCoverage();
		
		//UniqueGreedySelection.select();
		ILPInputGenerator.generate();
		//NewILPInputGenerator.generate();
		
		//Others
		//copyTests.copy();
		//testDiversity.measure();
		//testGenerator.generate();
		
		//Previously used, but ot any more
		//UniqueExhaustiveSelection.select(100);
		
		//EDTSO
		//EDTSO.generate();
	}
	
}
