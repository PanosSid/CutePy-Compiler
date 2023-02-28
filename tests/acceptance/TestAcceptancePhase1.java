package acceptance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lex.EOFToken;
import lex.FileReader;
import lex.LexAnalyser;
import lex.Token;
import syntax.SyntaxAnalyser;

/*
 * Acceptance testing for Lex and Syntax analyzer
 */
public class TestAcceptancePhase1 {
	
	private String TEST_FILES_PATH = "D:\\Panos\\CSE UOI\\10o εξάμηνο\\Μεταφραστές\\project-Compilers\\CutePy-Compiler\\tests\\acceptance\\"; 
	
	@Test
	public void testAllFunctions() {
		FileReader reader = new FileReader(TEST_FILES_PATH+"allFuncs.cpy");
		LexAnalyser lex = new LexAnalyser(reader);
		SyntaxAnalyser syntax = new SyntaxAnalyser(lex);
		try {
			syntax.analyzeSyntax();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		Token expectedCurrentTk = new EOFToken(79);
		Token expectedPrevTk = new Token(";", "delimiter", 78);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
	
	@Test
	public void testAllFunctionsModified() {
		FileReader reader = new FileReader(TEST_FILES_PATH+"allFuncsModified.cpy");
		LexAnalyser lex = new LexAnalyser(reader);
		SyntaxAnalyser syntax = new SyntaxAnalyser(lex);
		try {
			syntax.analyzeSyntax();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		Token expectedCurrentTk = new EOFToken(109);
		Token expectedPrevTk = new Token(";", "delimiter", 108);
		Assertions.assertEquals(expectedCurrentTk, syntax.getCurrentToken());
		Assertions.assertEquals(expectedPrevTk, syntax.getPrevToken());
	}
}
