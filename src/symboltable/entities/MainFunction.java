package symboltable.entities;

public class MainFunction extends Function {

	public MainFunction(String name) {
		super(name);
	}

	public MainFunction(String name, int startingQuad, int frameLength) {
		super(name, startingQuad, frameLength);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Function other = (Function) obj;
		return framelength == other.framelength && startingQuad == other.startingQuad;
	}
}
