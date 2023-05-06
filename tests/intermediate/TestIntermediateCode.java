package intermediate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import lex.Token;
import symboltable.SymbolTable;
import syntax.FakeFinalCodeManager;
import syntax.SyntaxAnalyser;

public class TestIntermediateCode {
	private SyntaxAnalyser syntax;
	private QuadManager quadManager;
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		quadManager = new QuadManager();
		SymbolTable symb = new SymbolTable();
		syntax = new SyntaxAnalyser(lex, quadManager, symb, new FakeFinalCodeManager());
	}
	
	@Test
	public void testArithmeticForIntegers() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	c = 1 + 2;\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: +, 1, 2, T_1\n"
				+ "102: :=, T_1, _, c\n"
				+ "103: end_block, main_arithmetic, _, _\n"
				+ "104: begin_block, main, _, _\n"
				+ "105: call, main_arithmetic, _, _\n"
				+ "106: halt, _, _, _\n"
				+ "107: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testArithmetic2() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	c = a+b*(c+d+1);\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: +, c, d, T_1\n"
				+ "102: +, T_1, 1, T_2\n"
				+ "103: *, b, T_2, T_3\n"
				+ "104: +, a, T_3, T_4\n"
				+ "105: :=, T_4, _, c\n"
				+ "106: end_block, main_arithmetic, _, _\n"
				+ "107: begin_block, main, _, _\n"
				+ "108: call, main_arithmetic, _, _\n"
				+ "109: halt, _, _, _\n"
				+ "110: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testReturnIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	return(a);\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: ret, a, _, _\n"
				+ "102: end_block, main_arithmetic, _, _\n"
				+ "103: begin_block, main, _, _\n"
				+ "104: call, main_arithmetic, _, _\n"
				+ "105: halt, _, _, _\n"
				+ "106: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testReturnIntermed2() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	c = a+b*(c+d+1);\n"			
				+ " 	return(c);\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: +, c, d, T_1\n"
				+ "102: +, T_1, 1, T_2\n"
				+ "103: *, b, T_2, T_3\n"
				+ "104: +, a, T_3, T_4\n"
				+ "105: :=, T_4, _, c\n"
				+ "106: ret, c, _, _\n"
				+ "107: end_block, main_arithmetic, _, _\n"
				+ "108: begin_block, main, _, _\n"
				+ "109: call, main_arithmetic, _, _\n"
				+ "110: halt, _, _, _\n"
				+ "111: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testPrintIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	c = a+b*(c+d+1);\n"			
				+ " 	print(c);\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: +, c, d, T_1\n"
				+ "102: +, T_1, 1, T_2\n"
				+ "103: *, b, T_2, T_3\n"
				+ "104: +, a, T_3, T_4\n"
				+ "105: :=, T_4, _, c\n"
				+ "106: out, _, _, c\n"
				+ "107: end_block, main_arithmetic, _, _\n"
				+ "108: begin_block, main, _, _\n"
				+ "109: call, main_arithmetic, _, _\n"
				+ "110: halt, _, _, _\n"
				+ "111: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testInputIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"			
				+ " 	x = int(input());\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: in, x, _, _\n"
				+ "102: end_block, main_arithmetic, _, _\n"
				+ "103: begin_block, main, _, _\n"
				+ "104: call, main_arithmetic, _, _\n"
				+ "105: halt, _, _, _\n"
				+ "106: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testConditionIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ " >b and c>d or e>f \n"
				);
		syntax.setCurrentToken(new Token("a", "identifier", 1));
		syntax.condition();
		String expectedIntermedCode = ""
				+ "100: >, a, b, 102\n"
				+ "101: jump, _, _, 104\n"
				+ "102: >, c, d, _\n"
				+ "103: jump, _, _, 104\n"
				+ "104: >, e, f, _\n"
				+ "105: jump, _, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testConditionIntermed2() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ " >b or a>c and [b>c or a>1] and b!=1 \n"
				);
		syntax.setCurrentToken(new Token("a", "identifier", 1));
		syntax.condition();
		String expectedIntermedCode = ""
				+ "100: >, a, b, _\n"
				+ "101: jump, _, _, 102\n"
				+ "102: >, a, c, 104\n"
				+ "103: jump, _, _, _\n"
				+ "104: >, b, c, 108\n"
				+ "105: jump, _, _, 106\n"
				+ "106: >, a, 1, 108\n"
				+ "107: jump, _, _, _\n"
				+ "108: !=, b, 1, _\n"
				+ "109: jump, _, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testWhileIntermed1() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ /*while*/ " (a>b):\n"	
				+ " 	b=b+1; \n"
				);
		syntax.setCurrentToken(new Token("while", "keyword", 1));
		syntax.structuredStatement();
		String expectedIntermedCode = ""
				+ "100: >, a, b, 102\n"
				+ "101: jump, _, _, 105\n"
				+ "102: +, b, 1, T_1\n"
				+ "103: :=, T_1, _, b\n"
				+ "104: jump, _, _, 100\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testWhileIntermed2() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ /*while*/ " (a>b):\n"
				+ "#{\n"	
				+ " 	b=b+1; \n"
				+ "#}\n"
				);
		syntax.setCurrentToken(new Token("while", "keyword", 1));
		syntax.structuredStatement();
		String expectedIntermedCode = ""
				+ "100: >, a, b, 102\n"
				+ "101: jump, _, _, 105\n"
				+ "102: +, b, 1, T_1\n"
				+ "103: :=, T_1, _, b\n"
				+ "104: jump, _, _, 100\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testifIntermed1() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ /*if*/"(a>b):\n"
				+ " 	b=b+1;\n"
				+ "else:\n"
				+ " 	b=b-2;\n"
				);
		syntax.setCurrentToken(new Token("if", "keyword", 1));
		syntax.structuredStatement();
		String expectedIntermedCode = ""
				+ "100: >, a, b, 102\n"
				+ "101: jump, _, _, 105\n"
				+ "102: +, b, 1, T_1\n"
				+ "103: :=, T_1, _, b\n"
				+ "104: jump, _, _, 107\n"
				+ "105: -, b, 2, T_2\n"
				+ "106: :=, T_2, _, b\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testifIntermed2() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ /*if*/"(a>b):\n"
				+ "#{\n"
				+ " 	b=b+1;\n"
				+ "#}\n"
				+ "else:\n"
				+ "#{\n"
				+ " 	b=b-2;\n"
				+ "#}\n"
				);
		syntax.setCurrentToken(new Token("if", "keyword", 1));
		syntax.structuredStatement();
		String expectedIntermedCode = ""
				+ "100: >, a, b, 102\n"
				+ "101: jump, _, _, 105\n"
				+ "102: +, b, 1, T_1\n"
				+ "103: :=, T_1, _, b\n"
				+ "104: jump, _, _, 107\n"
				+ "105: -, b, 2, T_2\n"
				+ "106: :=, T_2, _, b\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	

	@Test
	public void testifIntermedWithoutElse() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ /*if*/"(a>b):\n"
				+ "#{\n"
				+ " 	b=b+1;\n"
				+ "#}\n"
				+ "print(1);\n"
				);
		syntax.setCurrentToken(new Token("if", "keyword", 1));
		syntax.statements();
		String expectedIntermedCode = ""
				+ "100: >, a, b, 102\n"
				+ "101: jump, _, _, 105\n"
				+ "102: +, b, 1, T_1\n"
				+ "103: :=, T_1, _, b\n"				
				+ "104: jump, _, _, _\n" 		// TODO check if it is appropiate to leave an empty jump
				+ "105: out, _, _, 1\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testCallFuncIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	x = 1 + func(x, y);"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: par, x, cv, _\n"
				+ "102: par, y, cv, _\n"
				+ "103: par, T_1, ret, _\n"
				+ "104: call, func, _, _\n"
				+ "105: +, 1, T_1, T_2\n"
				+ "106: :=, T_2, _, x\n"
				+ "107: end_block, main_arithmetic, _, _\n"
				+ "108: begin_block, main, _, _\n"
				+ "109: call, main_arithmetic, _, _\n"
				+ "110: halt, _, _, _\n"
				+ "111: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testCallFuncIntermed2() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	x = max (max(a, b), max(c, d));"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: par, a, cv, _\n"
				+ "102: par, b, cv, _\n"
				+ "103: par, T_1, ret, _\n"
				+ "104: call, max, _, _\n"
				+ "105: par, c, cv, _\n"
				+ "106: par, d, cv, _\n"
				+ "107: par, T_2, ret, _\n"
				+ "108: call, max, _, _\n"
				+ "109: par, T_1, cv, _\n"
				+ "110: par, T_2, cv, _\n"
				+ "111: par, T_3, ret, _\n"
				+ "112: call, max, _, _\n"
				+ "113: :=, T_3, _, x\n"
				+ "114: end_block, main_arithmetic, _, _\n"
				+ "115: begin_block, main, _, _\n"
				+ "116: call, main_arithmetic, _, _\n"
				+ "117: halt, _, _, _\n"
				+ "118: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	
	@Test
	public void testSmallFuncIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_small():\n"
				+ "#{\n"
				+ "	#$ const  =1;	??? #$\n"
				+ "	#declare b,g,f\n"
				+ "\n"
				+ "	def P1(X, Y):\n"
				+ "	#{\n"
				+ "		#declare e,f\n"
				+ "\n"
				+ "		def P11(X):\n"
				+ "		#{\n"
				+ "			#declare e\n"
				+ "			e = A;\n"
				+ "			X = Y;\n"
				+ "			f = b;\n"
				+ "			return(e);\n"
				+ "		#}\n"
				+ "\n"
				+ "		#$ code for P1 #$\n"
				+ "		b = X;\n"
				+ "		e = P11(X);\n"
				+ "		e = P1(X,Y);\n"
				+ "		X = b;\n"
				+ "		return(e);\n"
				+ "\n"
				+ "	#}\n"
				+ "\n"
				+ "	#$ code for main #$\n"
				+ "	if (b>1 and f<2 or g+1<f+b):\n"
				+ "	#{\n"
				+ "		f = P1( g);\n"
				+ "	#}\n"
				+ "	else:\n"
				+ "	#{\n"
				+ "		f = 1;\n"
				+ "	#}\n"
				+ "\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "#$ call of main functions #$\n"
				+ "	main_small();\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, P11, _, _\n"
				+ "101: :=, A, _, e\n"
				+ "102: :=, Y, _, X\n"
				+ "103: :=, b, _, f\n"
				+ "104: ret, e, _, _\n"
				+ "105: end_block, P11, _, _\n"
				+ "106: begin_block, P1, _, _\n"
				+ "107: :=, X, _, b\n"
				+ "108: par, X, cv, _\n"
				+ "109: par, T_1, ret, _\n"
				+ "110: call, P11, _, _\n"
				+ "111: :=, T_1, _, e\n"
				+ "112: par, X, cv, _\n"
				+ "113: par, Y, cv, _\n"
				+ "114: par, T_2, ret, _\n"
				+ "115: call, P1, _, _\n"
				+ "116: :=, T_2, _, e\n"
				+ "117: :=, b, _, X\n"
				+ "118: ret, e, _, _\n"
				+ "119: end_block, P1, _, _\n"
				+ "120: begin_block, main_small, _, _\n"
				+ "121: >, b, 1, 123\n"
				+ "122: jump, _, _, 125\n"
				+ "123: <, f, 2, 129\n"
				+ "124: jump, _, _, 125\n"
				+ "125: +, g, 1, T_3\n"
				+ "126: +, f, b, T_4\n"
				+ "127: <, T_3, T_4, 129\n"
				+ "128: jump, _, _, 134\n"
				+ "129: par, g, cv, _\n"
				+ "130: par, T_5, ret, _\n"
				+ "131: call, P1, _, _\n"
				+ "132: :=, T_5, _, f\n"
				+ "133: jump, _, _, 135\n"
				+ "134: :=, 1, _, f\n"
				+ "135: end_block, main_small, _, _\n"
				+ "136: begin_block, main, _, _\n"
				+ "137: call, main_small, _, _\n"
				+ "138: halt, _, _, _\n"
				+ "139: end_block, main, _, _\n"
				;

		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testIfWhileFuncIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_ifWhile():\n"
				+ "#{\n"
				+ "	#declare c,a,b,t	\n"
				+ "	a=1;\n"
				+ "	while (a+b<1 and b<5):\n"
				+ "	#{\n"
				+ "		if (t==1):\n"
				+ "			c=2;\n"
				+ "		else:\n"
				+ "			if (t==2):\n"
				+ "				c=4;\n"
				+ "			else:\n"
				+ "				c=0;\n"
				+ "		while (a<1):\n"
				+ "			if (a==2):\n"
				+ "				while(b==1):\n"
				+ "					c=2;\n"
				+ "	#}\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_ifWhile();	\n"
				+ ""
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = "100: begin_block, main_ifWhile, _, _\n"
				+ "101: :=, 1, _, a\n"
				+ "102: +, a, b, T_1\n"
				+ "103: <, T_1, 1, 105\n"
				+ "104: jump, _, _, 127\n"
				+ "105: <, b, 5, 107\n"
				+ "106: jump, _, _, 127\n"
				+ "107: ==, t, 1, 109\n"
				+ "108: jump, _, _, 111\n"
				+ "109: :=, 2, _, c\n"
				+ "110: jump, _, _, 116\n"
				+ "111: ==, t, 2, 113\n"
				+ "112: jump, _, _, 115\n"
				+ "113: :=, 4, _, c\n"
				+ "114: jump, _, _, 116\n"
				+ "115: :=, 0, _, c\n"
				+ "116: <, a, 1, 118\n"
				+ "117: jump, _, _, 126\n"
				+ "118: ==, a, 2, 120\n"
				+ "119: jump, _, _, 125\n"
				+ "120: ==, b, 1, 122\n"
				+ "121: jump, _, _, 124\n"
				+ "122: :=, 2, _, c\n"
				+ "123: jump, _, _, 120\n"
				+ "124: jump, _, _, 125\n"
				+ "125: jump, _, _, 116\n"
				+ "126: jump, _, _, 102\n"
				+ "127: end_block, main_ifWhile, _, _\n"
				+ "128: begin_block, main, _, _\n"
				+ "129: call, main_ifWhile, _, _\n"
				+ "130: halt, _, _, _\n"
				+ "131: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testExamsFuncIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+"def main_exams():  \n"
				+ "#{  \n"
				+ "	#declare c,a,b,t\n"
				+ "	a=1; \n"
				+ "	while (a+b<1 and b<5): \n"
				+ "	#{\n"
				+ "		if (t==1):\n"
				+ "			c=2; \n"
				+ "		else :\n"
				+ "			if (t==2):"
				+ "				c=4;"
				+ "			else:\n"
				+ "				c=0; \n"
				+ "			\n"
				+ "		while (a<1): \n"
				+ "			if (a==2): \n"
				+ "				while(b==1): \n"
				+ "					c=2; \n"
				+ "	#} \n"
				+ "#} \n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_exams();"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_exams, _, _\n"
				+ "101: :=, 1, _, a\n"
				+ "102: +, a, b, T_1\n"
				+ "103: <, T_1, 1, 105\n"
				+ "104: jump, _, _, 127\n"
				+ "105: <, b, 5, 107\n"
				+ "106: jump, _, _, 127\n"
				+ "107: ==, t, 1, 109\n"
				+ "108: jump, _, _, 111\n"
				+ "109: :=, 2, _, c\n"
				+ "110: jump, _, _, 116\n"
				+ "111: ==, t, 2, 113\n"
				+ "112: jump, _, _, 115\n"
				+ "113: :=, 4, _, c\n"
				+ "114: jump, _, _, 116\n"
				+ "115: :=, 0, _, c\n"
				+ "116: <, a, 1, 118\n"
				+ "117: jump, _, _, 126\n"
				+ "118: ==, a, 2, 120\n"
				+ "119: jump, _, _, 125\n"
				+ "120: ==, b, 1, 122\n"
				+ "121: jump, _, _, 124\n"
				+ "122: :=, 2, _, c\n"
				+ "123: jump, _, _, 120\n"
				+ "124: jump, _, _, 125\n"
				+ "125: jump, _, _, 116\n"
				+ "126: jump, _, _, 102\n"
				+ "127: end_block, main_exams, _, _\n"
				+ "128: begin_block, main, _, _\n"
				+ "129: call, main_exams, _, _\n"
				+ "130: halt, _, _, _\n"
				+ "131: end_block, main, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testMaxFuncIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_max():\n"
				+ "#{\n"
				+ "	#declare a,b,c,d,e\n"
				+ "	def max(x, y):\n"
				+ "	#{\n"
				+ "		if (x>y):\n"
				+ "			return(x);\n"
				+ "		else:\n"
				+ "			return(y);\n"
				+ "	#}\n"
				+ "	e = max(max(a,b),max(c, d));\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ " 	main_max();"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, max, _, _\n"
				+ "101: >, x, y, 103\n"
				+ "102: jump, _, _, 105\n"
				+ "103: ret, x, _, _\n"
				+ "104: jump, _, _, 106\n"
				+ "105: ret, y, _, _\n"
				+ "106: end_block, max, _, _\n"
				+ "107: begin_block, main_max, _, _\n"
				+ "108: par, a, cv, _\n"
				+ "109: par, b, cv, _\n"
				+ "110: par, T_1, ret, _\n"
				+ "111: call, max, _, _\n"
				+ "112: par, c, cv, _\n"
				+ "113: par, d, cv, _\n"
				+ "114: par, T_2, ret, _\n"
				+ "115: call, max, _, _\n"
				+ "116: par, T_1, cv, _\n"
				+ "117: par, T_2, cv, _\n"
				+ "118: par, T_3, ret, _\n"
				+ "119: call, max, _, _\n"
				+ "120: :=, T_3, _, e\n"
				+ "121: end_block, main_max, _, _\n"
				+ "122: begin_block, main, _, _\n"
				+ "123: call, main_max, _, _\n"
				+ "124: halt, _, _, _\n"
				+ "125: end_block, main, _, _\n"			
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testFuncWithNegVarInCondition() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_negs():\n"
				+ "#{\n"
				+ "	#declare x, i\n"
				+ "	i = 1;\n"
				+ "	x = 2;\n"
				+ "	if (i > -x):\n"
				+ "		print(i);\n"
				+ "	print(-x);\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_negs();"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_negs, _, _\n"
				+ "101: :=, 1, _, i\n"
				+ "102: :=, 2, _, x\n"
				+ "103: >, i, -x, 105\n"
				+ "104: out, _, _, i\n"
				+ "105: out, _, _, -x\n"
				+ "108: end_block, main_negs, _, _\n"
				+ "109: begin_block, main, _, _\n"
				+ "110: call, main_negs, _, _\n"
				+ "111: halt, _, _, _\n"
				+ "112: end_block, main, _, _"			
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testAssignANegNum() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_neg_assign():\n"
				+ "#{\n"
				+ "	#declare x,y\n"
				+ "	x = -1;\n"
				+ "	y = -x;\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_neg_assign();"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_neg_assign, _, _\n"
				+ "101: :=, 1, _, x\n"
				+ "102: :=, 2, _, x\n"
				+ "103: >, i, -x, 105\n"
				+ "104: out, _, _, i\n"
				+ "105: out, _, _, -x\n"
				+ "108: end_block, main_neg_assign, _, _\n"
				+ "109: begin_block, main, _, _\n"
				+ "110: call, main_neg_assign, _, _\n"
				+ "111: halt, _, _, _\n"
				+ "112: end_block, main, _, _"			
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testDivides() throws CutePyException {
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
		String expectedIntermedCode = ""
				+ "100: begin_block, main_divides, _, _\n"
				+ "101: in, x, _, _\n"
				+ "102: in, y, _, _\n"
				+ "103: //, y, x, T_1\n"
				+ "104: *, T_1, x, T_2\n"
				+ "105: ==, y, T_2, 107\n"
				+ "106: jump, _, _, 109\n"
				+ "107: out, _, _, 1\n"
				+ "108: jump, _, _, 110\n"
				+ "109: out, _, _, 0\n"
				+ "110: end_block, main_divides, _, _\n"
				+ "111: begin_block, main, _, _\n"
				+ "112: call, main_divides, _, _\n"
				+ "113: halt, _, _, _\n"
				+ "114: end_block, main, _, _\n"			
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
}
