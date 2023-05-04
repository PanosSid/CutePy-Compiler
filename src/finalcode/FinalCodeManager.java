package finalcode;

import exceptions.CutePyException;
import symboltable.SymbolTable;
import symboltable.entities.Entity;
import symboltable.entities.EntityWithOffset;
import symboltable.entities.FormalParameter;
import symboltable.entities.Parameter;
import symboltable.entities.ParameterMode;
import symboltable.entities.TemporaryVariable;
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
	
	public void loadvr(String varName, String reg) throws CutePyException {
		Entity entity = symbolTable.searchEntity(varName);
		if (entity instanceof TemporaryVariable ||
				isLocalVariable(entity) || isParamPassedByValue(entity)) {
			addToFinalCode("lw "+reg+", -"+ ((EntityWithOffset) entity).getOffset() + "(sp)");
		} else if (isAncestorsLocalVariable(entity) || isAncestorsParameter(entity)) {
			  
		}
			 
	}
	
	private boolean isAncestorsParameter(Entity entity)  {
		return false;
	}
	
	private boolean isAncestorsLocalVariable(Entity entity) throws CutePyException {
		if (entity.getFoundScope() - symbolTable.getLastScopeNum() < 0 /* different scopes*/  
				&& entity instanceof Variable) {
			return true;
		}
		return false;
	}
	
	private boolean isLocalVariable(Entity entity) throws CutePyException {
		if (entity.getFoundScope() - symbolTable.getLastScopeNum() == 0
				&& entity instanceof Variable) {
			return true;
		}
		return false;
	}
	
	private boolean isParamPassedByValue(Entity entity) {
		if (entity instanceof Parameter &&
				((Parameter) entity).getMode().equals(ParameterMode.CV)) {
			return true;
		}
		return false;
	}
	
	private void addToFinalCode(String str) {
		finalCode += "\t"+str+"\n"; 
	}
	
	private void addLabelToFinalCode(String str) {
		finalCode += str+"\n";
	}
}
