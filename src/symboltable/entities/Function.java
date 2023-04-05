package symboltable.entities;

public abstract class Function extends Entity {
	protected int startingQuad;
	protected int framelength;
	
	public Function(String name) {
		super(name);
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
	
	
}
