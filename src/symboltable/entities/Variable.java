package symboltable.entities;

import java.util.Objects;

public class Variable extends Entity {
	protected DataType datatype;
	protected int offset;

	public Variable(String name) {
		super(name);
		this.datatype = DataType.INTEGER;

	}

	public Variable(String name, int offset) {
		super(name);
		this.datatype = DataType.INTEGER;
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public DataType getDatatype() {
		return datatype;
	}

	@Override
	public String toString() {
		return "[Variable: "+super.name+"/"+ datatype + "/"+ offset+"]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(datatype, offset);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable other = (Variable) obj;
		return datatype == other.datatype && offset == other.offset;
	}
	
	

}
