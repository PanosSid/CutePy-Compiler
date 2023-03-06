package lex;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;

public class TestFileReader {
	private String TEST_FILES_PATH = System.getProperty("user.dir")+"\\tests\\lex\\"; 
	@Test
	public void testInitFileContents() throws IOException {
		FileReader reader = new FileReader();
		reader.initFileContents(TEST_FILES_PATH+"readerTest.cpy");
		String expectedContents = "test panos line 1\r\n" + "\r\n" + "line 3 \r\n" + "youlo 4\r\n" + "ending line5"+FileReader.EOF;
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
