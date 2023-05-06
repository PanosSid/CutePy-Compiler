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
	
//	@Test
//	public void testFinalCodeAssign() throws CutePyException {
//		String quadStr = ":=, 1, _, a";
//		runTransformToQuad(1, quadStr);
//		String expectedFinalCode = "\n"
//				+ "L1:\t\t\t# 1: "+quadStr+"\n"	
//				+ "\tlw ra, (sp)\n"
//				+ "\tjr ra\n";
//		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
//	}
	
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
				+ "	add t1, t1, t2\n"
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
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
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
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
		
	}
	
	@Test
	public void testFinalCodeCountDigits() throws CutePyException { 
		setUpSyntaxAnalyser(""
				+ "def main_countdigits():\n"
				+ "#{\n"
				+ "	#declare x, count\n"
				+ "\n"
				+ "	x = int(input());\n"
				+ "	count = 0;\n"
				+ "	while (x>0):\n"
				+ "	#{\n"
				+ "		x = x // 10;\n"
				+ "		count = count + 1;\n"
				+ "	#}\n"
				+ "	print(count);\n"
				+ "\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_countdigits();\n"
				+ ""
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
				+ "main_countdigits:			# 1: begin_block, main_countdigits, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L2:			# 2: in, x, _, _\n"
				+ "	li a7, 5\n"
				+ "	ecall\n"
				+ "	sw a0, -12(sp)\n"
				+ "\n"
				+ "L3:			# 3: :=, 0, _, count\n"
				+ "	li t0, 0\n"
				+ "	sw t0, -16(sp)\n"
				+ "\n"
				+ "L4:			# 4: >, x, 0, 6\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 0\n"
				+ "	bgt t1, t2, L6\n"
				+ "\n"
				+ "L5:			# 5: jump, _, _, 11\n"
				+ "	j L11\n"
				+ "\n"
				+ "L6:			# 6: //, x, 10, &1\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 10\n"
				+ "	div t1, t1, t2\n"
				+ "	sw t1, -20(sp)\n"
				+ "\n"
				+ "L7:			# 7: :=, &1, _, x\n"
				+ "	lw t0, -20(sp)\n"
				+ "	sw t0, -12(sp)\n"
				+ "\n"
				+ "L8:			# 8: +, count, 1, &2\n"
				+ "	lw t1, -16(sp)\n"
				+ "	li t2, 1\n"
				+ "	add t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L9:			# 9: :=, &2, _, count\n"
				+ "	lw t0, -24(sp)\n"
				+ "	sw t0, -16(sp)\n"
				+ "\n"
				+ "L10:			# 10: jump, _, _, 4\n"
				+ "	j L4\n"
				+ "\n"
				+ "L11:			# 11: out, _, _, count\n"
				+ "	lw a0, -16(sp)\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L12:			# 12: end_block, main_countdigits, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main:			# 13: begin_block, main, _, _\n"
				+ "	addi sp, sp, 12\n"
				+ "	mv gp, sp\n"
				+ "\n"
				+ "L14:			# 14: call, main_countdigits, _, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal main_countdigits\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L15:			# 15: halt, _, _, _\n"
				+ "	li a0, 0\n"
				+ "	li a7, 93\n"
				+ "	ecall\n"
				+ "\n"
				+ "L16:			# 16: end_block, main, _, _\n"
				+ "";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
		
	}
	
	@Test
	public void testFinalCodeFactorial() throws CutePyException { 
		setUpSyntaxAnalyser(""
				+ "def main_factorial():\n"
				+ "#{\n"
				+ "	#$ declarations #$\n"
				+ "	#declare x\n"
				+ "	#declare i,fact\n"
				+ "	\n"
				+ "	#$ body of main_factorial #$\n"
				+ "	x = int(input());\n"
				+ "	fact = 1;\n"
				+ "	i = 1;\n"
				+ "	while (i<=x):\n"
				+ "	#{\n"
				+ "		fact = fact * i;\n"
				+ "		i = i + 1;\n"
				+ "	#}\n"
				+ "	print(fact);\n"
				+ "\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_factorial();"
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
				+ "main_factorial:			# 1: begin_block, main_factorial, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L2:			# 2: in, x, _, _\n"
				+ "	li a7, 5\n"
				+ "	ecall\n"
				+ "	sw a0, -12(sp)\n"
				+ "\n"
				+ "L3:			# 3: :=, 1, _, fact\n"
				+ "	li t0, 1\n"
				+ "	sw t0, -20(sp)\n"
				+ "\n"
				+ "L4:			# 4: :=, 1, _, i\n"
				+ "	li t0, 1\n"
				+ "	sw t0, -16(sp)\n"
				+ "\n"
				+ "L5:			# 5: <=, i, x, 7\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	ble t1, t2, L7\n"
				+ "\n"
				+ "L6:			# 6: jump, _, _, 12\n"
				+ "	j L12\n"
				+ "\n"
				+ "L7:			# 7: *, fact, i, &1\n"
				+ "	lw t1, -20(sp)\n"
				+ "	lw t2, -16(sp)\n"
				+ "	mul t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L8:			# 8: :=, &1, _, fact\n"
				+ "	lw t0, -24(sp)\n"
				+ "	sw t0, -20(sp)\n"
				+ "\n"
				+ "L9:			# 9: +, i, 1, &2\n"
				+ "	lw t1, -16(sp)\n"
				+ "	li t2, 1\n"
				+ "	add t1, t1, t2\n"
				+ "	sw t1, -28(sp)\n"
				+ "\n"
				+ "L10:			# 10: :=, &2, _, i\n"
				+ "	lw t0, -28(sp)\n"
				+ "	sw t0, -16(sp)\n"
				+ "\n"
				+ "L11:			# 11: jump, _, _, 5\n"
				+ "	j L5\n"
				+ "\n"
				+ "L12:			# 12: out, _, _, fact\n"
				+ "	lw a0, -20(sp)\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L13:			# 13: end_block, main_factorial, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main:			# 14: begin_block, main, _, _\n"
				+ "	addi sp, sp, 12\n"
				+ "	mv gp, sp\n"
				+ "\n"
				+ "L15:			# 15: call, main_factorial, _, _\n"
				+ "	addi fp, sp, 32\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 32\n"
				+ "	jal main_factorial\n"
				+ "	addi sp, sp, -32\n"
				+ "\n"
				+ "L16:			# 16: halt, _, _, _\n"
				+ "	li a0, 0\n"
				+ "	li a7, 93\n"
				+ "	ecall\n"
				+ "\n"
				+ "L17:			# 17: end_block, main, _, _\n"
				+ "";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
		
	}
	
	@Test
	public void testFinalCodeFibonacci() throws CutePyException { 
		setUpSyntaxAnalyser(""
				+ "def main_fibonacci():\n"
				+ "#{\n"
				+ "    #declare x\n"
				+ "\n"
				+ "    def fibonacci(x):\n"
				+ "    #{\n"
				+ "        if (x<=1):\n"
				+ "            return(x);\n"
				+ "        else:\n"
				+ "            return (fibonacci(x-1)+fibonacci(x-2));\n"
				+ "    #}\n"
				+ "\n"
				+ "    x = int(input());\n"
				+ "    print(fibonacci(x));\n"
				+ "    \n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_fibonacci();"
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
				+ "fibonacci:			# 1: begin_block, fibonacci, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L2:			# 2: <=, x, 1, 4\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 1\n"
				+ "	ble t1, t2, L4\n"
				+ "\n"
				+ "L3:			# 3: jump, _, _, 6\n"
				+ "	j L6\n"
				+ "\n"
				+ "L4:			# 4: ret, x, _, _\n"
				+ "	lw t1, -12(sp)\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L5:			# 5: jump, _, _, 16\n"
				+ "	j L16\n"
				+ "\n"
				+ "L6:			# 6: -, x, 1, &1\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 1\n"
				+ "	sub t1, t1, t2\n"
				+ "	sw t1, -16(sp)\n"
				+ "\n"
				+ "L7:			# 7: par, &1, cv, _\n"
				+ "	addi fp, sp, 36\n"
				+ "	lw t0, -16(sp)\n"
				+ "	sw t0, -12(fp)\n"
				+ "\n"
				+ "L8:			# 8: par, &2, ret, _\n"
				+ "	addi t0, sp, -20\n"
				+ "	sw t0, -8(fp)\n"
				+ "\n"
				+ "L9:			# 9: call, fibonacci, _, _\n"
				+ "	lw t0, -4(sp)\n"
				+ "	sw t0, -4(fp)\n"
				+ "	addi sp, sp, 36\n"
				+ "	jal fibonacci\n"
				+ "	addi sp, sp, -36\n"
				+ "\n"
				+ "L10:			# 10: -, x, 2, &3\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 2\n"
				+ "	sub t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L11:			# 11: par, &3, cv, _\n"
				+ "	addi fp, sp, 36\n"
				+ "	lw t0, -24(sp)\n"
				+ "	sw t0, -12(fp)\n"
				+ "\n"
				+ "L12:			# 12: par, &4, ret, _\n"
				+ "	addi t0, sp, -28\n"
				+ "	sw t0, -8(fp)\n"
				+ "\n"
				+ "L13:			# 13: call, fibonacci, _, _\n"
				+ "	lw t0, -4(sp)\n"
				+ "	sw t0, -4(fp)\n"
				+ "	addi sp, sp, 36\n"
				+ "	jal fibonacci\n"
				+ "	addi sp, sp, -36\n"
				+ "\n"
				+ "L14:			# 14: +, &2, &4, &5\n"
				+ "	lw t1, -20(sp)\n"
				+ "	lw t2, -28(sp)\n"
				+ "	add t1, t1, t2\n"
				+ "	sw t1, -32(sp)\n"
				+ "\n"
				+ "L15:			# 15: ret, &5, _, _\n"
				+ "	lw t1, -32(sp)\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L16:			# 16: end_block, fibonacci, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main_fibonacci:			# 17: begin_block, main_fibonacci, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L18:			# 18: in, x, _, _\n"
				+ "	li a7, 5\n"
				+ "	ecall\n"
				+ "	sw a0, -12(sp)\n"
				+ "\n"
				+ "L19:			# 19: par, x, cv, _\n"
				+ "	addi fp, sp, 36\n"
				+ "	lw t0, -12(sp)\n"
				+ "	sw t0, -12(fp)\n"
				+ "\n"
				+ "L20:			# 20: par, &6, ret, _\n"
				+ "	addi t0, sp, -16\n"
				+ "	sw t0, -8(fp)\n"
				+ "\n"
				+ "L21:			# 21: call, fibonacci, _, _\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 36\n"
				+ "	jal fibonacci\n"
				+ "	addi sp, sp, -36\n"
				+ "\n"
				+ "L22:			# 22: out, _, _, &6\n"
				+ "	lw a0, -16(sp)\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L23:			# 23: end_block, main_fibonacci, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main:			# 24: begin_block, main, _, _\n"
				+ "	addi sp, sp, 12\n"
				+ "	mv gp, sp\n"
				+ "\n"
				+ "L25:			# 25: call, main_fibonacci, _, _\n"
				+ "	addi fp, sp, 20\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 20\n"
				+ "	jal main_fibonacci\n"
				+ "	addi sp, sp, -20\n"
				+ "\n"
				+ "L26:			# 26: halt, _, _, _\n"
				+ "	li a0, 0\n"
				+ "	li a7, 93\n"
				+ "	ecall\n"
				+ "\n"
				+ "L27:			# 27: end_block, main, _, _\n"
				+ "";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
		
	}
	
	@Test
	public void testFinalCodeDivides() throws CutePyException { 
		setUpSyntaxAnalyser(""
				+ "def main_divides():\n"
				+ "#{\n"
				+ "	#declare x, y\n"
				+ "	x = int(input());\n"
				+ "	y = int(input());\n"
				+ "	if (y == (y//x) * x):\n"
				+ "		print(1);\n"
				+ "	else:\n"
				+ "		print(0);\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_divides();"
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
				+ "main_divides:			# 1: begin_block, main_divides, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L2:			# 2: in, x, _, _\n"
				+ "	li a7, 5\n"
				+ "	ecall\n"
				+ "	sw a0, -12(sp)\n"
				+ "\n"
				+ "L3:			# 3: in, y, _, _\n"
				+ "	li a7, 5\n"
				+ "	ecall\n"
				+ "	sw a0, -16(sp)\n"
				+ "\n"
				+ "L4:			# 4: //, y, x, &1\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	div t1, t1, t2\n"
				+ "	sw t1, -20(sp)\n"
				+ "\n"
				+ "L5:			# 5: *, &1, x, &2\n"
				+ "	lw t1, -20(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	mul t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L6:			# 6: ==, y, &2, 8\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -24(sp)\n"
				+ "	beq t1, t2, L8\n"
				+ "\n"
				+ "L7:			# 7: jump, _, _, 10\n"
				+ "	j L10\n"
				+ "\n"
				+ "L8:			# 8: out, _, _, 1\n"
				+ "	li a0, 1\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L9:			# 9: jump, _, _, 11\n"
				+ "	j L11\n"
				+ "\n"
				+ "L10:			# 10: out, _, _, 0\n"
				+ "	li a0, 0\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L11:			# 11: end_block, main_divides, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main:			# 12: begin_block, main, _, _\n"
				+ "	addi sp, sp, 12\n"
				+ "	mv gp, sp\n"
				+ "\n"
				+ "L13:			# 13: call, main_divides, _, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal main_divides\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L14:			# 14: halt, _, _, _\n"
				+ "	li a0, 0\n"
				+ "	li a7, 93\n"
				+ "	ecall\n"
				+ "\n"
				+ "L15:			# 15: end_block, main, _, _\n"
				+ "";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testFinalCodePrimes() throws CutePyException { 
		setUpSyntaxAnalyser(""
				+ "def main_primes():\n"
				+ "#{\n"
				+ "	#declare i\n"
				+ "	\n"
				+ "	def isPrime(x):\n"
				+ "	#{\n"
				+ "		#declare i\n"
				+ "\n"
				+ "		def divides(x,y):\n"
				+ "		#{\n"
				+ "			if (y == (y//x) * x):\n"
				+ "				return (1);\n"
				+ "			else:\n"
				+ "				return (0);\n"
				+ "		#}\n"
				+ "\n"
				+ "		i = 2;\n"
				+ "		while (i<x):\n"
				+ "		#{\n"
				+ "			if (divides(i,x)==1):\n"
				+ "				return (0);\n"
				+ "			i = i + 1;\n"
				+ "		#}\n"
				+ "		return (1);\n"
				+ "\n"
				+ "	#}\n"
				+ "	\n"
				+ "	#$ body of main_primes #$\n"
				+ "	i = 2;\n"
				+ "	while (i<=30):\n"
				+ "	#{\n"
				+ "		if (isPrime(i)==1):\n"
				+ "		#{\n"
				+ "			print(i);\n"
				+ "			i = i + 1;\n"
				+ "		#}\n"
				+ "		else:				\n"
				+ "			i = i + 1;\n"
				+ "	#}\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_primes();"
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
				+ "divides:			# 1: begin_block, divides, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L2:			# 2: //, y, x, &1\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	div t1, t1, t2\n"
				+ "	sw t1, -20(sp)\n"
				+ "\n"
				+ "L3:			# 3: *, &1, x, &2\n"
				+ "	lw t1, -20(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	mul t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L4:			# 4: ==, y, &2, 6\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -24(sp)\n"
				+ "	beq t1, t2, L6\n"
				+ "\n"
				+ "L5:			# 5: jump, _, _, 8\n"
				+ "	j L8\n"
				+ "\n"
				+ "L6:			# 6: ret, 1, _, _\n"
				+ "	li t1, 1\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L7:			# 7: jump, _, _, 9\n"
				+ "	j L9\n"
				+ "\n"
				+ "L8:			# 8: ret, 0, _, _\n"
				+ "	li t1, 0\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L9:			# 9: end_block, divides, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "isPrime:			# 10: begin_block, isPrime, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L11:			# 11: :=, 2, _, i\n"
				+ "	li t0, 2\n"
				+ "	sw t0, -16(sp)\n"
				+ "\n"
				+ "L12:			# 12: <, i, x, 14\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	blt t1, t2, L14\n"
				+ "\n"
				+ "L13:			# 13: jump, _, _, 25\n"
				+ "	j L25\n"
				+ "\n"
				+ "L14:			# 14: par, i, cv, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	lw t0, -16(sp)\n"
				+ "	sw t0, -12(fp)\n"
				+ "\n"
				+ "L15:			# 15: par, x, cv, _\n"
				+ "	lw t0, -12(sp)\n"
				+ "	sw t0, -16(fp)\n"
				+ "\n"
				+ "L16:			# 16: par, &3, ret, _\n"
				+ "	addi t0, sp, -20\n"
				+ "	sw t0, -8(fp)\n"
				+ "\n"
				+ "L17:			# 17: call, divides, _, _\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal divides\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L18:			# 18: ==, &3, 1, 20\n"
				+ "	lw t1, -20(sp)\n"
				+ "	li t2, 1\n"
				+ "	beq t1, t2, L20\n"
				+ "\n"
				+ "L19:			# 19: jump, _, _, 22\n"
				+ "	j L22\n"
				+ "\n"
				+ "L20:			# 20: ret, 0, _, _\n"
				+ "	li t1, 0\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L21:			# 21: jump, _, _, 22\n"
				+ "	j L22\n"
				+ "\n"
				+ "L22:			# 22: +, i, 1, &4\n"
				+ "	lw t1, -16(sp)\n"
				+ "	li t2, 1\n"
				+ "	add t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L23:			# 23: :=, &4, _, i\n"
				+ "	lw t0, -24(sp)\n"
				+ "	sw t0, -16(sp)\n"
				+ "\n"
				+ "L24:			# 24: jump, _, _, 12\n"
				+ "	j L12\n"
				+ "\n"
				+ "L25:			# 25: ret, 1, _, _\n"
				+ "	li t1, 1\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L26:			# 26: end_block, isPrime, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main_primes:			# 27: begin_block, main_primes, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L28:			# 28: :=, 2, _, i\n"
				+ "	li t0, 2\n"
				+ "	sw t0, -12(sp)\n"
				+ "\n"
				+ "L29:			# 29: <=, i, 30, 31\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 30\n"
				+ "	ble t1, t2, L31\n"
				+ "\n"
				+ "L30:			# 30: jump, _, _, 43\n"
				+ "	j L43\n"
				+ "\n"
				+ "L31:			# 31: par, i, cv, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	lw t0, -12(sp)\n"
				+ "	sw t0, -12(fp)\n"
				+ "\n"
				+ "L32:			# 32: par, &5, ret, _\n"
				+ "	addi t0, sp, -16\n"
				+ "	sw t0, -8(fp)\n"
				+ "\n"
				+ "L33:			# 33: call, isPrime, _, _\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal isPrime\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L34:			# 34: ==, &5, 1, 36\n"
				+ "	lw t1, -16(sp)\n"
				+ "	li t2, 1\n"
				+ "	beq t1, t2, L36\n"
				+ "\n"
				+ "L35:			# 35: jump, _, _, 40\n"
				+ "	j L40\n"
				+ "\n"
				+ "L36:			# 36: out, _, _, i\n"
				+ "	lw a0, -12(sp)\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L37:			# 37: +, i, 1, &6\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 1\n"
				+ "	add t1, t1, t2\n"
				+ "	sw t1, -20(sp)\n"
				+ "\n"
				+ "L38:			# 38: :=, &6, _, i\n"
				+ "	lw t0, -20(sp)\n"
				+ "	sw t0, -12(sp)\n"
				+ "\n"
				+ "L39:			# 39: jump, _, _, 42\n"
				+ "	j L42\n"
				+ "\n"
				+ "L40:			# 40: +, i, 1, &7\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 1\n"
				+ "	add t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L41:			# 41: :=, &7, _, i\n"
				+ "	lw t0, -24(sp)\n"
				+ "	sw t0, -12(sp)\n"
				+ "\n"
				+ "L42:			# 42: jump, _, _, 29\n"
				+ "	j L29\n"
				+ "\n"
				+ "L43:			# 43: end_block, main_primes, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main:			# 44: begin_block, main, _, _\n"
				+ "	addi sp, sp, 12\n"
				+ "	mv gp, sp\n"
				+ "\n"
				+ "L45:			# 45: call, main_primes, _, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal main_primes\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L46:			# 46: halt, _, _, _\n"
				+ "	li a0, 0\n"
				+ "	li a7, 93\n"
				+ "	ecall\n"
				+ "\n"
				+ "L47:			# 47: end_block, main, _, _\n"
				+ "";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
	}
	
	@Test
	public void testFinalCodeIsPrime() throws CutePyException { 
		setUpSyntaxAnalyser(""
				+ "def main_isPrime():\n"
				+ "#{\n"
				+ "	#declare i,x\n"
				+ "\n"
				+ "	def divides(x,y):\n"
				+ "	#{\n"
				+ "		if (y == (y//x) * x):\n"
				+ "			return (1);\n"
				+ "		else:\n"
				+ "			return (0);\n"
				+ "	#}\n"
				+ "	\n"
				+ "	x = int(input());\n"
				+ "	i = 2;\n"
				+ "	while (i<x):\n"
				+ "	#{\n"
				+ "		if (divides(i,x)==1):\n"
				+ "		#{\n"
				+ "			print(0);\n"
				+ "		#}\n"
				+ "		i = i + 1;\n"
				+ "	#}\n"
				+ "	print(1);\n"
				+ "\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_isPrime();\n"
				+ ""
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
				+ "divides:			# 1: begin_block, divides, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L2:			# 2: //, y, x, &1\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	div t1, t1, t2\n"
				+ "	sw t1, -20(sp)\n"
				+ "\n"
				+ "L3:			# 3: *, &1, x, &2\n"
				+ "	lw t1, -20(sp)\n"
				+ "	lw t2, -12(sp)\n"
				+ "	mul t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L4:			# 4: ==, y, &2, 6\n"
				+ "	lw t1, -16(sp)\n"
				+ "	lw t2, -24(sp)\n"
				+ "	beq t1, t2, L6\n"
				+ "\n"
				+ "L5:			# 5: jump, _, _, 8\n"
				+ "	j L8\n"
				+ "\n"
				+ "L6:			# 6: ret, 1, _, _\n"
				+ "	li t1, 1\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L7:			# 7: jump, _, _, 9\n"
				+ "	j L9\n"
				+ "\n"
				+ "L8:			# 8: ret, 0, _, _\n"
				+ "	li t1, 0\n"
				+ "	lw t0, -8(sp)\n"
				+ "	sw t1, (t0)\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "L9:			# 9: end_block, divides, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main_isPrime:			# 10: begin_block, main_isPrime, _, _\n"
				+ "	sw ra, -0(sp)\n"
				+ "\n"
				+ "L11:			# 11: in, x, _, _\n"
				+ "	li a7, 5\n"
				+ "	ecall\n"
				+ "	sw a0, -16(sp)\n"
				+ "\n"
				+ "L12:			# 12: :=, 2, _, i\n"
				+ "	li t0, 2\n"
				+ "	sw t0, -12(sp)\n"
				+ "\n"
				+ "L13:			# 13: <, i, x, 15\n"
				+ "	lw t1, -12(sp)\n"
				+ "	lw t2, -16(sp)\n"
				+ "	blt t1, t2, L15\n"
				+ "\n"
				+ "L14:			# 14: jump, _, _, 26\n"
				+ "	j L26\n"
				+ "\n"
				+ "L15:			# 15: par, i, cv, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	lw t0, -12(sp)\n"
				+ "	sw t0, -12(fp)\n"
				+ "\n"
				+ "L16:			# 16: par, x, cv, _\n"
				+ "	lw t0, -16(sp)\n"
				+ "	sw t0, -16(fp)\n"
				+ "\n"
				+ "L17:			# 17: par, &3, ret, _\n"
				+ "	addi t0, sp, -20\n"
				+ "	sw t0, -8(fp)\n"
				+ "\n"
				+ "L18:			# 18: call, divides, _, _\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal divides\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L19:			# 19: ==, &3, 1, 21\n"
				+ "	lw t1, -20(sp)\n"
				+ "	li t2, 1\n"
				+ "	beq t1, t2, L21\n"
				+ "\n"
				+ "L20:			# 20: jump, _, _, 23\n"
				+ "	j L23\n"
				+ "\n"
				+ "L21:			# 21: out, _, _, 0\n"
				+ "	li a0, 0\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "#L22:			# 22: jump, _, _, _\n"
				+ "\n"
				+ "L23:			# 23: +, i, 1, &4\n"
				+ "	lw t1, -12(sp)\n"
				+ "	li t2, 1\n"
				+ "	add t1, t1, t2\n"
				+ "	sw t1, -24(sp)\n"
				+ "\n"
				+ "L24:			# 24: :=, &4, _, i\n"
				+ "	lw t0, -24(sp)\n"
				+ "	sw t0, -12(sp)\n"
				+ "\n"
				+ "L25:			# 25: jump, _, _, 13\n"
				+ "	j L13\n"
				+ "\n"
				+ "L26:			# 26: out, _, _, 1\n"
				+ "	li a0, 1\n"
				+ "	li a7, 1\n"
				+ "	ecall\n"
				+ "	la a0, str_nl\n"
				+ "	li a7, 4\n"
				+ "	ecall\n"
				+ "\n"
				+ "L27:			# 27: end_block, main_isPrime, _, _\n"
				+ "	lw ra, (sp)\n"
				+ "	jr ra\n"
				+ "\n"
				+ "main:			# 28: begin_block, main, _, _\n"
				+ "	addi sp, sp, 12\n"
				+ "	mv gp, sp\n"
				+ "\n"
				+ "L29:			# 29: call, main_isPrime, _, _\n"
				+ "	addi fp, sp, 28\n"
				+ "	sw sp, -4(fp)\n"
				+ "	addi sp, sp, 28\n"
				+ "	jal main_isPrime\n"
				+ "	addi sp, sp, -28\n"
				+ "\n"
				+ "L30:			# 30: halt, _, _, _\n"
				+ "	li a0, 0\n"
				+ "	li a7, 93\n"
				+ "	ecall\n"
				+ "\n"
				+ "L31:			# 31: end_block, main, _, _\n"
				+ "";
		
		Assertions.assertEquals(expectedFinalCode, finManager.getFinalCode());
		
	}
	
}
