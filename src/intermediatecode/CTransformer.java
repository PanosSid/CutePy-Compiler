package intermediatecode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import lex.CharTypes;

public class CTransformer {
	private String cCode = "";
	private Queue<String> funcParams = new LinkedList<String>(); 
	
	public CTransformer() {}
	
	public String getCcode() {
		return cCode;
	}
	
	public void transformIntermidateCodeToC(Map<Integer, Quad> intermedCode) {
		addToCCode("//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n", 0);
		addToCCode("#include <stdio.h>\n", 0);
		addToCCode("int main(){", 0);
		addToCCode(getDeclerationOfVars(intermedCode), 1);
		for (Integer label : intermedCode.keySet()) {
			Quad quad = intermedCode.get(label);
			addToCCode(getEquivalentCCode(label, quad), 1);
		}
		addToCCode("}", 0);
	}
	
	public String getDeclerationOfVars(Map<Integer, Quad> intermedCode) {
		List<String> varsList = new ArrayList<String>();
		for (Integer label : intermedCode.keySet()) {
			Quad quad = intermedCode.get(label);
			List<String> quadVars = quad.getOperandsThatAreVars();
			for (String var: quadVars) {
				if (!varsList.contains(var)) {
					varsList.add(var);
				}
			} 
		}
		String declareVars = "int ";
		for (String variable : varsList) {
			declareVars += variable+", ";
		}
		return declareVars.substring(0, declareVars.length()-2)+";";
	}

	private String getEquivalentCCode(Integer label, Quad quad) {
		String operator = quad.getOperator();
		String cLine = "";
		if (operator.equals("begin_block")) {
			// do nothing
		} else if (operator.equals("end_block")) {
			// do nothing
		} else if (operator.equals(":=")) {
			cLine = quad.getOperand3() + " = " + quad.getOperand1()+ ";";			
		} else if (CharTypes.ADD_OPS.contains(operator.charAt(0)) || CharTypes.MUL_OPS.contains(operator)) {
			cLine = quad.getOperand3() + " = " + quad.getOperand1() + " " + operator + " " + quad.getOperand2() + ";";
		} else if (CharTypes.REL_OPS.contains(operator)) {
			cLine = "if (" + quad.getOperand1() + " " + operator + " " + quad.getOperand2() +") goto L_" + quad.getOperand3()+ ";"; 
		} else if (operator.equals("jump")) {
			cLine = "goto L_"+ quad.getOperand3()+ ";";
		} else if (operator.equals("out")) {
			cLine = "printf("+quad.getOperand3()+");";
		} else if (operator.equals("in")) {
			cLine = "scanf(%d, "+quad.getOperand1()+");";
		} else if (operator.equals("ret")) {
			cLine = "return "+quad.getOperand1()+";";
		} else if (operator.equals("par")) {
//			cLine = "par "+quad.getOperand1()+";";
			if (quad.getOperand2().equals("cv") || quad.getOperand2().equals("ret")) {
				funcParams.add(quad.getOperand1());
			}
		} else if (operator.equals("call")) {
			if (funcParams.isEmpty()) {
				cLine = quad.getOperand1()+"()"+";";				
			} else {
				String tmp = quad.getOperand1()+"(";
				while (funcParams.size() > 1) {
					tmp += funcParams.remove()+", ";
				}
				tmp = tmp.substring(0, tmp.length()-2) + ");";
				cLine = funcParams.remove() + " = "+ tmp;
			}
//		else if (operator.equals("halt")) {
		} else {
			return null;
		}
		return getLblC(label) + cLine;
	}
	
	private String getLblC(Integer label) {
		return "L_"+label+": ";
	}
	
	private void addToCCode(String s, int tabs) {
		for (int i = 0; i < tabs; i++) {
			cCode += "\t";
		}
		cCode += s + "\n";
	}
}
