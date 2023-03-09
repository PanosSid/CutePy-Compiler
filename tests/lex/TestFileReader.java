package lex;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Assertions;

public class TestFileReader {
	private String NEW_LINE = System.lineSeparator();
	private String TEST_FILES_PATH = System.getProperty("user.dir")+File.separator +"tests"+File.separator +"lex"+File.separator; 
	
	@Test
	public void testInitFileContents() throws IOException {
		FileReader reader = new FileReader();
		reader.initFileContents(TEST_FILES_PATH+"readerTest.cpy");
		String expectedContents = "test panos line 1"+NEW_LINE + NEW_LINE + "line 3 "+NEW_LINE + "youlo 4" +NEW_LINE + "ending line5"+FileReader.EOF;
		String actualContents = reader.getFileContents();
		Assertions.assertEquals(expectedContents, actualContents);
	}
	
	@Test 
	public void testReadNext() throws Exception {
		FileReader reader = new FileReader();
		reader.initFileContents(TEST_FILES_PATH+"readerNext.cpy");
		char firstChar = reader.readNext();
		Assertions.assertEquals('p', firstChar);
		for (int i = 1; i < 18; i++) { 	// skip chars
			reader.readNext();			
		}
		char tabChar = reader.readNext();
		Assertions.assertEquals('\t', tabChar);
	} 
	

}
