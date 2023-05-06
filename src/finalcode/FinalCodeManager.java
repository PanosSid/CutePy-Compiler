package finalcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import exceptions.CutePyException;
import intermediatecode.Quad;
import lex.CharTypes;
import symboltable.SymbolTable;
import symboltable.entities.Entity;
import symboltable.entities.EntityWithOffset;
import symboltable.entities.Function;
import symboltable.entities.Parameter;
import symboltable.entities.ParameterMode;
import symboltable.entities.TemporaryVariable;
import symboltable.entities.Variable;

public class FinalCodeManager {
	private String finalCode = ".data\n\nstr_nl: .asciz \"\\n\"\n\n.text\n\n";
	private SymbolTable symbolTable;
	private String compiledFuncName; 	// used to determine the relation between the callee and called function
	private String funcNameToBeCalled;
	private int paramNum = 1;
	
	public FinalCodeManager() {}
	
	public FinalCodeManager(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public void emptyFinalCode() {
		finalCode = "";
	}
	
	public String getFinalCode() {
		return finalCode;
	}
	
	public void initMainFinalCode() {
		finalCode += "L0:\n\tj main\n";
	}
	
	public void genFinalCode(int startingLabel, Map<Integer, Quad> intermedCode) throws CutePyException {
		List<Integer> keys = new ArrayList<>(intermedCode.keySet());	
		for (int i = 0; i < keys.size(); i++) {
			Integer key = keys.get(i);
			if (key >= startingLabel) {
				if (intermedCode.get(key).getOperator().equals("par")) {
					int j = i;
					Integer key2 = keys.get(j);
					Quad quad  = intermedCode.get(key2);
					while (!quad.getOperator().equals("call")) {
						j++;
						key2 = keys.get(j);
						quad = intermedCode.get(key2);
					}
					funcNameToBeCalled= quad.getOperand1();
				}
				transformQuadToFinalCode(key ,intermedCode.get(key)); 
			}
		}
	}
	
	public void transformQuadToFinalCode(int label, Quad quad) throws CutePyException {
		String operator = quad.getOperator();
		if (operator.equals("begin_block")) {
			compiledFuncName = quad.getOperand1();
			if (quad.getOperand1().equals("main")) {
				addLabelToFinalCodeFunc(quad.getOperand1(), label, quad);
//				addLabelToFinalCode(label, quad);
				addToFinalCode("addi sp, sp, 12");
				addToFinalCode("mv gp, sp");
			} else {
				addLabelToFinalCodeFunc(quad.getOperand1(), label, quad);
//				addLabelToFinalCode(label, quad);
				addToFinalCode("sw ra, -0(sp)");
			}
			return;
		}
		
		addLabelToFinalCode(label, quad);
		if (operator.equals("end_block")) {
			if (quad.getOperand1().equals("main")) {
				// TODO needs something ???
			} else {
				addToFinalCode("lw ra, (sp)");
				addToFinalCode("jr ra");
			}
			
		} else if (operator.equals(":=")) {
			loadvr(quad.getOperand1(), "t0");		
			storerv("t0", quad.getOperand3());				
		} else if (CharTypes.ADD_OPS.contains(operator.charAt(0)) || CharTypes.MUL_OPS.contains(operator)) {
			loadvr(quad.getOperand1(),"t1");
			loadvr(quad.getOperand2(),"t2");
			if (operator.equals("+")) {
				addToFinalCode("add t1, t1, t2"); 	// add rd r1 r2 = rd = r1 + r2
			} else if (operator.equals("-")) {
				addToFinalCode("sub t1, t1, t2");
			} else if  (operator.equals("*")) {
				addToFinalCode("mul t1, t1, t2");
			} else if  (operator.equals("//")) {
				addToFinalCode("div t1, t1, t2");
			}
			storerv("t1",quad.getOperand3());
		} else if (CharTypes.REL_OPS.contains(operator)) {
			loadvr(quad.getOperand1(), "t1");
			loadvr(quad.getOperand2(), "t2");			
			if (operator.equals("==")) { 
				addToFinalCode("beq t1, t2, L"+quad.getOperand3());
			} else if (operator.equals("!=")) {
				addToFinalCode("bne t1, t2, L"+quad.getOperand3());
			} else if (operator.equals(">")) {
				addToFinalCode("bgt t1, t2, L"+quad.getOperand3());
			} else if (operator.equals("<")) {
				addToFinalCode("blt t1, t2, L"+quad.getOperand3());
			} else if (operator.equals(">=")) {
				addToFinalCode("bge t1, t2, L"+quad.getOperand3());
			} else if (operator.equals("<=")) {
				addToFinalCode("ble t1, t2, L"+quad.getOperand3());
			}
		} else if (operator.equals("jump")) {
			// prosoxi mipos kanei jumb se label poy exoume onomasei me string?
			addToFinalCode("j "+"L"+quad.getOperand3());
		} else if (operator.equals("out")) {
			EntityWithOffset entity = (EntityWithOffset) symbolTable.searchEntity(quad.getOperand3());
			addToFinalCode("lw a0, -"+entity.getOffset()+"(sp)");
			addToFinalCode("li a7, 1");
			addToFinalCode("ecall");
			addToFinalCode("la a0, str_nl"); 	// go to next line !!!
			addToFinalCode("li a7, 4");
			addToFinalCode("ecall");
		} else if (operator.equals("in")) {
			addToFinalCode("li a7, 5");
			addToFinalCode("ecall");
			EntityWithOffset entity = (EntityWithOffset) symbolTable.searchEntity(quad.getOperand1());
			addToFinalCode("sw a0, -"+entity.getOffset()+"(sp)");
			
		} else if (operator.equals("ret")) {	
			loadvr(quad.getOperand1(), "t1");
			addToFinalCode("lw t0, -8(sp)" );
			addToFinalCode("sw t1, (t0)" );
//			addToFinalCode("lw ra, (sp)" );
//			addToFinalCode("jr ra" );
		} else if (operator.equals("par")) {
			Function funcToBeCalled = (Function) symbolTable.searchEntity(funcNameToBeCalled);	// this must be the function  that will be called later!!!
			if (paramNum == 1) {
				addToFinalCode("addi fp, sp, "+funcToBeCalled.getFramelength());
			}
			
			if (quad.getOperand2().equals("cv")) {
				loadvr(quad.getOperand1(), "t0");
				addToFinalCode("sw t0, -"+(12+4*(paramNum-1))+"(fp)"); 	// paramNum starts from 1 so we need that paramNum-1 
			} else if (quad.getOperand2().equals("ret")) {
				TemporaryVariable tempVar = (TemporaryVariable) symbolTable.searchEntity(quad.getOperand1());
				addToFinalCode("addi t0, sp, -"+tempVar.getOffset());
				addToFinalCode("sw t0, -"+8+"(fp)");
			} else {
				throw new CutePyException("Unknown type of parameter with name "+quad.getOperand1());
			}

			paramNum ++;
		} else if (operator.equals("call")) {
			paramNum = 1;
			Function entity = (Function) symbolTable.searchEntity(quad.getOperand1());
			
			if (quad.getOperand1().startsWith("main_")) {
				addToFinalCode("addi fp, sp, "+entity.getFramelength());
				addToFinalCode("sw sp, -4(fp)");
				addToFinalCode("addi sp, sp, "+ entity.getFramelength());
				addToFinalCode("jal "+quad.getOperand1());
				addToFinalCode("addi sp, sp, -"+entity.getFramelength());
				return;
			}
			
			Function calleeEntity = (Function) symbolTable.searchEntity(compiledFuncName);
			if (calleeEntity.getFoundScope() == entity.getFoundScope()) {
				addToFinalCode("lw t0, -4(sp)");
				addToFinalCode("sw t0, -4(fp)");
			} else if (calleeEntity.getFoundScope() < entity.getFoundScope()) {
				addToFinalCode("sw sp, -4(fp)");
			} else {
				throw new CutePyException("Called and callee functions found in wrong scopes !!!");
			}
			addToFinalCode("addi sp, sp, "+ entity.getFramelength());
			addToFinalCode("jal "+quad.getOperand1());
			addToFinalCode("addi sp, sp, -"+entity.getFramelength());
			
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
	        Integer.parseInt(operand1);
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
		if (isNumber(varName)) {
			addToFinalCode("li "+reg+", "+varName);
			return;
		}		
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
	
	private void addLabelToFinalCode(int lbl, Quad quad) {
		finalCode += "\nL"+lbl+":\t\t\t# "+lbl+": "+quad+"\n";
	}
	
	private void addLabelToFinalCodeFunc(String funcName, int lbl, Quad quad) {
		finalCode += "\n"+funcName+":\t\t\t# "+lbl+": "+quad+"\n";
	}
}
