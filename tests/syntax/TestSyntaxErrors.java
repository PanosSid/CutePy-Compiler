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
		Assertions.assertEquals("[Error in line " +3+ "] there must be at least one simple or structured statement ", thrown.getMessage());	
		
	}
}
