package symboltable.entities;

import exceptions.CutePyException;

public abstract class Entity {
	protected String name;
	private int foundScope = -1;
	
	public Entity() {}
	
	public Entity(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getFoundScope() throws CutePyException {
		if (foundScope == -1) {
			throw new CutePyException("Found scope of entity: ["+name+"] is -1");
		}
		return foundScope;
	}
	
	public void setFoundScope(int foundScope) {
		this.foundScope = foundScope;
	}
	

}
