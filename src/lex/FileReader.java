package lex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class FileReader {
	private String fileContents;	//doent contain an EOF Char
	private int filePointer;
	private String fileName;
	public static final Character EOF = '~';
	
	public FileReader() {
		new CharTypes();
		this.filePointer = 0;
	}
	
	public void initFileContents(String fileName) throws IOException {
		this.fileName = fileName;
		byte[] content = Files.readAllBytes(Paths.get(fileName));
		fileContents = new String(content) +EOF;
//		try {
//			byte[] content = Files.readAllBytes(Paths.get(fileName));
//			fileContents = new String(content) +EOF;
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getFileContents() {
		return fileContents;
	}

	public void setFilePointer(int f) {
		filePointer = f;
	}
	
	public void setFileContents(String f) {
		fileContents = f+EOF;
		filePointer = 0;
	}
	
	public Character readNext() {
		Character c = fileContents.charAt(filePointer);
		advanceFilePointer();
		return c;
	}

	private void advanceFilePointer() {
		filePointer++;
	}

	public void backtrackFilePointer() {
		filePointer--;
	}

	public void skipWhiteChars() throws Exception {
		List<Character> whiteChars = Arrays.asList(new Character[]{' ', '\t'});
		Character c = readNext();
		while(whiteChars.contains(c)) {
			c = readNext();
		}
		backtrackFilePointer();
	}

}
