package lex;

import java.util.Arrays;
import java.util.List;

public class LexAnalyser {
	public static final List<String> KEYWORDS = Arrays.asList("def", "if",
			"else", "while", "print", "return", "int", "input", "#declare",
			"or", "and", "not", "\"__name__\"", "\"__main__\"");	//TODO chek and add more if needed
	private FileReader sourceReader;
	private String processedStr;
	private int lineNum = 1;

	public LexAnalyser(FileReader sourceReader) {
		this.sourceReader = sourceReader;
		processedStr = "";
	}

	public Token getToken() throws Exception { // + gia ta cutepycomments!!!!!
		resetProcessedStr();
		Character c = readChar();
		if (CharTypes.LETTERS.contains(c)) {
			return getIdkOrKeywordToken();
		} else if (CharTypes.DIGITS.contains(c)) {
			return getNumberToken();
		} else if (CharTypes.ADD_OPS.contains(c)) {
			return getAddOperatorToken();
		} else if (CharTypes.GROUP_SUMBOLS.contains(c)) {
			return getGroupOperatorToken();
		} else if (CharTypes.DELIMITERS.contains(c)) {
			return getDelimiterOperatorToken();
		} else if (c.equals('*')) {
			return getMulOperatorMultToken();
		} else if (c.equals('/')) {
			return getMulOperatorDivToken();
		} else if (c.equals('<')) {
			return getRealOperatorSmallerToken();
		} else if (c.equals('>')) {
			return getRealOperatorSmallerToken();
		} else if (c.equals('#')) {
			return getSharpToken();
		} else if (c.equals('=')) {
			return getEqualOperatorToken();
		} else if (c.equals(' ') || c.equals('\t')) {
			return getToken();
		} else if (isNewLine(c)) {
			lineNum++;
			return getToken();
		}

		return null;
	}
	
	private Token getSharpToken() throws Exception {
		Character c = readChar();
		if (c.equals('$')) {
			unReadChar();
			return skipComment();
		} else if (c.equals('d')) {
			return getDeclareToken();
		} else if (c.equals('{')) {
			return new Token(processedStr, "groupOperator", lineNum);
		} else if (c.equals('}')) {
			return new Token(processedStr, "groupOperator", lineNum);
		}
		return null;
	}

	private Token getDeclareToken() throws Exception {
		String declare = "declare";
		for (int i = 1; i < declare.length(); i++) {
			if (readChar() != declare.charAt(i)) {
				throw new Exception("Eror not declare but #"+declare.substring(i));
			}
		}
		return new Token(processedStr, "keyword", lineNum);
	}

	private Token skipComment() throws Exception {
		Character c = readChar();
		if (c.equals('$')) {
			while (readChar() != '#') {
				c = readChar();
				if (c.equals(FileReader.EOF)) {
					throw new Exception("[Error] comment is not closed!!");
				}
				if (isNewLine(c)){
					lineNum++;
				}
			}
			while (readChar() != '$') {
				c = readChar();
				if (c.equals(FileReader.EOF)) {
					throw new Exception("[Error] comment is not closed!!");
				}
			}
			return getToken();
			
		}
		throw new Exception("Not implemented yet need to check for #declare");
	}

	private Character readChar() {
		Character c = sourceReader.readNext();
		processedStr += c;
		return c;
	}

	private void unReadChar() {
		sourceReader.backtrackFilePointer();
		processedStr = processedStr.substring(0, processedStr.length() - 1);
	}

	public boolean isNewLine(Character current) { // TODO mono tou to \r theoreitai newline?
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

	private Token getIdkOrKeywordToken() throws Exception {
		Character c = readChar();
		if (processedStr.length() <=30 &&
				(Character.isAlphabetic(c) || Character.isDigit(c) || c.equals('_'))) {
			return getIdkOrKeywordToken();
		}
		unReadChar();
		if (KEYWORDS.contains(processedStr)) {
			return new Token(processedStr, "keyword", lineNum);
		} else if (!processedStr.startsWith("#")) {
			return new Token(processedStr, "identifier", lineNum);			
		} else {
			throw new Exception("[Error] unknow Error LOL"); //TODO pio kalo minima
		}
	}

	private Token getNumberToken() throws Exception {
		Character c = readChar();
		if (Character.isAlphabetic(c)) {
			throw new Exception("[Error] found letter after character on line:" + lineNum);
		} else if (Character.isDigit(c)) {
			return getNumberToken();
		}
		unReadChar();
		if (Integer.parseInt(processedStr) < (-Math.pow(2, 32) - 1)
				|| Integer.parseInt(processedStr) > (Math.pow(2, 32) - 1)) {
			throw new Exception("[Error] found integer with value outside of the limits");
		}
		return new Token(processedStr, "number", lineNum);
	}

	private Token getAddOperatorToken() throws Exception {
		return new Token(processedStr, "addOperator", lineNum);
	}

	private Token getMulOperatorMultToken() {		
		return new Token(processedStr, "mulOperator", lineNum);
	}
	
	private Token getMulOperatorDivToken() throws Exception {
		Character c = readChar();
		if (!c.equals('/')) {
			throw new Exception("[Error]: Char after division char '/' is "+c+" not '/");
		}
		return new Token(processedStr, "mulOperator", lineNum);
	}
	
	private Token getRealOperatorSmallerToken() throws Exception {
		Character c = readChar();
		if (c.equals('<')) {
			throw new Exception("[Error]: found '<<' must be < or <> or <=");
		} else if (c.equals('>') || c.equals('=')) {
			return new Token(processedStr, "relOperator", lineNum);
		}
		unReadChar();
		return new Token(processedStr, "relOperator", lineNum);
	}

	private Token getGroupOperatorToken() {
		return new Token(processedStr, "groupOperator", lineNum);
	}

	private Token getDelimiterOperatorToken() {
		return new Token(processedStr, "delimiter", lineNum);
	}

	private Token getEqualOperatorToken() throws Exception {
		Character c = readChar();
		if (c.equals('=')) {
			return new Token(processedStr, "relOperator", lineNum);
		} else if (Character.isAlphabetic(c) || Character.isDigit(c)) {
			unReadChar();
			return new Token(processedStr, "assignment", lineNum);
		} else if (c.equals(' ') || c.equals('\t')) {
			return getAssignmentWithSpace();
		} else {
			throw new Exception("[Error]: found ''=");					
		}
	}
	
	private Token getAssignmentWithSpace() throws Exception {
		Character c = readChar();
		if (Character.isAlphabetic(c) || Character.isDigit(c)) {
			unReadChar();
			return new Token(processedStr.trim(), "assignment", lineNum);	// the trim here is important
		}
		throw new Exception("[Error] found '=whitespace and a char tha is not a digit or letter");
	}

	private void resetProcessedStr() {
		processedStr = "";
	}

	public static void main(String args[]) {
		FileReader reader = new FileReader();
		reader.setFileContents(" #$ : 547 ");
		LexAnalyser lex = new LexAnalyser(reader);
		try {
			lex.getToken();
		} catch (Exception e) {
			
		}

	}
}
