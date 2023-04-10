package symboltable;

import java.util.List;
import java.util.Stack;

import exceptions.CutePyException;
import symboltable.entities.Entity;
import symboltable.entities.Function;
import symboltable.entities.MainFunction;
import symboltable.entities.Parameter;

public class SymbolTable {
	protected Stack<Scope> symbolTable = new Stack<Scope>();
	
	public SymbolTable() {
		symbolTable = new Stack<Scope>();
		symbolTable.add(new Scope());
		addEntity(new MainFunction("main"));
	}
	
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
	
	public void updateFields(String entityName, int uptValue) {
//		Entity
		
	}
	
	public void addFormalParam(String name, String mode) {
		
	}
	
	public void addFunction(Function func) {
		addEntity(func);
		addScope();
	}
	
	public void addLocalFunction(Function func, List<Parameter> params) {
		addEntity(func);
		addScope();
		for (Parameter p : params) {
			addEntity(p);
		}
	}
		
	public int getOffsetOfNextEntity() {
		return symbolTable.lastElement().getLengthOfScope();
	}

	@Override
	public String toString() {
		String s = "SymbolTable\n";
		for (int i = symbolTable.size() - 1; i >= 0; i--) {
			s += "Scope "+ i + " "+symbolTable.get(i) +"\n";
		}
		return s;
	}
	
	
	
}
