package finalcode;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import lex.FileReader;
import lex.LexAnalyser;
import symboltable.Scope;
import symboltable.SymbolTable;
import symboltable.entities.Entity;
import symboltable.entities.FormalParameter;
import symboltable.entities.LocalFunction;
import symboltable.entities.MainFunction;
import symboltable.entities.TemporaryVariable;
import symboltable.entities.Variable;

public class TestFinalCode {
	private SymbolTable symbolTable;
	private FinalCodeManager finManager;
	
	public TestFinalCode() {
		symbolTable = new SymbolTable();
		finManager = new FinalCodeManager(symbolTable);;
	}
//	private void setUpSyntaxAnalyser(String sourceCode) {
//		FileReader reader = new FileReader();
//		reader.setFileContents(sourceCode);
//		LexAnalyser lex = new LexAnalyser(reader);
//		SymbolTable symbolTable = new SymbolTable();
////		finManager = new FinalCodeManager(symbolTable);
////		syntax = new SyntaxAnalyser(lex,  new QuadManager(), symbolTable, finManager);
//	}
	
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
		finManager.gnlvcode("a");
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tlw t0, -8(sp)\n"
				+ "\tlw t0, -8(t0)\n"
				+ "\taddi t0, t0, 12\n";
		
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
		finManager.gnlvcode("a");
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "\tlw t0, -8(sp)\n"
				+ "\tlw t0, -8(t0)\n"
				+ "\tlw t0, -8(t0)\n"
				+ "\taddi t0, t0, 16\n";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
}
