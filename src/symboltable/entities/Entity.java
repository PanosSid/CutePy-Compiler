package symboltable.entities;

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
	
	public int getFoundScope() {
		return foundScope;
	}
	
	public void setFoundScope(int foundScope) {
		this.foundScope = foundScope;
	}
	

}
