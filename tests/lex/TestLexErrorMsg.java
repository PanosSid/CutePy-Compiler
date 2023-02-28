package lex;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TestLexErrorMsg {

	@Test
	public void testLetterAfterNumberThrowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" 12345a \n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line"+1+"] found letter after character on line: 1", thrown.getMessage());	
	}
	
	@Test
	public void testNumberOutOfBoundsThrowsException1() throws Exception {
		FileReader reader = new FileReader();
		reader.setFileContents(" 4294967296 \n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line"+1+"] found integer with value outside of the limits", thrown.getMessage());	
	}
	
	@Test
	public void testUnclosedCommentThrowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" #$ : 547 \n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line 1] comment is not closed!!", thrown.getMessage());	
	}
	
	@Test
	public void testUnclossedCommentThatContainsHashTag() {
		FileReader reader = new FileReader();
		reader.setFileContents(""
				+ "#$ body of \r\n"
				+ "	primes2 # panos $\r\n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line 2] comment is not closed!!", thrown.getMessage());	
	}
	
	@Test
	public void testDivOperatorThowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" /4");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line "+1+"]: Char after division char '/' is "+4+" not '/'. The division operator is '//'.", thrown.getMessage());	
	}
	
	@Test
	public void testRelOpSmallerThrowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" << ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line "+1+"]: found '<<' must be < or <> or <=", thrown.getMessage());	
	}
	
	@Test
	public void testRelOpLargerThrowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" >> ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line "+1+"]: found '>>' must be > or >=", thrown.getMessage());	
	}
	
	@Test
	public void testEqualsThrowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" =_ ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line"+1+"]: found '='. Relation operator is '==', ...>", thrown.getMessage());	
	}
	
	@Test
	public void testEqualsWithSpaceThrowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" = ! ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line "+1+"] found '=whitespace and a char that is not a digit or letter found '= !'", thrown.getMessage());	
	}

	@Test
	public void testSharpTokenThrowsException() {
		FileReader reader = new FileReader();
		reader.setFileContents(" #panos ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line "+1+"] expected '#declare' or '#{' or '#}' or '#$' but found '#p' ", thrown.getMessage());	
	}
	
	@Test
	public void testGetTokenAfterRecognisingConsecutiveChars() {
		FileReader reader = new FileReader();
		reader.setFileContents(" __namEK__ ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error: at line "+1+"] expected '__name__' but found '__namE' ", thrown.getMessage());	
	}
	
	@Test
	public void testUnknownCharacter() {
		FileReader reader = new FileReader();
		reader.setFileContents(" @&&");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
		Assertions.assertEquals("[Error at line:"+1+"] found char '@' that doesnt belong in the CutePy alphabet", thrown.getMessage());	
	}
	
//	@Test
//	public void testIdentifierOrKeyword() {
//		FileReader reader = new FileReader();
//		reader.setFileContents(" panos_sid@");
//		LexAnalyser lex = new LexAnalyser(reader);
//		Exception thrown = assertThrows(Exception.class, () -> lex.getToken());
//		Assertions.assertEquals("[Error at line:"+1+"] found char '@' that doesnt belong in the CutePy alphabet", thrown.getMessage());	
//	}
	
}
