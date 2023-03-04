package syntax;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import exceptions.CutePyException;
import lex.FileReader;
import lex.LexAnalyser;

public class TestSyntaxErrors {
	private SyntaxAnalyser syntax;
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		syntax = new SyntaxAnalyser(lex);
	}
	
	@Test
	public void testEmptyFunction() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def emptyFunc():\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t emptyFunc();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected 'at least one simple or structured statement' but found '#}'", thrown.getMessage());	
		
	}
	
	@Test
	public void testDefMainFuncThrowsException() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def mainFunc(:\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t emptyFunc();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 1] expected ')' but found ':'", thrown.getMessage());	
		
	}
	
	@Test
	public void testDefMainFuncThrowsException2() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def mainFunc():\n"
				+ "#{\n"
				+ "	  print(1);\n"
				+ "def mainFunc2():\n"
				+ "#{\n"
				+ "	  print(1);\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t emptyFunc();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 4] expected '#}' but found 'def'", thrown.getMessage());	
	}
	
	@Test
	public void testDefMainFuncThrowsException3() throws Exception {
		setUpSyntaxAnalyser(""
				+ " mainFunc(:\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t emptyFunc();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 1] expected 'def' but found 'mainFunc'", thrown.getMessage());	
		
	}
	
	@Test
	public void testDefMainFuncThrowsException4() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def 253():\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t emptyFunc();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 1] expected '<identifier>' but found '253'", thrown.getMessage());	
		
	}
	
	@Test
	public void testDefFuncThrowsException() throws Exception {
		setUpSyntaxAnalyser("def main_fibonacci():\r\n"
				+ "#{\r\n"
				+ "	#declare x\r\n"
				+ "\r\n"
				+ "	def fibonacci(x) \r\n"
				+ "	#{\r\n"
				+ "		if (x<=1):\r\n"
				+ "			return(x);\r\n"
				+ "		else:\r\n"
				+ "			return (fibonacci(x-1)+fibonacci(x-2));\r\n"
				+ "	#}\r\n"
				+ "\r\n"
				+ "	x = int(input());\r\n"
				+ "	print(fibonacci(x));\r\n"
				+ "\r\n"
				+ "#}"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 6] expected ':' but found '#{'", thrown.getMessage());	
	}
	
	@Test
	public void testDefFuncThrowsException2() throws Exception {
		setUpSyntaxAnalyser("def main_fibonacci():\r\n"
				+ "#{\r\n"
				+ "	#declare x\r\n"
				+ "\r\n"
				+ "	def fibonacci(x) \r\n"
				+ "	#{\r\n"
				+ "		if (x<=1):\r\n"
				+ "			return(x);\r\n"
				+ "		else:\r\n"
				+ "			return (fibonacci(x-1)+fibonacci(x-2));\r\n"
				+ "\r\n"
				+ "	x = int(input());\r\n"
				+ "	print(fibonacci(x));\r\n"
				+ "\r\n"
				+ "#}"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 6] expected ':' but found '#{'", thrown.getMessage());	
	}
	
//	@Test
//	public void testDefFuncThrowsException3() throws Exception {
//		setUpSyntaxAnalyser("def main_fibonacci():\r\n"
//				+ "#{\r\n"
//				+ "	#declare x\r\n"
//				+ "\r\n"
//				+ "	def fibonacci(x):\r\n"
//				+ "	#{\r\n"
//				+ "		if (x<=1):\r\n"
//				+ "			return(x);\r\n"
//				+ "		else:\r\n"
//				+ "			return (fibonacci(x-1)+fibonacci(x-2));\r\n"
//				+ "\r\n"
//				+ "	x = int(input());\r\n"
//				+ "	print(fibonacci(x));\r\n"
//				+ "\r\n"
//				+ "print(1);\r\n"
//				+ "#}"
//				+ "if __name__ == \"__main__\":\r\n"
//				+ "\t emptyFunc();\r\n"
//				
//				);	
//		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
//		Assertions.assertEquals("[Error in line 6] expected ':' but found '#{'", thrown.getMessage());	
//	}
	
	
	@Test
	public void testStatementThrowsException() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def emptyFunc():\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t emptyFunc();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected 'at least one simple or structured statement' but found '#}'", thrown.getMessage());	
	}
	
//	@Test
//	public void testSimpleStatementThrowsException() throws Exception {
//		setUpSyntaxAnalyser(""
//				+ " def emptyFunc():\n"
//				+ "#{\n"
//				+ "# 222\n"
//				+ "#}\n"
//				+ "if __name__ == \"__main__\":\r\n"
//				+ "\t emptyFunc();\r\n"
//				);	
//		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
//		Assertions.assertEquals("[Error in line 3] expected 'at least one simple or structured statement' but found '#}'", thrown.getMessage());	
//	}
	
	
	@Test
	public void testBug() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def main_factorial():\r\n"
				+ "#{\r\n"
				+ "	#$ declarations #$\r\n"
				+ "	#declare x\r\n"
				+ "	#declare i,fact\r\n"
				+ "\r\n"
				+ "	#$ body of main_factorial #$\r\n"
				+ " if x>1:\n"
				+ "	x = int(input());\r\n"
				+ "	fact = 1;\r\n"
				+ "	i = 1;\r\n"
				+ "\r\n"
				+ "#}"
				+ "\r\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "#$ call of main functions #$\r\n"
				+ "	main_primes();\r\n"
				+ "	"
				);
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line " +8+ "] expected '(' but found 'x'", thrown.getMessage());	
		
	}
}
