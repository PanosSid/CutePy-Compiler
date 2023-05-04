package finalcode;

import java.util.Map;

import org.junit.platform.commons.util.StringUtils;

import exceptions.CutePyException;
import intermediatecode.Quad;
import lex.CharTypes;
import symboltable.SymbolTable;
import symboltable.entities.Entity;
import symboltable.entities.EntityWithOffset;
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
	
	public void initMainFinalCode() {
		finalCode += "L0:\n\tj main\n\n";
	}
	
	public void genFinalCode(int startingLabel, Map<Integer, Quad> intermedCode) throws CutePyException {
		for (Integer key : intermedCode.keySet()) {
			if (key >= startingLabel) {
				transformQuadToFinalCode(key ,intermedCode.get(key)); 
			}
		}
	}
	
	private void transformQuadToFinalCode(int label, Quad quad) throws CutePyException {
		String operator = quad.getOperator();
		if (operator.equals("begin_block")) {
			if (quad.getOperand1().equals("main")) {
				finalCode += "\nmain:\n";
				addToFinalCode("addi sp, sp, 12");
				addToFinalCode("mv gp, sp");
			}
		} else if (operator.equals("end_block")) {
			
		} else if (operator.equals(":=")) {
			addLabelToFinalCode(label);
			if (isNumber(quad.getOperand1())){ 	//TODO move this logic to loadvr (and maybe to storevr ?)!!! 
				addToFinalCode("li t0, "+quad.getOperand1());
				storerv("t0", quad.getOperand3());
			} else {
				loadvr(quad.getOperand1(), "t0");		
				storerv("t0", quad.getOperand3());				
				
			}
		} else if (CharTypes.ADD_OPS.contains(operator.charAt(0)) || CharTypes.MUL_OPS.contains(operator)) {
//			loadvr(quad.getOperand1(),"t1");
//			loadvr(quad.getOperand2(),"t2");
//			if (operator.equals("+")) {
//				addToFinalCode("add t1,t2,t1");
//			} else if (quad.getOperand1().equals("-")) {
//				addToFinalCode("sub t1,t2,t1");
//			} else if  (quad.getOperand1().equals("*")) {
//				addToFinalCode("mul t1,t2,t1");
//			} else if  (quad.getOperand1().equals("//")) {
//				addToFinalCode("div t1,t2,t1");
//			}
//			storerv("t1",quad.getOperand3());
		} else if (CharTypes.REL_OPS.contains(operator)) {
			 
		} else if (operator.equals("jump")) {
			
		} else if (operator.equals("out")) {
			
		} else if (operator.equals("in")) {
			
		} else if (operator.equals("ret")) {
			
		} else if (operator.equals("par")) {
			
		} else if (operator.equals("call")) { 	// TODO impement this for main_ funcs to run tests !!!!
			addLabelToFinalCode(label);
			addToFinalCode("not implemented yet");
		} else if (operator.equals("halt")) {
			addToFinalCode("li a0, 0");
			addToFinalCode("li a7, 93");
			addToFinalCode("ecall");
		}
	}

	private boolean isNumber(String operand1) {
	    if (operand1 == null) {
	        return false;
	    }
	    try {
	        int n = Integer.parseInt(operand1);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	public void gnvlcode(String variableName) throws CutePyException {
		Entity entity =  symbolTable.searchEntity(variableName);
		int foundScope = entity.getFoundScope();
		int lastScope = symbolTable.getLastScopeNum();
		int scopesToClimb = lastScope - foundScope;
		
		addToFinalCode("lw t0, -8(sp)");
		for (int i = 0; i < scopesToClimb-1; i++) {
			addToFinalCode("lw t0, -8(t0)");
		}
		addToFinalCode("addi t0, t0, -"+ ((EntityWithOffset) entity).getOffset());
		
		
	}
	
	public void loadvr(String varName, String reg) throws CutePyException {
		Entity entity = symbolTable.searchEntity(varName);
		if (entity instanceof TemporaryVariable ||
				isLocalVariable(entity) || isParamPassedByValue(entity)) {
			addToFinalCode("lw "+reg+", -"+ ((EntityWithOffset) entity).getOffset() + "(sp)");
		} else if (isAncestorsLocalVariable(entity) || isAncestorsParamByValue(entity)) {
			gnvlcode(varName);
			addToFinalCode("lw "+reg+", (t0)");
		} else {
			throw new CutePyException("FinalCodeManager.laodvr("+varName+","+reg+") unknwon load case");
		}		 
	}
	
	public void storerv(String reg, String varName) throws CutePyException {
		Entity entity = symbolTable.searchEntity(varName);
		if (entity instanceof TemporaryVariable ||
				isLocalVariable(entity) || isParamPassedByValue(entity)) {
			addToFinalCode("sw "+reg+", -"+ ((EntityWithOffset) entity).getOffset() + "(sp)");
		} else if (isAncestorsLocalVariable(entity) || isAncestorsParamByValue(entity)) {
			gnvlcode(varName);
			addToFinalCode("sw "+reg+", (t0)");
		} else {
			throw new CutePyException("FinalCodeManager.storerv("+varName+","+reg+") unknwon store case");
		}
			 
	}
	
	private boolean isAncestorsParamByValue(Entity entity) throws CutePyException  {
		if (entity instanceof Parameter && !isOnTheLastScope(entity) &&
				((Parameter) entity).getMode().equals(ParameterMode.CV)) {
			return true;
		}
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
		if (isOnTheLastScope(entity)
				&& entity instanceof Variable) {
			return true;
		}
		return false;
	}

	private boolean isParamPassedByValue(Entity entity) throws CutePyException {
		if (entity instanceof Parameter && isOnTheLastScope(entity) &&
				((Parameter) entity).getMode().equals(ParameterMode.CV)) {
			return true;
		}
		return false;
	}
	
	private boolean isOnTheLastScope(Entity entity) throws CutePyException {
		return entity.getFoundScope() - symbolTable.getLastScopeNum() == 0;
	}
	
	private void addToFinalCode(String str) {
		finalCode += "\t"+str+"\n"; 
	}
	
	private void addLabelToFinalCode(int lbl) {
		finalCode += "\nL"+lbl+":\n";
	}
}
