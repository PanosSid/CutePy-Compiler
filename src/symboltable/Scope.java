package symboltable;

import java.util.LinkedList;
import java.util.Objects;

import symboltable.entities.Entity;
import symboltable.entities.Function;


public class Scope {
	private LinkedList<Entity> entityList = new LinkedList<Entity>();
	private Function scopeFunc; 	// used as a reference to update the fields of the func
	
	public Scope(Function scopeFunc) {
		super();
		this.scopeFunc = scopeFunc;
	}

	public Scope(Entity... entities) {
		entityList = new LinkedList<Entity>();
		for (int i = 0; i < entities.length; i++) {
			entityList.add(entities[i]);
		}
	}
	
	public void addEntity(Entity entity) {
		// check if an entity with the same name already exists???
		entityList.add(entity);
		
	}

	public Entity findEntity(String entityName) {
		for (Entity entity : entityList) {
			if (entity.getName().equals(entityName)) {
				return entity;
			}
		}
		return null;
	}
	
	
	public int getLengthOfScope() {
		return 12 + entityList.size()*4;
	}
	
	public void updateStartingQuadOfFunc(int startingQuad) {
		scopeFunc.setStartingQuad(startingQuad);
	}
	
	public void updateFrameLengthOfFunc() {
		scopeFunc.setFramelength(getLengthOfScope());
	}
	

	@Override
	public String toString() {
		String s = "";
		for (Entity entity : entityList) {
			s += entity + "---";
		}
		return s;
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scope other = (Scope) obj;
		return Objects.equals(entityList, other.entityList);
	}

	
	
}
