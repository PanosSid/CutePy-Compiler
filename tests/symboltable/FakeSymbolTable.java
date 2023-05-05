package symboltable;

import java.util.ArrayList;
import java.util.List;

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
		completedScopes.add(scopeStack.lastElement());
		System.out.println(this);
		super.removeScope();
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
