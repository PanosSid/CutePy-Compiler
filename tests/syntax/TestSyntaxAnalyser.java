package syntax;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import lex.EOFToken;
import lex.FileReader;
import lex.LexAnalyser;
import lex.Token;

public class TestSyntaxAnalyser {
	
	private SyntaxAnalyser syntax;
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		syntax = new SyntaxAnalyser(lex);
	}

	
	
	@Test
	public void testMainFunctionCall() throws Exception {
		setUpSyntaxAnalyser(" main_func();");
//		syntax.setCurrentToken();
		syntax.mainFunctionCall();
		Token expectedCurrentTk = new EOFToken(1);
		Token expectedPrevTk = new Token(";", "delimiter", 1);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	@Test
	public void testDefMainFunction() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def mainFunc1():\n"
				+ "#{ statement1\n"
				+ "#}\n"
				+ "def mainFunc2():\n"
				+ "#{ statement2\n"
				+ "#}\n");
//		syntax.setCurrentToken();
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(7);
		Token expectedPrevTk = new Token("#}", "groupOperator", 6);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	@Test
	public void testMainFunctionWithoutSubFuncs() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def mainFunc1():\n"
				+ "#{\n"
				+ "\t#declare x1\n"
				+ "\t#declare y1, y2\n"
				+ "\tx1 = y1 + y2\n"
				+ "\tprint(x1);\n"
				+ "\treturn(-x1);\n"
				+ "#}\n");
//		syntax.setCurrentToken();
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(9);
		Token expectedPrevTk = new Token("#}", "groupOperator", 8);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
//	@Ignore
	@Test
	public void testMain_CountDigits() throws Exception {
		setUpSyntaxAnalyser(""
				+"def main_countdigits():\r\n"
				+ "#{\r\n"
				+ "	#declare x, count\r\n"
				+ "	x = int(input());\r\n"
				+ "	count = 0;\r\n"
				+ "	while (x>0):\r\n"
				+ "	#{\r\n"
				+ "		x = x // 10;\r\n"
				+ "		count = count + 1;\r\n"
				+ "	#}\r\n"
				+ "	print(count);\r\n"
				+ "#}\n");
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(13);
		Token expectedPrevTk = new Token("#}", "groupOperator", 12);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	
	@Test
	public void testUnitWithoutWhile() throws Exception {
		setUpSyntaxAnalyser(""
				+"def main_countdigits():\r\n"
				+ "#{\r\n"
				+ "	#declare x, count\r\n"
				+ "	x = int(input());\r\n"
				+ "	count = 0;\r\n"
				+ "		z = y // 10;\r\n"
				+ "		count = count + 1;\r\n"
				+ "	print(count);\r\n"
				+ "#}\n");
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(10);
		Token expectedPrevTk = new Token("#}", "groupOperator", 9);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	@Test
	public void testOnlyWhile() throws Exception {
		setUpSyntaxAnalyser(""
				+"def main_countdigits():\r\n"
				+ "#{\r\n"
				+ "	while (x>0):\r\n"
				+ "	#{\r\n"
				+ "		x = x // 10;\r\n"
				+ "		count = count + 1;\r\n"
				+ "	#}\r\n"
				+ "#}\n");
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(9);
		Token expectedPrevTk = new Token("#}", "groupOperator", 8);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	@Test
	public void testFactorial() throws Exception {
		setUpSyntaxAnalyser(""
				+"def main_factorial():\r\n"
				+ "#{\r\n"
				+ "	#$ declarations #$\r\n"
				+ "	#declare x\r\n"
				+ "	#declare i,fact\r\n"
				+ "\r\n"
				+ "	#$ body of main_factorial #$\r\n"
				+ "	x = int(input());\r\n"
				+ "	fact = 1;\r\n"
				+ "	i = 1;\r\n"
				+ "	while (i<=x):\r\n"
				+ "	#{\r\n"
				+ "		fact = fact * i;\r\n"
				+ "		i = i + 1;\r\n"
				+ "	#}\r\n"
				+ "	print(fact);\r\n"
				+ "\r\n"
				+ "#}\n"
				+ "\r\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "#$ call of main functions #$\r\n"
				+ "main_factorial();\r\n"
				+ "\r\n");
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(19);
		Token expectedPrevTk = new Token("#}", "groupOperator", 18);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	
	
}
