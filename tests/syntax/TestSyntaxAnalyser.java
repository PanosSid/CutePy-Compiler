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
		setUpSyntaxAnalyser(""
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t mainFuncCall();\r\n"
				);
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(3);
		Token expectedPrevTk = new Token(";", "delimiter", 2);
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
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t mainFunc1();\r\n"
				);
//		syntax.setCurrentToken();
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(9);
		Token expectedPrevTk = new Token(";", "delimiter", 8);
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
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t mainFunc1();\r\n"
				);
//		syntax.setCurrentToken();
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(11);
		Token expectedPrevTk = new Token(";", "delimiter", 10);
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
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_countdigits();\r\n"
				);
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(15);
		Token expectedPrevTk = new Token(";", "delimiter", 14);
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
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_countdigits();\r\n"
				);
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(12);
		Token expectedPrevTk = new Token(";", "delimiter", 11);
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
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_countdigits();\r\n"
				);
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(11);
		Token expectedPrevTk = new Token(";", "delimiter", 10);
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
				+ "main_factorial();\r\n"
				+ "\r\n");
		syntax.analyzeSyntax();
		Token expectedCurrentTk = new EOFToken(23);
		Token expectedPrevTk = new Token(";", "delimiter", 21);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	
	
}
