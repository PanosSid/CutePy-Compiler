package symboltable.entities;
/**
 * Parameter as seen from inside a function
 * @author Panos
 * 
 */
public class Parameter extends FormalParameter {
	private int offset;
	
	public Parameter(String name, ParameterMode mode) {
		super(name, mode);
		// TODO Auto-generated constructor stub
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	

}
