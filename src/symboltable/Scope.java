package symboltable;

import java.util.LinkedList;
import java.util.Objects;

import exceptions.CutePyException;
import symboltable.entities.Entity;
import symboltable.entities.Function;


public class Scope {
	private LinkedList<Entity> entityList = new LinkedList<Entity>();
	private Function scopeFunc; 	// used as a reference to update the fields of the func
	
	public Scope() {
		
	}
	
	public Scope(Function scopeFunc) {
		super();
		this.scopeFunc = scopeFunc;
//		entityList.add(scopeFunc);
	}

	public Scope(Entity... entities) {
		for (int i = 0; i < entities.length; i++) {
			entityList.add(entities[i]);
		}
	}
	
	public void addEntity(Entity entity) throws CutePyException {
		if (findEntity(entity.getName()) != null) {
			throw new CutePyException("Duplicate entity with name " + entity.getName());
			// prosoxi prepei na diaxorisoyme tin addParameter kai tin addEnitty?
		}
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
		return 12 + entityList.size()*4 - countFunctionsInsideScope()*4;
	}
	
	public void updateStartingQuadOfFunc(int startingQuad) {
		scopeFunc.setStartingQuad(startingQuad);
	}
	
	public void updateFrameLengthOfFunc() {
		scopeFunc.setFramelength(getLengthOfScope());
	}
	
	private int countFunctionsInsideScope() {
		int count = 0;
		for (Entity entity : entityList) {
		    if (entity instanceof Function) {
		        count++;
		    }
		}
		return count;
	}

	@Override
	public String toString() {
		String s = "";
		for (Entity entity : entityList) {
			s += entity + " --- ";
		}
		return (s.length()-3 >0) ? s.substring(0, s.length()-3) : s;
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
