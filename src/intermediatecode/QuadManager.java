package intermediatecode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QuadManager {
	private int currentLabel;
	private int tempCounter;
	private String tempPrefix;
	private Map<Integer, Quad> intermedCodeMap; 

	public QuadManager() {
		currentLabel = 99;
		tempCounter = 0;
		intermedCodeMap = new LinkedHashMap<Integer, Quad>();
		tempPrefix = "T_";
	}
	
	public QuadManager(int startingLabel) {
		currentLabel = startingLabel;
		tempCounter = 0;
		intermedCodeMap = new LinkedHashMap<Integer, Quad>();
		tempPrefix = "&";
	}

	public Map<Integer, Quad> getIntermedCodeMap() {
		return intermedCodeMap;
	}

	public void setIntermedCodeMap(Map<Integer, Quad> intermidCodeMap) {
		this.intermedCodeMap = intermidCodeMap;
	}

	public void genQuad(String operator, String operand1, String operand2, String operand3) {
		currentLabel++;
		intermedCodeMap.put(currentLabel, new Quad(operator, operand1, operand2, operand3));
	}

	public int nextQuad() {
		return currentLabel + 1;
	}

	public String newTemp() {
		tempCounter++;
		return tempPrefix + tempCounter;
	}

	public List<Integer> emptyList() {
		return new ArrayList<Integer>();
	}

	public List<Integer> makeList(int label) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(label);
		return list;
	}

	public List<Integer> mergeList(List<Integer> list1, List<Integer> list2) {
		List<Integer> merged = new ArrayList<Integer>();
		merged.addAll(list1);
		merged.addAll(list2);
		return merged;
	}

	public void backpatch(List<Integer> list, int label) {
		for (Integer alabel : list) {
			Quad q = intermedCodeMap.get(alabel);
			q.setOperand3(label + "");
		}
	}

	public String getIntermediateCode() {
		String s = "";
		for (Integer label : intermedCodeMap.keySet()) {
			s += label + ": " + intermedCodeMap.get(label).toString() + "\n";
		}
		return s;
	}
	
	public boolean isValidForTransformationToC() {
		return false;
	}
	
	@Override
	public String toString() {
		return getIntermediateCode();
	}
}
