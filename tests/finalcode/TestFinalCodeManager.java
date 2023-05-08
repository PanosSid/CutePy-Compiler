package finalcode;

import java.util.ArrayList;
import java.util.Stack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import symboltable.Scope;
import symboltable.SymbolTable;
import symboltable.entities.Entity;
import symboltable.entities.FormalParameter;
import symboltable.entities.LocalFunction;
import symboltable.entities.MainFunction;
import symboltable.entities.Parameter;
import symboltable.entities.TemporaryVariable;
import symboltable.entities.Variable;

public class TestFinalCodeManager {
	private SymbolTable symbolTable;
	private FinalCodeManager finManager;
	
	public TestFinalCodeManager() {
		symbolTable = new SymbolTable();
		finManager = new FinalCodeManager(symbolTable);;
	}

	private String getInitFCode() {
		return ".data\n\n.text\n\nL0:\n\tj main\n\n";
	}
	
	@Test
	public void testGnlvcode() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f1")));
		scopes.push(new Scope(
				new Variable("a", 12),
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>()),
				new LocalFunction("f3", 0, 0, new ArrayList<FormalParameter>())
				));

		scopes.push(new Scope(
				new LocalFunction("f4", 104, 16, new ArrayList<FormalParameter>())
				));

		scopes.push(new Scope(
				new TemporaryVariable("T_1", 12)
				));
		symbolTable.setScopeStack(scopes);
		finManager.gnvlcode("a");
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tlw t0, -4(sp)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\taddi t0, t0, -12\n";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	
	/*
	 * Note this symbolTable does not correspond to a valid program 
	 * is used only to test the gnlvcode()
	 */
	@Test
	public void testGnlvcode2() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f1")));
		scopes.push(new Scope(
				new Variable("a", 12),
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>()),
				new LocalFunction("f3", 0, 0, new ArrayList<FormalParameter>())
				));
		
		scopes.push(new Scope(
				new Variable("b", 12),
				new Variable("a", 16), 	//looking for this at scope 2 
				new LocalFunction("f21", 101, 12, new ArrayList<FormalParameter>()),
				new LocalFunction("f31", 0, 0, new ArrayList<FormalParameter>())
				));

		scopes.push(new Scope(
				new Variable("k", 12),
				new TemporaryVariable("T_1", 16),
				new LocalFunction("f41", 104, 16, new ArrayList<FormalParameter>())
				));
		
		scopes.push(new Scope(
				new TemporaryVariable("T_12", 12),
				new TemporaryVariable("T_11", 16),
				new Variable("p", 12),
				new LocalFunction("f42", 104, 16, new ArrayList<FormalParameter>())
				));

		scopes.push(new Scope(
				new TemporaryVariable("T_1", 12)	// we are on scope 5
				));
		symbolTable.setScopeStack(scopes);
		finManager.gnvlcode("a");
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tlw t0, -4(sp)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\taddi t0, t0, -16\n";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testLoadvrForInteger() throws CutePyException {
		finManager.loadvr("100", "t1");
		finManager.loadvr("200", "t2");
		
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tli t1, 100\n"
				+ "\tli t2, 200\n"
				;
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testLoadvrFor1stCase() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f1")));
		scopes.push(new Scope(
				new Variable("var", 12),	/* not this !!*/
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Parameter("pbyvalue", 12),
				new Variable("var", 16),
				new TemporaryVariable("T_1", 20)
				));
		symbolTable.setScopeStack(scopes);
		
		finManager.loadvr("var", "t1");
		finManager.loadvr("pbyvalue", "t2");
		finManager.loadvr("T_1", "t3");
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tlw t1, -16(sp)\n"
				+ "\tlw t2, -12(sp)\n"
				+ "\tlw t3, -20(sp)\n"
				;
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testLoadvrFor2ndCase1() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f")));
		scopes.push(new Scope(
				new LocalFunction("f1", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Parameter("x", 12),
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new LocalFunction("f3", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Variable("a", 12)
				));
		symbolTable.setScopeStack(scopes);
		
		finManager.loadvr("x", "t1");
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tlw t0, -4(sp)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\taddi t0, t0, -12\n"
				+ "\tlw t1, (t0)\n"
				;
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testLoadvrFor2ndCase2() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f")));
		scopes.push(new Scope(
				new Variable("ancestorVar", 12),
				new LocalFunction("f1", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Parameter("x", 12),
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new LocalFunction("f3", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Variable("a", 12)
				));
		symbolTable.setScopeStack(scopes);
		
		finManager.loadvr("ancestorVar", "t1");
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tlw t0, -4(sp)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\taddi t0, t0, -12\n"
				+ "\tlw t1, (t0)\n"
				;
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testStorervFor1stCase() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f1")));
		scopes.push(new Scope(
				new Variable("var", 12),	/* not this !!*/
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Parameter("pbyvalue", 12),
				new Variable("var", 16),
				new TemporaryVariable("T_1", 20)
				));
		symbolTable.setScopeStack(scopes);
		
		finManager.storerv("t1", "var");
		finManager.storerv("t2", "pbyvalue");
		finManager.storerv("t3", "T_1");
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tsw t1, -16(sp)\n"
				+ "\tsw t2, -12(sp)\n"
				+ "\tsw t3, -20(sp)\n"
				;
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testStorervFor2ndCase1() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f")));
		scopes.push(new Scope(
				new LocalFunction("f1", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Parameter("x", 12),
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new LocalFunction("f3", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Variable("a", 12)
				));
		symbolTable.setScopeStack(scopes);
		
		finManager.storerv("t1", "x");
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tmv t5, t1\n"
				+ "\tlw t0, -4(sp)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\taddi t0, t0, -12\n"
				+ "\tsw t5, (t1)\n"
				;
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testStorervFor2ndCase2() throws CutePyException {
		Stack<Scope> scopes = new Stack<Scope>();
		scopes.push(new Scope((Entity) new MainFunction("main_f")));
		scopes.push(new Scope(
				new Variable("ancestorVar", 12),
				new LocalFunction("f1", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Parameter("x", 12),
				new LocalFunction("f2", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new LocalFunction("f3", 101, 12, new ArrayList<FormalParameter>())
				));
		scopes.push(new Scope(
				new Variable("a", 12)
				));
		symbolTable.setScopeStack(scopes);
		
		finManager.storerv("t1", "ancestorVar");
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tmv t5, t1\n"
				+ "\tlw t0, -4(sp)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\tlw t0, -4(t0)\n"
				+ "\taddi t0, t0, -12\n"
				+ "\tsw t5, (t1)\n"
				;
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
}
