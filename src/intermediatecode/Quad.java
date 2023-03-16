package intermediatecode;

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

	public String getOperandsThatAreVars() {
		String operands[] = {operand1, operand2, operand3};
		String vars = "";
		for (int i = 0; i < operands.length; i++) {
			if (!isNumeric(operands[i])) {
				vars += operands[i]+", ";
			}
		}
		return operand1;
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