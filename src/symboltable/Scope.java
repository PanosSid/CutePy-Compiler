package symboltable;

import java.util.LinkedList;

import symboltable.entities.Entity;

public class Scope {
	private LinkedList<Entity> entityList;
		
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
	
	public int getOffsetOfNextEntity() {
		return 12 + (entityList.size()+1)*4;
	}
}
