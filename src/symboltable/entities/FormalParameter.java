package symboltable.entities;

/**
 * Parameter as seen in a function call 
 * @author Panos
 *
 */
public class FormalParameter extends Entity {
	protected DataType datatype;
	protected ParameterMode mode;
	
	public FormalParameter(String name, ParameterMode mode) {
		super(name);
		this.mode = mode;
		this.datatype = DataType.INTEGER;
	}

	public ParameterMode getMode() {
		return mode;
	}

	public void setMode(ParameterMode mode) {
		this.mode = mode;
	}
	
		
}
