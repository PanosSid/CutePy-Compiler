package symboltable.entities;

public abstract class Function extends Entity {
	protected int startingQuad;
	protected int framelength;
	
	public Function(String name) {
		super(name);
	}
	
	public Function(String name, int startingQuad, int framelength) {
		super(name);
		this.startingQuad = startingQuad;
		this.framelength = framelength;
	}

	public int getStartingQuad() {
		return startingQuad;
	}

	public void setStartingQuad(int startingQuad) {
		this.startingQuad = startingQuad;
	}

	public int getFramelength() {
		return framelength;
	}

	public void setFramelength(int framelength) {
		this.framelength = framelength;
	}

	@Override
	public String toString() {
		return "[Function: "+super.name+", SQ=" + startingQuad + ", FL=" + framelength + "]";
	}
	
	
	
	
}
