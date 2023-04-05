package symboltable;

import java.util.Stack;

import exceptions.CutePyException;
import symboltable.entities.Entity;

public class SymbolTable {
	private Stack<Scope> symbolTable;
	
	
	public void addScope() {
		symbolTable.push(new Scope());
	}
	
	public void removeScope() {
		symbolTable.pop();
	}
	
	public void addEntity(Entity entity) {
		symbolTable.lastElement().addEntity(entity);
	}
	
	public Entity findEntity(String entityName) throws CutePyException {
		for (int i = symbolTable.size() - 1; i >= 0; i--) {
			Scope scope = symbolTable.get(i);
			Entity foundEntity = scope.findEntity(entityName);  
			if (foundEntity != null) {
				return foundEntity; 
			}
		}
		throw new CutePyException("Entity '"+entityName+"' not found in symbol table.");
	}
	
	public void updateFields() {
		// .....
	}
	
	public void addFormalParam(String name, String mode) {
		
	}
	
	public void addFunction() {
		addScope();
//		addEntity();
	}
	
	
	
}
