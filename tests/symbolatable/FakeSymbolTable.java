package symbolatable;

import java.util.ArrayList;
import java.util.List;

import symboltable.Scope;
import symboltable.SymbolTable;

public class FakeSymbolTable extends SymbolTable {
	private List<Scope> completedScopes;
	
	public FakeSymbolTable() {
		super();
		completedScopes = new ArrayList<Scope>();
	}
	
	public List<Scope> getCompletedScopes() {
		return completedScopes;
	}

	@Override
	public void removeScope() {
		completedScopes.add(symbolTable.lastElement());
		System.out.println(this);
		super.removeScope();
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	
	
	
}
