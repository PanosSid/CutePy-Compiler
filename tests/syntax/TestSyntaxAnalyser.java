package syntax;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

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
		Token expectedCurrentTk = null;
		Token expectedPrevTk = new Token(";", "delimiter", 1);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
}
