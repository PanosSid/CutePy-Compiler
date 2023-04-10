package symboltable.entities;

import java.util.Objects;

/**
 * Parameter as seen in a function call 
 * @author Panos
 *
 */
public class FormalParameter extends Entity {
	protected DataType datatype;
	protected ParameterMode mode;
	
	public FormalParameter(String name) {
		super(name);
		this.mode = ParameterMode.CV;
		this.datatype = DataType.INTEGER;
	}
	
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

	@Override
	public int hashCode() {
		return Objects.hash(datatype, mode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormalParameter other = (FormalParameter) obj;
		return datatype == other.datatype && mode == other.mode;
	}
	
		
}
