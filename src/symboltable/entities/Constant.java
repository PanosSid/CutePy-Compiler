package symboltable.entities;

public class Constant extends Entity {
	private DataType datatype;
	private int value;
	
	public Constant(String name, int value) {
		super(name);
		this.datatype = DataType.INTEGER;
		this.value = value;
	}

	public int getValue() {
		return value;
	}
	
	
}
