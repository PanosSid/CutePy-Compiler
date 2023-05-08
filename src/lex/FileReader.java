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
		replaceNegativeVars();
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
	
	/**
	 * Used to deal with negative variables in the program.
	 * It replaces negative variables with a multiplication with -1
	 * etc a = -x  -> a = (-1)*x  
	 */
	public void replaceNegativeVars() {		
		String regex = "([\\[\\(<>=]\\s*)-(\\s*[a-zA-Z(])";
		String replacement = "$1(-1)*$2";
		fileContents = fileContents.replaceAll(regex, replacement);
	}
	
	public static void main(String args[]) {
		FileReader fr = new FileReader();
		fr.setFileContents("\n"
				+ "= -45 \n"
				+ "=-panos\n"
				+ "[-panos222]\n"
				+ "(-panos222)\n"
				+ "<-panos222\n"
				+ ">-panos_222)\n"
				+ "-panos222)\n"
				+ "= -x\n"
				+ "= - x\n"
				+ "= -(x+5)\n"
				);
		fr.replaceNegativeVars();
		System.out.println("Input: " + fr.getFileContents());
		System.out.println("Output: " + fr.getFileContents());
	}

}
