package symboltable.entities;

public abstract class Entity {
	
	protected String name;
	
	public Entity() {}
	
	public Entity(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
//	public boolean areEntitiesTheSame(Entity other) {
//		return (name.equals(other.name)) ? true : false;
//	}

}
