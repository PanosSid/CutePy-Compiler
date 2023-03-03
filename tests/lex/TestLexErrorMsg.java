package lex;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import exceptions.CutePyException;

public class TestLexErrorMsg {
	private FileReader reader = new FileReader();

	@Test
	public void testLetterAfterNumberThrowsException() {
		reader.setFileContents(" 12345a \n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line" + 1 + "] found letter after character on line: 1",
				thrown.getMessage());
	}

	@Test
	public void testNumberOutOfBoundsThrowsException1() throws Exception {
		reader.setFileContents(" 4294967296 \n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line" + 1 + "] found integer with value outside of the limits",
				thrown.getMessage());
	}

	@Test
	public void testUnclosedCommentThrowsException() {
		reader.setFileContents(" #$ : 547 \n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line 1] comment is not closed!!", thrown.getMessage());
	}

	@Test
	public void testUnclossedCommentThatContainsHashTag() {
		reader.setFileContents("" + "#$ body of \r\n" + "	primes2 # panos $\r\n");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line 2] comment is not closed!!", thrown.getMessage());
	}

	@Test
	public void testDivOperatorThowsException() {
		reader.setFileContents(" /4");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line " + 1 + "]: Char after division char '/' is " + 4
				+ " not '/'. The division operator is '//'.", thrown.getMessage());
	}

	@Test
	public void testRelOpSmallerThrowsException() {
		reader.setFileContents(" << ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line " + 1 + "]: found '<<' must be < or <=", thrown.getMessage());
	}

	@Test
	public void testRelOpLargerThrowsException() {
		reader.setFileContents(" >> ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line " + 1 + "]: found '>>' must be > or >=", thrown.getMessage());
	}

	@Test
	public void testNotEqualsThrowsException() {
		reader.setFileContents(" !panos ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line " + 1 + "]: found '!p' must be '!='", thrown.getMessage());
	}

	@Test
	public void testEqualsThrowsException() {
		reader.setFileContents(" =_ ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line" + 1 + "]: found '='. Relation operator is '==', ...>",
				thrown.getMessage());
	}

	@Test
	public void testEqualsWithSpaceThrowsException() {
		reader.setFileContents(" = ! ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals(
				"[Error in line " + 1 + "] found '=whitespace and a char that is not a digit or letter found '= !'",
				thrown.getMessage());
	}

	@Test
	public void testSharpTokenThrowsException() {
		reader.setFileContents(" #panos ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line " + 1 + "] expected '#declare' or '#{' or '#}' or '#$' but found '#p' ",
				thrown.getMessage());
	}

	@Test
	public void testGetTokenAfterRecognisingConsecutiveChars() {
		reader.setFileContents(" __namEK__ ");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error in line " + 1 + "] expected '__name__' but found '__namE' ",
				thrown.getMessage());
	}

	@Test
	public void testUnknownCharacter() {
		reader.setFileContents(" @&&");
		LexAnalyser lex = new LexAnalyser(reader);
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error at line:" + 1 + "] found char '@' that doesnt belong in the CutePy alphabet",
				thrown.getMessage());
	}

	@Test
	public void testIdentifierOrKeyword() throws CutePyException {
		reader.setFileContents(" panos_sid@");
		LexAnalyser lex = new LexAnalyser(reader);
		lex.getToken();
		Exception thrown = assertThrows(CutePyException.class, () -> lex.getToken());
		Assertions.assertEquals("[Error at line:"+1+"] found char '@' that doesnt belong in the CutePy alphabet", thrown.getMessage());	
	}

}
