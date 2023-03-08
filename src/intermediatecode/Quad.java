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

	public void setOperand3(String operand3) {
		this.operand3 = operand3;
	}

	public String toString() {
		return operator + ", " + operand1 + ", " + operand2 + ", " + operand3;
	}
}