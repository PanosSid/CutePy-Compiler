package finalcode;

import exceptions.CutePyException;
import symboltable.SymbolTable;
import symboltable.entities.Entity;
import symboltable.entities.Variable;

public class FinalCodeManager {
	private String finalCode = ".data\n\n.text\n\n";
	private SymbolTable symbolTable;
	
	public FinalCodeManager(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public String getFinalCode() {
		return finalCode;
	}
	
	public void gnvlcode(String variableName) throws CutePyException {
		Variable entity = (Variable) symbolTable.searchEntity(variableName);
		int foundScope = entity.getFoundScope();
		int lastScope = symbolTable.getLastScopeNum();
		int scopesToClimb = lastScope - foundScope;
		
		addToFinalCode("lw t0, -8(sp)");
		for (int i = 0; i < scopesToClimb-1; i++) {
			addToFinalCode("lw t0, -8(t0)");
		}
		addToFinalCode("addi t0, t0, "+entity.getOffset());
		
		
	}
	
	
	private void addToFinalCode(String str) {
		finalCode += "\t"+str+"\n"; 
	}
	
	private void addLabelToFinalCode(String str) {
		finalCode += str+"\n";
	}
}
