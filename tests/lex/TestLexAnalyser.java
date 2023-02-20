package lex;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class TestLexAnalyser {
	
	@Test
	public void testRecognizeNumber() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents(" \t 123 4 \n56	\r\n\t78 \n910");
		LexAnalyser lex = new LexAnalyser(reader);
		List<Token> expectedTokens = Arrays.asList(new Token[] {
				new Token("123", "number", 1), 
				new Token("4", "number", 1),
				new Token("56", "number", 2),
				new Token("78", "number", 3),
				new Token("910", "number", 4),
				});
		List<Token> actualTokens =Arrays.asList(new Token[] {lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken()});
		compareListOfTokens(expectedTokens, actualTokens);
	}

	private void compareListOfTokens(List<Token> expectedTokens, List<Token> actualTokens) {
		for (int i = 0; i < expectedTokens.size(); i++) {
			Assertions.assertEquals(expectedTokens.get(i), actualTokens.get(i));			
		}		
	}
	
	@Test
	public void testRecognizeNumberBug() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents("\n56");
		LexAnalyser lex = new LexAnalyser(reader);
		Assertions.assertEquals(new Token("56", "number", 2), lex.getToken());	

	}
	
	@Test
	public void testRecognizeNumberBug2() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents("\r\n\t78");
		LexAnalyser lex = new LexAnalyser(reader);
		Assertions.assertEquals(new Token("78", "number", 2), lex.getToken());	
		
	}
	
	@Test
	public void testReadNextWithNewLines() {
		FileReader reader = new FileReader();
		LexAnalyser lex = new LexAnalyser(reader);
		Assertions.assertTrue(!lex.isNewLine('5'));
		Assertions.assertTrue(lex.isNewLine('\n'));
		
		reader.setFileContents("\r5");
		Assertions.assertTrue(!lex.isNewLine('\r'));
		
		reader.setFileContents("\r\n");
		reader.setFilePointer(1);
		Assertions.assertTrue(lex.isNewLine('\r'));
		
	}
	

	
}
