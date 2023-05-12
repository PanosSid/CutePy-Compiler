package symboltable.entities;

import java.util.Objects;

/**
 * Parameter as seen from inside a function
 * @author Panos
 * 
 */
public class Parameter extends FormalParameter implements EntityWithOffset{
	private int offset;
	
	public Parameter(String name, ParameterMode mode) {
		super(name, mode);
	}
	
	public Parameter(String name, int offset) {
		super(name, ParameterMode.CV);
		this.offset = offset;
	}
	
	public Parameter(String name, int offset, ParameterMode mode) {
		super(name, mode);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
//	@Override
//	public boolean areEntitiesTheSame(Entity other) {
//		if (other.getClass().equals(FormalParameter.class) 
//				&& super.areEntitiesTheSame(other)) {
//			return false;
//		}
//		return super.areEntitiesTheSame(other);
//	}

	@Override
	public String toString() {
		return "[Parameter: "+super.name+"/"+offset+"/"+super.mode+"]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(offset);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		return offset == other.offset;
	}
}
