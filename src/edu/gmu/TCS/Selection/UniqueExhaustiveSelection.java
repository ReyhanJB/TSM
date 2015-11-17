package edu.gmu.TCS.Selection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UniqueExhaustiveSelection {

	public static void select(int total) throws IOException{
		String[] apps = {"a2dp.Vol"};
		for(String app:apps){
			System.out.println("\n"+app+"\n");
			List<Integer> coveredNodes = new ArrayList<>();
			String bitVectorPath = "/Users/reyhanjb/Documents/Projects/TCS/output/paths/100_500/"+app+"/new_bitVectors.txt";
			List<setType> subsets = new ArrayList<>();
			List<Integer> tests = new ArrayList<>();
			List<returnType> rts = UniqueGreedySelection.getVectors(app, "",bitVectorPath);
			
			for(int i=0; i<total; i++)
				tests.add(i);
			for(int i=0; i<rts.get(0).bitVector.size(); i++)
				coveredNodes.add(0);
			
			subsets = subsetSelection(rts);
			
			double max = subsets.get(0).energy;
			for(setType st:subsets){
				if(st.energy > max)
					max = st.energy;
			}
			for(setType st:subsets){
				if(st.energy == max)
					System.out.println(st.setlist);
			}
		}
	}
	
	public static List<setType> subsetSelection(List<returnType> set){
		List<List<returnType>> subsets = new ArrayList<>();
		List<setType> subsets1 = new ArrayList<>();
		for(int i=0; i<set.size(); i++){
			List<List<returnType>> subsetsTmp = new ArrayList<>(subsets);
			for(List<returnType> list:subsetsTmp){
				List<returnType> tmp = new ArrayList<returnType>(list);
				tmp.add(set.get(i));
				setType st = new setType(new ArrayList<>(tmp));
				if(st.effectiveTests == tmp.size()){
					subsets.add(tmp);
					subsets1.add(st);
				}
			}
			subsets.add(new ArrayList<>(Arrays.asList(set.get(i))));
		}
		return subsets1;
	}
}

class setType{
	
	int effectiveTests;
	List<returnType> setlist;
	Double energy;
	
	public setType(List<returnType> listI){
		setlist = new ArrayList<>();
		List<returnType> list = new ArrayList<>();
		List<Integer> coveredNodes = new ArrayList<>();
		effectiveTests = 0;
		energy = 0.0;
		
		for(int i=0; i< listI.size(); i++){
			returnType rt = new returnType(listI.get(i));
			setlist.add(rt);
			list.add(rt);
		}
		for(int i=0; i<list.get(0).bitVector.size(); i++)
			coveredNodes.add(0);
		
		returnType maxrt = findMaxrt(list);
		while(maxrt.energyTmp > 0.0){
			energy += maxrt.energyTmp;
			effectiveTests++;
			UniqueGreedySelection.recalculate(maxrt, list, coveredNodes);
			maxrt = findMaxrt(list);
			if(maxrt == null)
				break;
		}
	}
	
	private returnType findMaxrt(List<returnType> rts){
		if(rts.size() == 0)
			return null;
		returnType max = rts.get(0);
		for(int i=1; i<rts.size(); i++){
			if(rts.get(i).energyTmp > max.energyTmp)
				max = rts.get(i);
		}
		return max;
	}
}