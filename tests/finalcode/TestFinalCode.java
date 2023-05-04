package finalcode;

import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import symboltable.SymbolTable;
import syntax.SyntaxAnalyser;

public class TestFinalCode {
	private SyntaxAnalyser syntax;
	private FinalCodeManager finManager;
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		SymbolTable symbolTable = new SymbolTable();
		finManager = new FinalCodeManager(symbolTable);
		syntax = new SyntaxAnalyser(lex,  new QuadManager(1), symbolTable, finManager);
	}
	
	@Test
	public void testFinalCodeAssign() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_assign():\n"
				+ "#{\n"
				+ "	#declare a,b,c\n"
				+ "	a = 1;\n"
				+ "	b = 2;\n"
				+ "	c = a;\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_assign();"
		);
		syntax.analyzeSyntax();
		
		String expectedFinalCode = ""
				+ ".data\n"
				+ "\n"
				+ ".text\n"
				+ "\n"
				+ "L0:\n"
				+ "	j main\n"
				+ "";
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
				+ "L0:"
				+ "";
		
	}
	
}
