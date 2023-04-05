package symboltable.entities;

public class Variable extends Entity {
	
	protected DataType datatype;
	protected int offset;

	public Variable(String name) {
		super(name);
		this.datatype = DataType.INTEGER;

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
	
	
	
	
}
