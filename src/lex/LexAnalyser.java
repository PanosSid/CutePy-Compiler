package lex;

import java.util.Arrays;
import java.util.List;

public class LexAnalyser {
	private FileReader sourceReader;
	private String processedStr;
	private int lineNum = 1;

	
	public LexAnalyser(FileReader sourceReader) {
		this.sourceReader = sourceReader;
		processedStr = "";
		CharTypes.initListWithAllCharTypes();
	}
	
	public Token getToken() throws Exception {	// +  gia ta cutepycomments!!!!!
		resetProcessedStr();	
		Character c = readChar();
		
		if (CharTypes.LETTERS.contains(c)) {
			
		} else if (CharTypes.DIGITS.contains(c)) {
			return getNumberToken();
		} else if (CharTypes.ADD_OPS.contains(c)) {
			
		} else if (CharTypes.MUL_OPS.contains(c)) {
			
		} else if (CharTypes.GROUP_SUMBOLS.contains(c)) {
			
		} else if (CharTypes.DELIMITERS.contains(c)) {
			
		} else if (c.equals(':')) {
			
		} else if (c.equals('<')) {
			
		} else if (c.equals('>')) {
			
		} else if (c.equals('=')) {
			
		} else if (c.equals(' ') || c.equals('\t') ) {
			return getToken();
		} else if (isNewLine(c)) {
			lineNum++;
			return getToken();
		}
		
		System.out.println("Error char not in alphabet char = "+c);
		return null;
	}

	private Character readChar(){
		Character c = sourceReader.readNext();
		processedStr += c;
		return c;
	}
	
	private void unReadChar() {
		sourceReader.backtrackFilePointer();
		processedStr = processedStr.substring(0, processedStr.length()-1); 
	}
	
	public boolean isNewLine(Character current) {	//TODO mono tou to \r theoreitai newline?
		if (current.equals('\n')) {
			return true;
		} else if (current.equals('\r')) {
			Character c = sourceReader.readNext();
			if (c.equals('\n'))
				return true;
			else {
				return false;
			}
		}
		return false;
	}
	
	private Token getNumberToken() {
		Character c = readChar();
		if (Character.isAlphabetic(c)) {
			System.out.println("Error");
			System.exit(-1);
		}
		if (Character.isDigit(c)) {
			return getNumberToken();
		}
		unReadChar();
		return new Token(processedStr, "number", lineNum);
	}
	
	private void resetProcessedStr() {
		processedStr = "";
	}


	public static void main(String args[]) {	
		char[] alphabet = "0123456789".toCharArray();
		for (int i = 0; i < alphabet.length; i++) {
			System.out.print("'"+alphabet[i]+"', ");

		}
		System.out.println();
		char[] alphabet2 = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();
		for (int i = 0; i < alphabet2.length; i++) {
			System.out.print("'"+alphabet2[i]+"', ");
		}
	}
}
