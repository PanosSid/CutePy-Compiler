package symboltable.entities;

public class TemporaryVariable extends Variable {

	public TemporaryVariable(String name) {
		super(name);
	}

	public TemporaryVariable(String name, int offset) {
		super(name, offset);
	}

	@Override
	public String toString() {
		return "[TempVariable: "+super.name+"/"+ datatype + "/"+ offset+"]";
	}

}
