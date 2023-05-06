package finalcode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import intermediatecode.Quad;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import symboltable.SymbolTable;
import syntax.SyntaxAnalyser;

public class TestFinalCode {
	private SyntaxAnalyser syntax;
	private FinalCodeManager finManager;
	
	public TestFinalCode() {
		finManager = new FinalCodeManager();
	}
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		SymbolTable symbolTable = new SymbolTable();
		finManager = new FinalCodeManager(symbolTable);
		syntax = new SyntaxAnalyser(lex,  new QuadManager(1), symbolTable, finManager);
	}
	
	@Test
	public void testFinalCodeBeginBlockMain() throws CutePyException { 
		String quadStr = "begin_block, main, _, _";
		runTransformToQuad(1, quadStr);
		String expectedFinalCode = "\n"
				+ "main:\t\t\t# 1: "+quadStr+"\n"
				+ "\taddi sp, sp, 12\n"
				+ "\tmv gp, sp\n";		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testFinalCodeBeginBlockMainFunc() throws CutePyException { 
		String quadStr = "begin_block, main_func, _, _";
		runTransformToQuad(1, quadStr);
		String expectedFinalCode = "\n"
				+ "main_func:\t\t\t# 1: "+quadStr+"\n"
				+ "\tsw ra, -0(sp)\n";		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testFinalCodeBeginBlockLocalFunc() throws CutePyException { 
		String quadStr = "begin_block, localfunc, _, _";
		runTransformToQuad(1, quadStr);
		String expectedFinalCode = "\n"
				+ "localfunc:\t\t\t# 1: "+quadStr+"\n"
				+ "\tsw ra, -0(sp)\n";		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testFinalCodeEndBlockMain() throws CutePyException { 
		String quadStr = "end_block, main, _, _";
		runTransformToQuad(1, quadStr);
		String expectedFinalCode = "\n"
				+ "L1:\t\t\t# 1: "+quadStr+"\n";		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testFinalCodeEndBlockFunc() throws CutePyException { 
		String quadStr = "end_block, afunc, _, _";
		runTransformToQuad(1, quadStr);
		String expectedFinalCode = "\n"
				+ "L1:\t\t\t# 1: "+quadStr+"\n"	
				+ "\tlw ra, (sp)\n"
				+ "\tjr ra\n";
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testFinalCodeAssign() throws CutePyException {
		String quadStr = ":=, 1, _, a";
		runTransformToQuad(1, quadStr);
		String expectedFinalCode = "\n"
				+ "L1:\t\t\t# 1: "+quadStr+"\n"	
				+ "\tlw ra, (sp)\n"
				+ "\tjr ra\n";
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	private void runTransformToQuad(int lbl, String quadStr) throws CutePyException {
		finManager.emptyFinalCode();
		finManager.transformQuadToFinalCode(1, new Quad(quadStr));	
	}
	
	
	
	@Test
	public void testFinalCodeExample1() throws CutePyException { 
		setUpSyntaxAnalyser(""
				+ "def main_fc_example1():\n"
				+ "#{\n"
				+ "	#declare a,b,c\n"
				+ "	\n"
				+ "	def f(a,b):\n"
				+ "	#{\n"
				+ "		b = a + 1;\n"
				+ "		c = 4; \n"
				+ "		return(b);\n"
				+ "	#}\n"
				+ "	\n"
				+ "	a = 1;\n"
				+ "	c = f(a,b);\n"
				+ "	print(c);\n"
				+ "	print(b);\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_fc_example1();"
		);
		syntax.analyzeSyntax();
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ "str_nl: .asciz \"\\n\"\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "L0:\n"
				+ "	j main\n"
				+ "\n"
				+ "f:			# 1: begin_block, f, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L2:			# 2: +, a, 1, &1\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 1\n"
				+ "	add t1,t2,t1\n"
				+ "	sw t1, -20(sp)\n"
				+ "\n"
				+ "L3:			# 3: :=, &1, _, b\n"
				+ "	lw t0, -20(sp)\n"
				+ "	sw t0, -16(sp)\n"
				+ "\n"
				+ "L4:			# 4: :=, 4, _, c\n"
				+ "	li t0, 4\n"
				+ "	lw t0, -8(sp)\n"
				+ "	addi t0, t0, -20\n"
				+ "	sw t0, (t0)\n"
				+ "\n"
				+ "L5:			# 5: ret, b, _, _\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "\n"
				+ "L6:			# 6: end_block, f, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main_fc_example1:			# 7: begin_block, main_fc_example1, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L8:			# 8: :=, 1, _, a\n"
				+ "	li t0, 1\n"
				+ "	sw t0, -12(sp)\n"
				+ "\n"
				+ "L9:			# 9: par, a, cv, _\n"
				+ "	addi fp, sp, 24\n"
				+ "	lw t0, -12(sp)\n"
				+ "	sw t0, -12(fp)\n"
				+ "\n"
				+ "L10:			# 10: par, b, cv, _\n"
				+ "	lw t0, -16(sp)\n"
				+ "	sw t0, -16(fp)\n"
				+ "\n"
				+ "L11:			# 11: par, &2, ret, _\n"
				+ "	addi t0, sp, -24\n"
				+ "	sw t0, -8(fp)\n"
				+ "\n"
				+ "L12:			# 12: call, f, _, _\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 24\n"
				+ "	jal f\n"
				+ "	addi sp, sp, -24\n"
				+ "\n"
				+ "L13:			# 13: :=, &2, _, c\n"
				+ "	lw t0, -24(sp)\n"
				+ "	sw t0, -20(sp)\n"
				+ "\n"
				+ "L14:			# 14: out, _, _, c\n"
				+ "	lw a0, -20(sp)\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L15:			# 15: out, _, _, b\n"
				+ "	lw a0, -16(sp)\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L16:			# 16: end_block, main_fc_example1, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main:			# 17: begin_block, main, _, _\n"
				+ "	addi sp, sp, 12\n"
				+ "	mv gp, sp\n"
				+ "\n"
				+ "L18:			# 18: call, main_fc_example1, _, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal main_fc_example1\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L19:			# 19: halt, _, _, _\n"
				+ "	li a0, 0\n"
				+ "	li a7, 93\n"
				+ "	ecall\n"
				+ "\n"
				+ "L20:			# 20: end_block, main, _, _\n"
				+ "";
		
	}
	
}
