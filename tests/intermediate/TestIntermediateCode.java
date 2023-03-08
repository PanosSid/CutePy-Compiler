package intermediate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import lex.Token;
import syntax.SyntaxAnalyser;

public class TestIntermediateCode {
	private SyntaxAnalyser syntax;
	private QuadManager quadManager;
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		quadManager = new QuadManager();
		syntax = new SyntaxAnalyser(lex, quadManager);
	}
	
	@Test
	public void testArithmeticForIntegers() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_arithmetic():\n"
				+ "#{\n"
				+ " 	c = 1 + 2;\n"
				+ "#}\n"
				+ "if __name__ == \"__main__\":\n"
				+ "\t main_arithmetic();\r\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: +, 1, 2, T_1\n"
				+ "102: :=, T_1, _, c\n"
				+ "103: end_block, main_arithmetic, _, _\n"
				+ "104: call, main_arithmetic, _, _\n"
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
				+ "\t main_arithmetic();\r\n"
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
				+ "107: call, main_arithmetic, _, _\n"
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
				+ "\t main_arithmetic();\r\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: ret, _, _, a\n"
				+ "102: end_block, main_arithmetic, _, _\n"
				+ "103: call, main_arithmetic, _, _\n"
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
				+ "\t main_arithmetic();\r\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: +, c, d, T_1\n"
				+ "102: +, T_1, 1, T_2\n"
				+ "103: *, b, T_2, T_3\n"
				+ "104: +, a, T_3, T_4\n"
				+ "105: :=, T_4, _, c\n"
				+ "106: ret, _, _, c\n"
				+ "107: end_block, main_arithmetic, _, _\n"
				+ "108: call, main_arithmetic, _, _\n"
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
				+ "\t main_arithmetic();\r\n"
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
				+ "108: call, main_arithmetic, _, _\n"
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
				+ "\t main_arithmetic();\r\n"
				);
		syntax.analyzeSyntax();
		String expectedIntermedCode = ""
				+ "100: begin_block, main_arithmetic, _, _\n"
				+ "101: in, x, _, _\n"
				+ "102: end_block, main_arithmetic, _, _\n"
				+ "103: call, main_arithmetic, _, _\n"
				;
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testConditionIntermed() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ " >b and c>d or e>f \n"
				);
//		syntax.analyzeSyntax();
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
	
}