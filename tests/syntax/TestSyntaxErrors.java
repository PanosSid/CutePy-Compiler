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
	public void testmain_Function() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def main_Func():\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected 'at least one simple or structured statement' but found '#}'", thrown.getMessage());	
		
	}
	
	@Test
	public void testDefMainFuncThrowsException() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def main_Func(:\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 1] expected ')' but found ':'", thrown.getMessage());	
		
	}
	
	@Test
	public void testDefMainFuncThrowsException2() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def main_Func():\n"
				+ "#{\n"
				+ "	  print(1);\n"
				+ "def main_Func2():\n"
				+ "#{\n"
				+ "	  print(1);\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				+ "\t main_Func2();\r\n"
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
				+ "\t main_Func();\r\n"
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
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 1] expected '<identifier> that starts with 'main_'' but found '253'", thrown.getMessage());	
		
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
//				+ "\t main_Func();\r\n"
//				
//				);	
//		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
//		Assertions.assertEquals("[Error in line 6] expected ':' but found '#{'", thrown.getMessage());	
//	}
	
	
	@Test
	public void testStatementThrowsException() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected 'at least one simple or structured statement' but found '#}'", thrown.getMessage());	
	}
	
//	@Test
//	public void testSimpleStatementThrowsException() throws Exception {
//		setUpSyntaxAnalyser(""
//				+ " def main_Func():\n"
//				+ "#{\n"
//				+ "# 222\n"
//				+ "#}\n"
//				+ "if __name__ == \"__main__\":\r\n"
//				+ "\t main_Func();\r\n"
//				);	
//		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
//		Assertions.assertEquals("[Error in line 3] expected 'at least one simple or structured statement' but found '#}'", thrown.getMessage());	
//	}
	
	@Test
	public void testAssignmentStatThrowsException1() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "x + 5 "
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected '=' but found '+'", thrown.getMessage());	
	}
	
	@Test
	public void testAssignmentStatThrowsException2() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "x = #declare y "
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected '= <identifier> or <integer>' but found '= #'", thrown.getMessage());	
	}
	
	@Test
	public void testAssignmentStatThrowsException3() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "x = int(5);"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected 'input' but found '5'", thrown.getMessage());	
	}
	
	@Test
	public void testAssignmentStatThrowsException4() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "x = int(input())"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected ';' but found '#}'", thrown.getMessage());	
	}
	
	@Test
	public void testPrintStatThrowsException1() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "print); "
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected '(' but found ')'", thrown.getMessage());	
	}
	@Test
	public void testPrintStatThrowsException2() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "print(6) "
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected ';' but found '#}'", thrown.getMessage());	
	}
	
	@Test
	public void testReturnStatThrowsException1() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "return1); "
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected '=' but found ')'", thrown.getMessage());	
	}
	
	@Test
	public void testReturnStatThrowsException2() throws Exception {
		setUpSyntaxAnalyser(""
				+ " def main_Func():\n"
				+ "#{\n"
				+ "return(4+5) "
				+ "#}\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "\t main_Func();\r\n"
				);	
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("[Error in line 3] expected ';' but found '#}'", thrown.getMessage());	
	}
	
	
	
	
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
	
	@Test
	public void testLocalFunctionsReturns() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def main_primes():\r\n"
				+ "#{\r\n"
				+ "	#declare i\r\n"
				+ "\r\n"
				+ "		def isPrime(x):\r\n"
				+ "		#{\r\n"
				+ "			i = 2;\r\n"
				+ "			i = i + 2;\r\n"
				+ "			def isPrime22():"
				+ "			#{\r\n"
				+ "				k = k +1;"
				+ "				return(k);"
				+ "			#}\r\n"
				+ "		#}\r\n"
				+ "\r\n"
				+ "	i = i * 1;\r\n"
				+ "\r\n"
				+ "#}\r\n"
				+ "\r\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "#$ call of main functions #$\r\n"
				+ "	main_primes();\r\n"
				+ "	"
				);
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("Error: the local function: 'isPrime' does not have a return statement", thrown.getMessage());	
		
	}
	
	@Test
	public void testLocalFunctionsReturns2() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def main_primes():\r\n"
				+ "#{\r\n"
				+ "	#declare i\r\n"
				+ "\r\n"
				+ "	def isPrime(x):\r\n"
				+ "	#{\r\n"
				+ "		i = 2;\r\n"
				+ "		i = i + 2;\r\n"
				+ "		def isPrime22():"
				+ "		#{\r\n"
				+ " 		if (i>0):\n"
				+ "				i = i + 1;\n"
				+ "			else:\n"
				+ "				return(k);\n"
				+ "		#}\r\n"
				+ "	#}\r\n"
				+ "\r\n"
				+ "	i = i * 1;\r\n"
				+ "\r\n"
				+ "#}\r\n"
				+ "\r\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "#$ call of main functions #$\r\n"
				+ "	main_primes();\r\n"
				+ "	"
				);
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("Error: the local function: 'isPrime' does not have a return statement", thrown.getMessage());	
	}
	
	@Test
	public void testLocalFunctionsReturns3() throws Exception {
		setUpSyntaxAnalyser(""
				+ "def main_primes():\r\n"
				+ "#{\r\n"
				+ "	#declare i\r\n"
				+ "\r\n"
				+ "	def isPrime(x):\r\n"
				+ "	#{\r\n"
				+ "		i = 2;\r\n"
				+ "		i = i + 2;\r\n"
				+ "		def isPrime22():"
				+ "		#{\r\n"
				+ " 		if (i>0):\n"
				+ "				i = i + 1;\n"
				+ "		#}\r\n"
				+ "	#}\r\n"
				+ "		return(k);\n"
				+ "\r\n"
				+ "	i = i * 1;\r\n"
				+ "\r\n"
				+ "#}\r\n"
				+ "\r\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "#$ call of main functions #$\r\n"
				+ "	main_primes();\r\n"
				+ "	"
				);
		Exception thrown = assertThrows(CutePyException.class, () -> syntax.analyzeSyntax());
		Assertions.assertEquals("Error: the local function: 'isPrime' does not have a return statement", thrown.getMessage());	
	}
}
