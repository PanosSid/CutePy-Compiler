package intermediatecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lex.CharTypes;

public class Quad {
	private String operator;
	private String operand1;
	private String operand2;
	private String operand3;

	public Quad(String operator, String operand1, String operand2, String operand3){
		this.operator = operator;
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.operand3 = operand3;
	}

	public Quad(String quadStr) {
		String fields[] = quadStr.split(",", 4);
		operator = fields[0].trim();
		operand1 = fields[1].trim();
		operand2 = fields[2].trim();
		operand3 = fields[3].trim();
	}

	public String getOperator() {
		return operator;
	}
	
	public String getOperand1() {
		return operand1;
	}

	public String getOperand2() {
		return operand2;
	}

	public String getOperand3() {
		return operand3;
	}

	public List<String> getOperandsThatAreVars() {
		List<String> varOperators = new ArrayList<String>();
		varOperators.addAll(Arrays.asList("+","-",":="));
		varOperators.addAll(CharTypes.MUL_OPS);
		varOperators.addAll(CharTypes.REL_OPS);		
		varOperators.add("par");		
		List<String> notVars = Arrays.asList("_", "ret", "cv" );
		List<String> varList = new ArrayList<String>();
		String operands[] = {operand1, operand2, operand3};
		for (int i = 0; i < operands.length; i++) {
			if (varOperators.contains(operator) &&
					(!isNumeric(operands[i]) && !notVars.contains(operands[i]))) {
				varList.add(operands[i]);
			}
		}
		return varList;	
	}
	
	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public void setOperand3(String operand3) {
		this.operand3 = operand3;
	}

	public String toString() {
		return operator + ", " + operand1 + ", " + operand2 + ", " + operand3;
	}
	
	

}