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
	public void testRecognizeSimpleNumber() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents("123");
		LexAnalyser lex = new LexAnalyser(reader);
		Assertions.assertEquals(new Token("123", "number", 1), lex.getToken());	

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
	

//	@Test
//	public void testRecognizeLetterAfterNumberException() throws Exception {
//		FileReader reader = new FileReader();
//		reader.setFileContents(" 14a");
//		LexAnalyser lex = new LexAnalyser(reader);
//		Assertions.assertThrows(null, null)
//		Assertions.assertEquals(new Token("-14", "number", 1), lex.getToken());	
//		
//	}
	
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
	
	
	@Test
	public void testRecognizeKeyword() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents("def");
		LexAnalyser lex = new LexAnalyser(reader);
		Assertions.assertEquals(new Token("def", "keyword", 1), lex.getToken());	
	}
	
	@Test
	public void testRecognizeIdentifier() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents("divides");
		LexAnalyser lex = new LexAnalyser(reader);
		Assertions.assertEquals(new Token("divides", "identifier", 1), lex.getToken());	
	}
	
	@Test
	public void testRecognizeMultipleTokens() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents(
				"def adds(x1,y1):\n"
				+"\ta = x1 + 1\n");
		List<Token> expectedTokens = Arrays.asList(new Token[] {
				new Token("def", "keyword", 1), 
				new Token("adds", "identifier", 1),
				new Token("(", "groupOperator", 1),
				new Token("x1", "identifier", 1),
				new Token(",", "delimiter", 1),
				new Token("y1", "identifier", 1),
				new Token(")", "groupOperator", 1),
				new Token(":", "delimiter", 1),
				new Token("a", "identifier", 2),
				new Token("=", "assignment", 2),
				new Token("x1", "identifier", 2),
				new Token("+", "addOperator", 2),
				new Token("1", "number", 2),
				});
		
		LexAnalyser lex = new LexAnalyser(reader);
		List<Token> actualTokens =Arrays.asList(new Token[] {
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken()});
		compareListOfTokens(expectedTokens, actualTokens);
	}
	
	@Test
	public void testRecognizeWithComments() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents(
				"def adds(x1,y1):\n"
				+"\t#$a = x1 + 1#$\n"
				+"\ty1 = x1 * 100\r\n");
		List<Token> expectedTokens = Arrays.asList(new Token[] {
				new Token("def", "keyword", 1), 
				new Token("adds", "identifier", 1),
				new Token("(", "groupOperator", 1),
				new Token("x1", "identifier", 1),
				new Token(",", "delimiter", 1),
				new Token("y1", "identifier", 1),
				new Token(")", "groupOperator", 1),
				new Token(":", "delimiter", 1),
				new Token("y1", "identifier", 3),
				new Token("=", "assignment", 3),
				new Token("x1", "identifier", 3),
				new Token("*", "mulOperator", 3),
				new Token("100", "number", 3),
				});
		
		LexAnalyser lex = new LexAnalyser(reader);
		List<Token> actualTokens =Arrays.asList(new Token[] {
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken()});
		compareListOfTokens(expectedTokens, actualTokens);
	}
	
	@Test
	public void testRecognizeWithCommentsBigTest() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents(
				"def add2PosNums(x1,y1):\r\n"
				+ "\r\n"
				+ "	#$ panos sidiropoulos\r\n"
				+ "yolo no identation \r\n"
				+ "		#$\r\n"
				+ "	if [x1 < 0 or y1 < 0]:\r\n"
				+ "		return 0\r\n"
				+ "	else:\r\n"
				+ "		return x1\r\n");
		List<Token> expectedTokens = Arrays.asList(new Token[] {
				new Token("def", "keyword", 1), 
				new Token("add2PosNums", "identifier", 1),
				new Token("(", "groupOperator", 1),
				new Token("x1", "identifier", 1),
				new Token(",", "delimiter", 1),
				new Token("y1", "identifier", 1),
				new Token(")", "groupOperator", 1),
				new Token(":", "delimiter", 1),
				
				new Token("if", "keyword", 6),
				new Token("[", "groupOperator", 6),
				new Token("x1", "identifier", 6),
				new Token("<", "relOperator", 6),
				new Token("0", "number", 6),
				new Token("or", "keyword", 6),
				new Token("y1", "identifier", 6),
				new Token("<", "relOperator", 6),
				new Token("0", "number", 6),
				new Token("]", "groupOperator", 6),
				new Token(":", "delimiter", 6),
				
				new Token("return", "keyword", 7),
				new Token("0", "number", 7),
				
				new Token("else", "keyword", 8),
				new Token(":", "delimiter", 8),
				new Token("return", "keyword", 9),
				new Token("x1", "identifier", 9),
				});
		
		LexAnalyser lex = new LexAnalyser(reader);
		List<Token> actualTokens =Arrays.asList(new Token[] {
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(),
				lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken(), lex.getToken()}
		);
		compareListOfTokens(expectedTokens, actualTokens);
	}

}
