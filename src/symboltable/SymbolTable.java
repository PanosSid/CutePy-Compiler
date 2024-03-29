package symboltable;

import java.util.List;
import java.util.Stack;

import exceptions.CutePyException;
import symboltable.entities.Entity;
import symboltable.entities.Function;
import symboltable.entities.Parameter;

public class SymbolTable {
	protected Stack<Scope> scopeStack = new Stack<Scope>();
	private String symbolTableHistory = "";
	
	public SymbolTable() {
		scopeStack = new Stack<Scope>();
		scopeStack.add(new Scope());
	}
	
	public SymbolTable(Stack<Scope> scopeStack) {
		super();
		this.scopeStack = scopeStack;
	}
	
	public void setScopeStack(Stack<Scope> scopeStack) {
		this.scopeStack = scopeStack;
	}
	public String getSymbolTableHistory() {
		return symbolTableHistory;
	}
	
	public void addScope(Function func) {
		scopeStack.push(new Scope(func));
	}
	
	public void removeScope() {
		symbolTableHistory += this+"\n\n";
		scopeStack.pop();
	}
	
	public void addEntity(Entity entity) throws CutePyException {
		scopeStack.lastElement().addEntity(entity);
	}
	
	public Entity searchEntity(String entityName) throws CutePyException {
		for (int i = scopeStack.size() - 1; i >= 0; i--) {
			Scope scope = scopeStack.get(i);
			Entity foundEntity = scope.findEntity(entityName);  
			if (foundEntity != null) {
				foundEntity.setFoundScope(i);
				return foundEntity; 
			}
		}
		throw new CutePyException("Entity '"+entityName+"' not found in symbol table.");
	}
	
	public void updateStartingQuadField(int uptValue) {
		scopeStack.lastElement().updateStartingQuadOfFunc(uptValue);
	}
	
	public void updateFrameLengthField() {
		scopeStack.lastElement().updateFrameLengthOfFunc();
	}
	
	public void addFormalParam(String name, String mode) {
		
	}
	
	public void addFunction(Function func) throws CutePyException {
		addEntity(func);
		addScope(func);
	}
	
	public void addLocalFunction(Function func, List<Parameter> params) throws CutePyException {
		addEntity(func);
		addScope(func);
		for (Parameter p : params) {
			addEntity(p);
		}
	}
		
	public int getOffsetOfNextEntity() {
		return scopeStack.lastElement().getLengthOfScope();
	}
	
	public int getLastScopeNum() {
		return scopeStack.size()-1;
	}
	

	@Override
	public String toString() {
		String s = "SymbolTable\n";
		for (int i = scopeStack.size() - 1; i > 0; i--) {
			s += "Scope "+ i + " "+scopeStack.get(i) +"\n";
			s += "   | \n";
		}
		
		s += "Scope "+ 0 + " "+scopeStack.get(0) +"\n";
		return s;
	}
	
	
	
}
