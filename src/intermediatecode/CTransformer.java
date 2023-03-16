package intermediatecode;

import java.util.Map;

public class CTransformer {
	private String cCode;
	
	public CTransformer() {}
	
	public void transformIntermidateCodeToC(Map<String, Quad> intermedCode) {
		addToCCode("//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n", 0);
		addToCCode("#include <stdio.h>", 0);
		addToCCode("//int main(){", 0);
		addToCCode(getDeclerationOfVars(intermedCode), 1);
		for (String label : intermedCode.keySet()) {
			Quad quad = intermedCode.get(label);
			String operator = quad.getOperator();
			addToCCode(getEquivalentCCode(quad), 1);
		}
		addToCCode("//}", 0);
	}
	
	private String getDeclerationOfVars(Map<String, Quad> intermedCode) {
		String declareVars = "int ";
		for (String label : intermedCode.keySet()) {
			Quad quad = intermedCode.get(label);
			declareVars += quad.getOperandsThatAreVars();
		}
		return declareVars;
	}

	private String getEquivalentCCode(Quad quad) {
		if (quad.getOperator().equals(":=")) {
			return quad.getOperand3() + " = " + quad.getOperand1()+ ";";
		} 

		return null;
	}

	private void addToCCode(String s, int tabs) {
		cCode += s + "\n";
	}
}
