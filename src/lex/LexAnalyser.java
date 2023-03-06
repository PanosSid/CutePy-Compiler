package lex;

import java.util.Arrays;
import java.util.List;

import exceptions.CutePyException;

public class LexAnalyser {
	public static final List<String> KEYWORDS = Arrays.asList("def", "if",
			"else", "while", "print", "return", "int", "input", "#declare",
			"or", "and", "not", "__name__", "\"__main__\"");	//TODO chek and add more if needed
	private FileReader sourceReader;
	private String processedStr;
	private int lineNum = 1;

	public LexAnalyser(FileReader sourceReader) {
		this.sourceReader = sourceReader;
		processedStr = "";
	}

	public Token getToken() throws CutePyException { 
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
			return getRelOperatorSmallerToken();
		} else if (c.equals('>')) {
			return getRelOperatorLargerToken();
		} else if (c.equals('#')) {
			return getSharpToken();
		} else if (c.equals('=')) {
			return getEqualOperatorToken();
		} else if (c.equals(' ') || c.equals('\t')) {
			return getToken();
		} else if (c.equals('_')) {
			return getUnderscoreNameToken();
		} else if (c.equals('!')) {
			return getNotEqualToOperator();
		} else if (c.equals('\"')) {
			return getStrMainToken();
		} else if (isNewLine(c)) {
			lineNum++;
			return getToken();
		} else if (c.equals(FileReader.EOF)) {
			return new EOFToken(lineNum);
		}
		throw new CutePyException("[Error at line:"+lineNum+"] found char '"+c+"' that doesnt belong in the CutePy alphabet");
	}
	
	private Token getNotEqualToOperator() throws CutePyException {
		Character c = readChar();
		if (c.equals('=')) {
			return new Token(processedStr, "relOperator", lineNum);
		} else {
			throw new CutePyException("[Error in line "+lineNum+"]: found '!"+c+"' must be '!='");			
		}
	}

	private Token getStrMainToken() throws CutePyException { 
		return getTokenAfterRecognisingConsecutiveChars("\"__main__\"" ,"keyword");
	}

	private Token getUnderscoreNameToken() throws CutePyException {
		return getTokenAfterRecognisingConsecutiveChars("__name__" ,"keyword");
	}
	
	private Token getSharpToken() throws CutePyException {
		Character c = readChar();
		if (c.equals('$')) {
			return skipComment();
		} else if (c.equals('d')) {
			return getDeclareToken();
		} else if (c.equals('{')) {
			return new Token(processedStr, "groupOperator", lineNum);
		} else if (c.equals('}')) {
			return new Token(processedStr, "groupOperator", lineNum);
		} else {
			throw new CutePyException("[Error in line "+lineNum+"] expected '#declare' or '#{' or '#}' or '#$' but found '#"+c+"' ");
		}
	}

	private Token getDeclareToken() throws CutePyException {
		return getTokenAfterRecognisingConsecutiveChars("declare" ,"keyword");
	}
	
	private Token getTokenAfterRecognisingConsecutiveChars(String reconStr,
			String tokenToBeReconFamily) throws CutePyException {
		unReadChar();
		for (int i = 0; i < reconStr.length(); i++) {
			Character c = readChar();
			if (c != reconStr.charAt(i)) {
				throw new CutePyException("[Error in line " + lineNum + "] expected '" + reconStr + "' but found '" +  processedStr + "' ");
			}
		}
		return new Token(processedStr, "keyword", lineNum);
	}

	private Token skipComment() throws CutePyException {
		skipCharsUntilHashTag();
		Character c = readChar();
		while (c != '$') {
			skipCharsUntilHashTag();
			c = readChar();
		}
		return getToken();
	}

	private Character skipCharsUntilHashTag() throws CutePyException {
		Character c = readChar();
		while (c != '#') {
			if (c.equals(FileReader.EOF)) {
				throw new CutePyException("[Error in line " + (lineNum-1) +"] comment is not closed!!");
			}
			if (isNewLine(c)) {
				lineNum++;
			}
			c = readChar();
		}
		return c;
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
		}
		return false;
	}

	private Token getIdkOrKeywordToken() throws CutePyException {
		Character c = readChar();
		if (processedStr.length() <=30 &&
				(Character.isAlphabetic(c) || Character.isDigit(c) || c.equals('_'))) {
			return getIdkOrKeywordToken();
		}
		unReadChar();
		if (KEYWORDS.contains(processedStr)) {
			return new Token(processedStr, "keyword", lineNum);
		}
		return new Token(processedStr, "identifier", lineNum);
	}

	private Token getNumberToken() throws CutePyException {
		Character c = readChar();
		if (Character.isAlphabetic(c)) {
			throw new CutePyException("[Error in line"+lineNum+"] found letter after character on line: " + lineNum);
		} else if (Character.isDigit(c)) {
			return getNumberToken();
		}
		unReadChar();
		if (isNumberOutOfBounds()) {
			throw new CutePyException("[Error in line "+lineNum+"] found integer : '"+processedStr+"' with value outside of the limits");
		}
		return new Token(processedStr, "number", lineNum);
	}
	
	private boolean isNumberOutOfBounds() {
		try {
			Integer parsedNum = Integer.parseInt(processedStr);
			return (parsedNum > (Math.pow(2, 32) - 1));
		} catch (NumberFormatException e) { 
			return true;
		}
	}
	
	private Token getAddOperatorToken() throws CutePyException {
		return new Token(processedStr, "addOperator", lineNum);
	}

	private Token getMulOperatorMultToken() {		
		return new Token(processedStr, "mulOperator", lineNum);
	}
	
	private Token getMulOperatorDivToken() throws CutePyException {
		Character c = readChar();
		if (!c.equals('/')) {
			throw new CutePyException("[Error in line "+lineNum+"]: Expected the division operator '//' but found '/"+c+"'");
		}
		return new Token(processedStr, "mulOperator", lineNum);
	}
	
	private Token getRelOperatorSmallerToken() throws CutePyException {
		Character c = readChar();
		if (c.equals('<')) {
			throw new CutePyException("[Error in line "+lineNum+"]: found '<<' must be < or <=");
		} else if (c.equals('=')) {
			return new Token(processedStr, "relOperator", lineNum);
		}
		unReadChar();
		return new Token(processedStr, "relOperator", lineNum);
	}
	
	private Token getRelOperatorLargerToken() throws CutePyException {
		Character c = readChar();
		if (c.equals('>') || c.equals('<')) {
			throw new CutePyException("[Error in line "+lineNum+"]: found '>"+c+"' must be > or >=");
		} else if (c.equals('=')) {
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

	private Token getEqualOperatorToken() throws CutePyException {
		Character c = readChar();
		if (c.equals('=')) {
			return new Token(processedStr, "relOperator", lineNum);
		} else if (Character.isAlphabetic(c) || Character.isDigit(c)) {
			unReadChar();
			return new Token(processedStr, "assignment", lineNum);
		} else if (c.equals(' ') || c.equals('\t')) {
			return getAssignmentWithSpace();
		} else {
			throw new CutePyException("[Error in line"+lineNum+"]: found '='. Relation operator is '==', ...>");	// TODO Better error msg			
		}
	}
	
	private Token getAssignmentWithSpace() throws CutePyException {
		Character c = readChar();
		if (Character.isAlphabetic(c) || Character.isDigit(c)) {
			unReadChar();
			return new Token(processedStr.trim(), "assignment", lineNum);	// the trim here is important
		}
		throw new CutePyException("[Error in line "+lineNum+"] expected '= <identifier> or <integer>' but found '= "+c+"'");
	}

	private void resetProcessedStr() {
		processedStr = "";
	}

}
