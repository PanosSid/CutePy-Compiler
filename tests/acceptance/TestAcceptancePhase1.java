package acceptance;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import view.CutePyCompiler;

/*
 * Acceptance testing for Lex and Syntax analyzer (PHASE 1)
 */
public class TestAcceptancePhase1 {
	private String TEST_FILES_PATH = System.getProperty("user.dir")+"\\tests\\acceptance\\";
	private CutePyCompiler cpyCompiler = new CutePyCompiler();
	private ByteArrayOutputStream outContent;
//	private final PrintStream originalOut = System.out;
	
	public TestAcceptancePhase1() {
		/* 
		 * redirect System.out to outContent so that i can 
		 * test that the compilation message is printed
		 */
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
	}
	
	@Test
	public void testAllFunctions() {
		cpyCompiler.compile(TEST_FILES_PATH+"allFuncs.cpy");
		Assertions.assertTrue(outContent.toString().contains("Compilation of '"+TEST_FILES_PATH+"allFuncs.cpy' successfully completed")); 	// TODO DELETE IT, THIS IS TEMPORARY
//		Assertions.assertEquals("Compilation successfully completed", outContent.toString());
	}
	
	@Test
	public void testAllFunctionsModified() {
		cpyCompiler.compile(TEST_FILES_PATH+"allFuncsModified.cpy");
		Assertions.assertTrue(outContent.toString().contains("Compilation of '"+TEST_FILES_PATH+"allFuncsModified.cpy' successfully completed")); 	// TODO DELETE IT, THIS IS TEMPORARY
//		Assertions.assertEquals("Compilation successfully completed", outContent.toString());
		
	}
	
	@Test
	public void testSourceWithErrors() {
		cpyCompiler.compile(TEST_FILES_PATH+"sourceWithErrors.cpy");
		Assertions.assertTrue(outContent.toString().contains("Compilation of '"+TEST_FILES_PATH+"sourceWithErrors.cpy"+"'FAILED")); 	// TODO DELETE IT, THIS IS TEMPORARY
//		Assertions.assertEquals("Compilation successfully completed", outContent.toString());
		
	}
	
}
