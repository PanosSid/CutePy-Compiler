package lex;

import java.util.Arrays;
import java.util.List;

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

	public Token getToken() throws Exception { 
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
		} else if (c.equals('\"')) {
			return getStrMainToken();
		} else if (isNewLine(c)) {
			lineNum++;
			return getToken();
		} else if (c.equals(FileReader.EOF)) {
			return new EOFToken(lineNum);
		}
		throw new Exception("[Error at line:"+lineNum+"] found char '"+c+"' that doesnt belong in the CutePy alphabet");
	}
	
	private Token getStrMainToken() throws Exception { 
		return getTokenAfterRecognisingConsecutiveChars("__main__\"" ,"keyword");
	}

	private Token getUnderscoreNameToken() throws Exception {
		return getTokenAfterRecognisingConsecutiveChars("_name__" ,"keyword");
	}
	
	private Token getTokenAfterRecognisingConsecutiveChars(String reconStr,
			String tokenToBeReconFamily) throws Exception {
		for (int i = 0; i < reconStr.length(); i++) {
			if (readChar() != reconStr.charAt(i)) {
				throw new Exception("[Error: at line" + lineNum + "] expected '" + reconStr + "' but found '" +  reconStr.charAt(i) + "' ");
			}
		}
		return new Token(processedStr, "keyword", lineNum);
	}

	private Token getSharpToken() throws Exception {
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
			throw new Exception("[Error: at line "+lineNum+"] expected '#declare' or '#{' or '#}' or '#$' but found '#"+c+"' ");
		}
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
		skipCharsUntilHashTag();
		Character c = readChar();
		while (c != '$') {
			skipCharsUntilHashTag();
			c = readChar();
		}
		return getToken();
	}

	private Character skipCharsUntilHashTag() throws Exception {
		Character c = readChar();
		while (c != '#') {
			if (c.equals(FileReader.EOF)) {
				throw new Exception("[Error: at line " + (lineNum-1) +"] comment is not closed!!");
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
		} else if (CharTypes.isNotInAlphabet(c)){
			throw new Exception("[Error at line:"+lineNum+"] found char '"+c+"' that doesnt belong in the CutePy alphabet");
		}
		throw new Exception("System Error: at line"+lineNum+" "); //TODO pio kalo minima;
	}

	private Token getNumberToken() throws Exception {
		Character c = readChar();
		if (Character.isAlphabetic(c)) {
			throw new Exception("[Error: at line"+lineNum+"] found letter after character on line:" + lineNum);
		} else if (Character.isDigit(c)) {
			return getNumberToken();
		}
		unReadChar();
		if (Integer.parseInt(processedStr) < (-Math.pow(2, 32) - 1)
				|| Integer.parseInt(processedStr) > (Math.pow(2, 32) - 1)) {
			throw new Exception("[Error: at line"+lineNum+"] found integer with value outside of the limits");
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
			throw new Exception("[Error: at line"+lineNum+"]: Char after division char '/' is "+c+" not '/");
		}
		return new Token(processedStr, "mulOperator", lineNum);
	}
	
	private Token getRelOperatorSmallerToken() throws Exception {
		Character c = readChar();
		if (c.equals('<')) {
			throw new Exception("[Error: at line"+lineNum+"]: found '<<' must be < or <> or <=");
		} else if (c.equals('>') || c.equals('=')) {
			return new Token(processedStr, "relOperator", lineNum);
		}
		unReadChar();
		return new Token(processedStr, "relOperator", lineNum);
	}
	
	private Token getRelOperatorLargerToken() throws Exception {
		Character c = readChar();
		if (c.equals('>') || c.equals('<')) {
			throw new Exception("[Error: at line"+lineNum+"]: found '>"+c+"' must be > or >=");
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
			throw new Exception("[Error: at line"+lineNum+"]: found ''=");					
		}
	}
	
	private Token getAssignmentWithSpace() throws Exception {
		Character c = readChar();
		if (Character.isAlphabetic(c) || Character.isDigit(c)) {
			unReadChar();
			return new Token(processedStr.trim(), "assignment", lineNum);	// the trim here is important
		}
		throw new Exception("[Error: at line"+lineNum+"] found '=whitespace and a char that is not a digit or letter found '= "+c+"'");
	}

	private void resetProcessedStr() {
		processedStr = "";
	}

}
