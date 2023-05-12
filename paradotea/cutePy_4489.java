import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489

public class cutePy_4489 {
	private SyntaxAnalyser syntaxAnalyzer;
	private LexAnalyser lex ;
	private FileReader reader;
	private QuadManager quadManager; 
	private CTransformer cTransformer;
	private SymbolTable symbolTable;
	private FinalCodeManager finManager;
	
	public cutePy_4489() {
		reader = new FileReader();
		lex = new LexAnalyser(reader);
		quadManager = new QuadManager(1);
		symbolTable = new SymbolTable();
		finManager = new FinalCodeManager(symbolTable);
		syntaxAnalyzer = new SyntaxAnalyser(lex, quadManager, symbolTable, finManager);
		cTransformer = new CTransformer(); 
	}
	
	public void compile(String filePath, boolean cflag) {
		try {
			checkForCpyExtension(filePath);
			reader.initFileContents(filePath);	
			syntaxAnalyzer.analyzeSyntax();
			writeToFile(filePath, "int", quadManager.getIntermediateCode(), "//");
			if (cflag) {
				quadManager.setTempPrefix("T_");
				transformToC(filePath);			
			}
			writeToFile(filePath, "symb", symbolTable.getSymbolTableHistory(), "//");
			writeToFile(filePath, "asm", finManager.getFinalCode(), "#");
			System.out.println("Compilation of '"+filePath+"' successfully completed");
		} catch (CutePyException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			e.printStackTrace();
		}
	}
	
	public void writeToFile(String filePath, String extension, String data, String commentType) throws IOException {
		String fileName = changeFilePathExtenstion(filePath, extension);
		FileWriter writer = new FileWriter(fileName);
		writer.write(commentType+"SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n\n");
		writer.write(data);
		writer.close();
	}
	
	public void transformToC(String filePath) throws IOException {
		cTransformer.transformIntermidateCodeToC(quadManager.getIntermedCodeMap());
		writeToFile(filePath, "c", cTransformer.getCcode(), "//");
	}

	private String changeFilePathExtenstion(String fileName, String extension) {
		return fileName.substring(0, fileName.lastIndexOf(".")+1) + extension;
	}
	
	private void checkForCpyExtension(String filePath) throws CutePyException {
		String fileName = filePath.substring(filePath.lastIndexOf(File.separator)+1);
		String extention = fileName.substring(fileName.lastIndexOf(".")+1);
		if (!extention.equals("cpy")) {
			throw new CutePyException("File extention must be 'cpy' but was '"+extention+"', for file "+fileName);
		}
	}
	
	public static void main(String[] args) {	
		List<String> argsList = new ArrayList<String>(Arrays.asList(args));
		boolean cFlag = argsList.remove("-c");
		cutePy_4489 cpyCompiler = new cutePy_4489();
		cpyCompiler.compile(argsList.get(0), cFlag);	
	}

}

class CharTypes {
	public static final List<Character> ADD_OPS = Arrays.asList(new Character[] { '+', '-' });
	public static final List<Character> DELIMITERS = Arrays.asList(new Character[] { ',', ';', ':' });
	public static final List<String> MUL_OPS = Arrays.asList(new String[] { "*", "//" });
	public static final List<Character> GROUP_SUMBOLS = Arrays.asList(new Character[] { '{', '}', '(', ')', '[', ']'});
	public static final List<Character> ASGN = Arrays.asList(new Character[] { '=' });
	public static final List<Character> SMALLER = Arrays.asList(new Character[] { '<' });
	public static final List<Character> LARGER = Arrays.asList(new Character[] { '>' });	
	public static final List<Character> LETTERS = Arrays
			.asList(new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
					'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'});
	public static final List<Character> DIGITS = Arrays.asList(new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});	
	public static final List<String> REL_OPS = Arrays.asList(new String[] { "<", "<=", ">", ">=", "!=", "=="});
	
	public static boolean isNotInAlphabet(Character c) {
		Set<Character> CUTEPY_ALPHABET = new HashSet<Character>();
		CUTEPY_ALPHABET.addAll(ADD_OPS);
		CUTEPY_ALPHABET.addAll(DELIMITERS);
		CUTEPY_ALPHABET.addAll(GROUP_SUMBOLS);
		CUTEPY_ALPHABET.addAll(ASGN);
		CUTEPY_ALPHABET.addAll(SMALLER);
		CUTEPY_ALPHABET.addAll(LARGER);
		CUTEPY_ALPHABET.addAll(LETTERS);
		CUTEPY_ALPHABET.addAll(DIGITS);
		CUTEPY_ALPHABET.add('_');
		CUTEPY_ALPHABET.add('*');
		CUTEPY_ALPHABET.add('<');
		CUTEPY_ALPHABET.add('>');
		CUTEPY_ALPHABET.add('!');
		return !CUTEPY_ALPHABET.contains(c);
	}
}

class CutePyException extends Exception {

	private static final long serialVersionUID = -6410108192678340586L;

	public CutePyException(String message) {
		super(message);
	}
}


class FileReader {
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

}

class Token {
	private String recognizedStr;
	private String family;
	private int lineNum;
	
	public Token(String recognizedStr, String family, int lineNum) {
		super();
		this.recognizedStr = recognizedStr;
		this.family = family;
		this.lineNum = lineNum;
	}

	public String getRecognizedStr() {
		return recognizedStr;
	}
	
	public String getFamily() {
		return family;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public boolean recognizedStrEquals(String otherRecognStr) {
		return recognizedStr.equals(otherRecognStr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(family, lineNum, recognizedStr);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		return Objects.equals(family, other.family) && lineNum == other.lineNum
				&& Objects.equals(recognizedStr, other.recognizedStr);
	}

	@Override
	public String toString() {
		return "Token [recognizedStr=" + recognizedStr + ", family=" + family + ", lineNum=" + lineNum + "]";
	}

}

class EOFToken extends Token {

	public EOFToken(int lineNum) {
		super(FileReader.EOF.toString(), "End Of File", lineNum);
	}

}


class LexAnalyser {
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
		if (Character.isAlphabetic(c) || Character.isDigit(c) || CharTypes.ADD_OPS.contains(c) || c.equals('(')) {
			unReadChar();
			return new Token(processedStr.trim(), "assignment", lineNum);	// the trim here is important
		} else {
			throw new CutePyException("[Error in line "+lineNum+"] expected '= <identifier> or <integer>' but found '= "+c+"'");						
		}
	}

	private void resetProcessedStr() {
		processedStr = "";
	}

}

class SyntaxAnalyser {
	private LexAnalyser lex;
	private Token currentToken;
	private QuadManager quadManager;
	private SymbolTable symbolTable;
	private FinalCodeManager finManager;
	
	private boolean statementsHasReturn = false;
	
	public SyntaxAnalyser(LexAnalyser lex) {
		this.lex = lex;
		quadManager = new QuadManager();
		symbolTable = new SymbolTable();
	}
	
	public SyntaxAnalyser(LexAnalyser lex, QuadManager quadManager, SymbolTable symbolTable,
			FinalCodeManager finManager) {
		this.lex = lex;
		this.quadManager = quadManager;
		this.symbolTable = symbolTable;
		this.finManager = finManager;
		finManager.initMainFinalCode();
	}

	public Token getCurrentToken() {
		return currentToken;
	}
	
	public void setCurrentToken(Token currentToken) {
		this.currentToken = currentToken;
	}

	public void analyzeSyntax() throws CutePyException {
		loadNextTokenFromLex();
		startRule();
	}

	private void startRule() throws CutePyException {
		defMainPart();
		callMainPart();
	}

	private void defMainPart() throws CutePyException {
		defMainFunction();
		while (currentToken.recognizedStrEquals("def")) {
			defMainFunction();
		}
	}

	private void defMainFunction() throws CutePyException {
//		mainFunctionhasReturn = false;
		if (currentToken.getRecognizedStr().equals("def")) {
			loadNextTokenFromLex();
			if (isMainFuncId()) {
				String mainFuncName = currentToken.getRecognizedStr();
				loadNextTokenFromLex();
				String args[] = { "(", ")", ":", "#{" };
				for (int i = 0; i < args.length; i++) {
					if (currentToken.getRecognizedStr().equals(args[i])) {
						loadNextTokenFromLex();
					} else {
						throw new CutePyException(getErrorMsg(args[i]));
					}
				}
				symbolTable.addFunction(EntityFactory.createMainFunction(mainFuncName));
				declarations();
				while (currentToken.recognizedStrEquals("def")) {
					defFunction();
				}
				int fCodeStartingLbl = quadManager.nextQuad();
				quadManager.genQuad("begin_block", mainFuncName, "_", "_");
				symbolTable.updateStartingQuadField(quadManager.nextQuad());
				statements();
				if (statementsHasReturn == true) {
					throw new CutePyException("Error: main function '"+mainFuncName+"' has a return statement");
				}
				
				quadManager.genQuad("end_block", mainFuncName, "_", "_");
				symbolTable.updateFrameLengthField();
				if (currentToken.getRecognizedStr().equals("#}")) {
					loadNextTokenFromLex();
					finManager.genFinalCode(fCodeStartingLbl, quadManager.getIntermedCodeMap());
					symbolTable.removeScope();
				} else {
					throw new CutePyException(getErrorMsg("#}"));
				}
			} else {
				throw new CutePyException(getErrorMsg("<identifier> that starts with 'main_'"));
			}
		} else {
			throw new CutePyException(getErrorMsg("def"));

		}

	}
	
	private void defFunction() throws CutePyException {
		if (currentToken.getRecognizedStr().equals("def")) {
			loadNextTokenFromLex();
			if (isID(currentToken) && !isMainFuncId()) {
				String funcName = currentToken.getRecognizedStr(); 
				loadNextTokenFromLex();
				if (currentToken.recognizedStrEquals("(")) {
					loadNextTokenFromLex();
					List<String> formalParams = idList();
					symbolTable.addLocalFunction(
							EntityFactory.createLocalFunction(funcName, formalParams),
							EntityFactory.createListOfParameters(formalParams)
							);
					String args[] = { ")", ":", "#{" };
					for (int i = 0; i < args.length; i++) {
						if (currentToken.getRecognizedStr().equals(args[i])) {
							loadNextTokenFromLex();
						} else {
							throw new CutePyException(getErrorMsg(args[i]));
						}
					}
					declarations();
					while (currentToken.recognizedStrEquals("def")) {
						defFunction();
					}
					int fCodeStartingLbl = quadManager.nextQuad();
					quadManager.genQuad("begin_block", funcName, "_", "_");
					symbolTable.updateStartingQuadField(quadManager.nextQuad());
					statements();
					if (statementsHasReturn == false) {
						throw new CutePyException("Error: the local function: '"+ funcName +"' does not have a return statement");
					}
					quadManager.genQuad("end_block", funcName, "_", "_");
					symbolTable.updateFrameLengthField();
					statementsHasReturn = false;

					if (currentToken.getRecognizedStr().equals("#}")) {
						loadNextTokenFromLex();
						finManager.genFinalCode(fCodeStartingLbl, quadManager.getIntermedCodeMap());
						symbolTable.removeScope();
					} else {
						throw new CutePyException(getErrorMsg("#}"));
					}
				} else {
					throw new CutePyException(getErrorMsg("("));
				}
			} else {
				throw new CutePyException(getErrorMsg("<identifier> that does not start with 'main_'"));
			}
		} else {
			throw new CutePyException(getErrorMsg("def"));
		}
	}

	private void declarations() throws CutePyException {
		while (currentToken.recognizedStrEquals("#declare")) {
			loadNextTokenFromLex();
			declerationLine();
		}
	}

	private void declerationLine() throws CutePyException {
		List<String> vars = idList();
		for (String var : vars) {
			symbolTable.addEntity(EntityFactory.createVariable(var, symbolTable.getOffsetOfNextEntity()));
		}
	}

	private void statement() throws CutePyException {
		if (isSimpleStatement()) {
			simpleStatement();
		} else if (isStructuredStatement()) {
			structuredStatement();
			
		} else {
			throw new CutePyException(getErrorMsg("at least one simple or structured statement"));
		}
	}

	public void statements() throws CutePyException {
		statement();
		while (isStatement()) {
			statement();
		}
	}

	private boolean isStatement() {
		return isSimpleStatement() || isStructuredStatement();
	}

	private String simpleStatement() throws CutePyException {
		if (isID(currentToken)) {
			assignmentStat();
			return "assignment";
		} else if (currentToken.recognizedStrEquals("print")) {
			printStat();
			return "print";
		} else if (currentToken.recognizedStrEquals("return")) {
			returnStat();
			return "return";
		} else {
			throw new CutePyException(getErrorMsg("<identifier> or 'print' or 'return'"));
		}
	}

	private boolean isSimpleStatement() {
		return isID(currentToken) || currentToken.recognizedStrEquals("print")
				|| currentToken.recognizedStrEquals("return");
	}

	public String structuredStatement() throws CutePyException {
		if (currentToken.recognizedStrEquals("if")) {
			loadNextTokenFromLex();
			ifStat();
			return "if";
		} else if (currentToken.recognizedStrEquals("while")) {
			loadNextTokenFromLex();
			whileStat();
			return "while";
		} else {
			throw new CutePyException(getErrorMsg("expected a stuctured statement 'if' or 'while'"));
		}
	}

	private boolean isStructuredStatement() {
		return currentToken.recognizedStrEquals("if") || currentToken.recognizedStrEquals("while");
	}
	
	private void assignmentStat() throws CutePyException {
		String aPlace = currentToken.getRecognizedStr();
		loadNextTokenFromLex();
		if (currentToken.recognizedStrEquals("=")) {
			loadNextTokenFromLex();
			if (isExpression()) {
				String ePlace = expression();
				if (currentToken.recognizedStrEquals(";")) {
					loadNextTokenFromLex();
					quadManager.genQuad(":=", ePlace, "_", aPlace);
				} else {
					throw new CutePyException(getErrorMsg(";"));
				}
			} else if (currentToken.recognizedStrEquals("int")) {
				loadNextTokenFromLex();
				String nextTks[] = { "(", "input", "(", ")", ")", ";" };
				for (int i = 0; i < nextTks.length; i++) {
					if (currentToken.recognizedStrEquals(nextTks[i])) {
						loadNextTokenFromLex();
					} else {
						throw new CutePyException(getErrorMsg(nextTks[i]));
					}
				}
				quadManager.genQuad("in", aPlace, "_", "_");
			} else {
				throw new CutePyException(getErrorMsg("'= expresion' or '= int(input());'"));
			}
		} else {
			throw new CutePyException(getErrorMsg("="));
		}
	}

	private boolean isExpression() {
		return CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0)) || isFactor();
	}

	private boolean isFactor() {
		return isInteger(currentToken.getRecognizedStr()) || currentToken.recognizedStrEquals("(")
				|| isID(currentToken);
	}

	private void printStat() throws CutePyException {
		String args1[] = { "print", "(" };
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args1[i])) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(args1[i]));
			}
		}
		String ePlace = expression();
		String args2[] = { ")", ";" };
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args2[i])) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(args2[i]));
			}
		}
		quadManager.genQuad("out", "_", "_", ePlace);
	}

	private void returnStat() throws CutePyException {
		String args1[] = { "return", "(" };
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args1[i])) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(args1[i]));
			}
		}
		String ePlace = expression();
		String args2[] = { ")", ";" };
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args2[i])) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(args2[i]));
			}
		}
		quadManager.genQuad("ret", ePlace, "_", "_");
		statementsHasReturn = true;
	}

	private void ifStat() throws CutePyException {
		if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			Map<String, List<Integer>> conditionMap = condition();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
				quadManager.backpatch(conditionMap.get("true"), quadManager.nextQuad());
				if (currentToken.recognizedStrEquals(":")) {
					loadNextTokenFromLex();
					if (currentToken.recognizedStrEquals("#{")) {
						loadNextTokenFromLex();
						statements();
						List<Integer> ifList = quadManager.makeList(quadManager.nextQuad());
						quadManager.genQuad("jump", "_", "_", "_");
						quadManager.backpatch(conditionMap.get("false"), quadManager.nextQuad());
						if (currentToken.recognizedStrEquals("#}")) {
							loadNextTokenFromLex();
							if (currentToken.recognizedStrEquals("else")) {
								elsePart();
							}
							quadManager.backpatch(ifList, quadManager.nextQuad());
						} else {
							throw new CutePyException(getErrorMsg("#}"));
						}
					} else if (isStatement()) {
						statement();
						List<Integer> ifList = quadManager.makeList(quadManager.nextQuad());
						quadManager.genQuad("jump", "_", "_", "_");
						quadManager.backpatch(conditionMap.get("false"), quadManager.nextQuad());
						if (currentToken.recognizedStrEquals("else")) {
							elsePart();
						}
						quadManager.backpatch(ifList, quadManager.nextQuad());
					} else {
						throw new CutePyException(getErrorMsg("#{"));
					}
				} else {
					throw new CutePyException(getErrorMsg(":"));
				}
			} else {
				throw new CutePyException(getErrorMsg(")"));
			}
		} else {
			throw new CutePyException(getErrorMsg("("));
		}
	}

	private void elsePart() throws CutePyException {
		if (currentToken.recognizedStrEquals("else")) {
			loadNextTokenFromLex();
			if (currentToken.recognizedStrEquals(":")) {
				loadNextTokenFromLex();
				if (currentToken.recognizedStrEquals("#{")) {
					loadNextTokenFromLex();
					statements();
					if (currentToken.recognizedStrEquals("#}")) {
						loadNextTokenFromLex();
					} else {
						throw new CutePyException(getErrorMsg("#}"));
					}
				} else if (isStatement()) {
					statement();
				} else {
					throw new CutePyException(getErrorMsg("#{"));
				}
			} else {
				throw new CutePyException(getErrorMsg(":"));
			}
		} else {
			throw new CutePyException(getErrorMsg("else"));
		}

	}

	public void whileStat() throws CutePyException {
		int whileQuad = quadManager.nextQuad();
		if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			Map<String, List<Integer>> conditionMap = condition();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
				if (currentToken.recognizedStrEquals(":")) {
					loadNextTokenFromLex();
					if (currentToken.recognizedStrEquals("#{")) {
						loadNextTokenFromLex();
						quadManager.backpatch(conditionMap.get("true"), quadManager.nextQuad());
						statements();
						quadManager.genQuad("jump", "_", "_", whileQuad+"");
						quadManager.backpatch(conditionMap.get("false"), quadManager.nextQuad());
						if (currentToken.recognizedStrEquals("#}")) {
							loadNextTokenFromLex();
						} else {
							throw new CutePyException(getErrorMsg("#}"));
						}
					} else if (isStatement()) {
						quadManager.backpatch(conditionMap.get("true"), quadManager.nextQuad());
						statement();
						quadManager.genQuad("jump", "_", "_", whileQuad+"");
						quadManager.backpatch(conditionMap.get("false"), quadManager.nextQuad());
					} else {
						throw new CutePyException(getErrorMsg("#{"));
					}
				} else {
					throw new CutePyException(getErrorMsg(":"));
				}
			} else {
				throw new CutePyException(getErrorMsg(")"));
			}
		} else {
			throw new CutePyException(getErrorMsg("("));
		}
	}

	private List<String> idList() throws CutePyException {
		List<String> ids = new ArrayList<String>();
		if (isID(currentToken)) {
			ids.add(currentToken.getRecognizedStr());
			loadNextTokenFromLex();
			while (currentToken.recognizedStrEquals(",")) {
				loadNextTokenFromLex();
				if (isID(currentToken)) {
					if (ids.contains(currentToken.getRecognizedStr())) {
						throw new CutePyException("[Error in line " + currentToken.getLineNum() + "] Duplicate id: "+currentToken.getRecognizedStr());
					}
					ids.add(currentToken.getRecognizedStr());
					loadNextTokenFromLex();
				} else {
					throw new CutePyException(getErrorMsg("identifier after ','"));
				}
			}
		}
		return ids;
		// dont need to throw exception here
	}

	private String expression() throws CutePyException {
		String sign = optionalSign();
		String t1Place = sign + term();
		while (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))) {
			String addOp = currentToken.getRecognizedStr();
			loadNextTokenFromLex();
			String t2Place = term();
			String w = quadManager.newTemp();
			quadManager.genQuad(addOp, t1Place, t2Place, w);
			symbolTable.addEntity(EntityFactory.createTemporaryVariable(w, symbolTable.getOffsetOfNextEntity()));
			t1Place = w;
		}
		return t1Place;
	}

	private String term() throws CutePyException {
		String f1Place = factor();
		while (CharTypes.MUL_OPS.contains(currentToken.getRecognizedStr())) {
			String mulOp = currentToken.getRecognizedStr();
			loadNextTokenFromLex();
			String f2Place = factor();
			String w = quadManager.newTemp();
			quadManager.genQuad(mulOp, f1Place, f2Place, w);
			symbolTable.addEntity(EntityFactory.createTemporaryVariable(w, symbolTable.getOffsetOfNextEntity()));
			f1Place = w;
		}
		return f1Place;
	}

	private String factor() throws CutePyException {
		if (isInteger(currentToken.getRecognizedStr())) {
			String fPlace = currentToken.getRecognizedStr();
			loadNextTokenFromLex();
			return fPlace;
		} else if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			String ePlace = expression();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
				return ePlace;
			} else {
				throw new CutePyException(getErrorMsg(")"));
			}
		} else if (isID(currentToken)) {
			String fPlace = currentToken.getRecognizedStr();
			loadNextTokenFromLex();
			String idTailPlace = idTail(fPlace);
			if (idTailPlace != null) {
				return idTailPlace;
			}
			return fPlace; 
		} else {
			throw new CutePyException(getErrorMsg("<identifier> or <integer> or '('"));
		}
	}

	private boolean isInteger(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private String idTail(String funcName) throws CutePyException {
		if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			actualParList();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
				String tmp = quadManager.newTemp();
				quadManager.genQuad("par", tmp, "ret", "_");
				quadManager.genQuad("call", funcName, "_", "_");
				symbolTable.addEntity(EntityFactory.createTemporaryVariable(tmp, symbolTable.getOffsetOfNextEntity()));
				return tmp;
			} else {
				throw new CutePyException(getErrorMsg(")"));
			}
		}
		return null;
		// dont need to throw exception here
	}

	private void actualParList() throws CutePyException {
		if (isExpression()) {
			String expressionPlace1 = expression();
			if (currentToken.recognizedStrEquals(",")) {
				while (currentToken.recognizedStrEquals(",")) {
					loadNextTokenFromLex();
					String expressionPlace2 = expression();
					quadManager.genQuad("par", expressionPlace1, "cv", "_");
					quadManager.genQuad("par", expressionPlace2, "cv", "_");
				}
			} else {
				quadManager.genQuad("par", expressionPlace1, "cv", "_");				
			}
		}
	}

	private String optionalSign() throws CutePyException {
		if (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))) {
			String sign = currentToken.getRecognizedStr(); 
			loadNextTokenFromLex();
			return sign;
		}
		return "";
		
	}

	public Map<String, List<Integer>> condition() throws CutePyException {
		Map<String, List<Integer>> boolTermMap = boolTerm();
		while (currentToken.recognizedStrEquals("or")) {
			loadNextTokenFromLex();
			quadManager.backpatch(boolTermMap.get("false"), quadManager.nextQuad());
			Map<String, List<Integer>> boolTerm2Map = boolTerm();
			boolTermMap.put("true", quadManager.mergeList(boolTermMap.get("true"), boolTerm2Map.get("true")));
			boolTermMap.put("false", boolTerm2Map.get("false"));
		}
		return boolTermMap;
	}

	private Map<String, List<Integer>> boolTerm() throws CutePyException {
		Map<String, List<Integer>> boolFactor1Map = boolFactor(); 	
		Map<String, List<Integer>> boolTermMap =  boolFactor1Map;	
		while (currentToken.recognizedStrEquals("and")) {
			loadNextTokenFromLex();
			quadManager.backpatch(boolTermMap.get("true"), quadManager.nextQuad());
			Map<String, List<Integer>> boolFactor2Map = boolFactor();
			boolTermMap.put("false", quadManager.mergeList(boolTermMap.get("false"), boolFactor2Map.get("false")));
			boolTermMap.put("true", boolFactor2Map.get("true"));
		}
		return boolTermMap;
	}

	private Map<String, List<Integer>> boolFactor() throws CutePyException {	
		if (currentToken.recognizedStrEquals("not")) {
			loadNextTokenFromLex();
			if (currentToken.recognizedStrEquals("[")) {
				loadNextTokenFromLex();
				Map<String, List<Integer>> conditionMap = condition();
				if (currentToken.recognizedStrEquals("]")) {
					loadNextTokenFromLex();
					Map<String, List<Integer>> boolFactorMap = new HashMap<String, List<Integer>>();
					boolFactorMap.put("true", conditionMap.get("false"));
					boolFactorMap.put("false", conditionMap.get("true"));
					return boolFactorMap;
				} else {
					throw new CutePyException(getErrorMsg("]"));
				}
			} else {
				throw new CutePyException(getErrorMsg("["));
			}

		} else if (currentToken.recognizedStrEquals("[")) {
			loadNextTokenFromLex();
			Map<String, List<Integer>> conditionMap = condition();
			if (currentToken.recognizedStrEquals("]")) {
				loadNextTokenFromLex();
				return conditionMap;
			} else {
				throw new CutePyException(getErrorMsg("]"));
			}
		} else if (isExpression()) {
			String e1Place = expression();
			String relOp = currentToken.getRecognizedStr();
			if (CharTypes.REL_OPS.contains((currentToken.getRecognizedStr()))) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg("<relation operator>"));
			}
			String e2Place = expression();
			Map<String, List<Integer>> boolFactorMap = new HashMap<String, List<Integer>>();
			boolFactorMap.put("true", quadManager.makeList(quadManager.nextQuad()));	
			quadManager.genQuad(relOp, e1Place, e2Place, "_");
			boolFactorMap.put("false", quadManager.makeList(quadManager.nextQuad()));
			quadManager.genQuad("jump", "_", "_", "_");				
			return boolFactorMap;
		} else {
			throw new CutePyException(getErrorMsg("'not [condition]' or '[condition]' or 'expression relOp expression'"));
		}
	}

	private void callMainPart() throws CutePyException {
		if (!currentToken.getRecognizedStr().equals(FileReader.EOF.toString())) {
			String args[] = { "if", "__name__", "==", "\"__main__\"", ":" };
			for (int i = 0; i < args.length; i++) {
				if (currentToken.getRecognizedStr().equals(args[i])) {
					loadNextTokenFromLex();
				} else {
					throw new CutePyException(getErrorMsg(args[i]));
				}
			}
			int fCodeStartingLbl = quadManager.nextQuad();
			quadManager.genQuad("begin_block", "main", "_", "_");
			mainFunctionCall();
			while (isID(currentToken)) {
				mainFunctionCall();
			}
			quadManager.genQuad("halt", "_", "_", "_");
			quadManager.genQuad("end_block", "main", "_", "_");
			finManager.genFinalCode(fCodeStartingLbl, quadManager.getIntermedCodeMap());
			symbolTable.removeScope();
		}
	}

	public void mainFunctionCall() throws CutePyException {
		if (isMainFuncId()) {
			String mainFuncName = currentToken.getRecognizedStr();
			quadManager.genQuad("call", mainFuncName, "_", "_");
			loadNextTokenFromLex();
			String args[] = { "(", ")", ";" };
			for (int i = 0; i < args.length; i++) {
				if (currentToken.getRecognizedStr().equals(args[i])) {
					loadNextTokenFromLex();
				} else {
					throw new CutePyException(getErrorMsg(args[i]));
				}
			}
		} else {
			throw new CutePyException(getErrorMsg("<identifier> that starts with 'main_'"));
		}
	}

	public boolean isID(Token token) {
		return token.getFamily().equals("identifier");
	}

	private void loadNextTokenFromLex() throws CutePyException {
		currentToken = lex.getToken();
	}
	
	private boolean isMainFuncId() {
		return isID(currentToken) && currentToken.getRecognizedStr().startsWith("main_");
	}

	private String getErrorMsg(String expected) {
		return "[Error in line " + currentToken.getLineNum() + "]"
				+ " expected '" + expected + "' but found '" + currentToken.getRecognizedStr() + "'";
	}
}

class Quad {
	private String operator;
	private String operand1;
	private String operand2;
	private String operand3;

	public Quad(String operator, String operand1, String operand2, String operand3){
		this.operator = operator;
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.operand3 = operand3;
	}

	public Quad(String quadStr) {
		String fields[] = quadStr.split(",", 4);
		operator = fields[0].trim();
		operand1 = fields[1].trim();
		operand2 = fields[2].trim();
		operand3 = fields[3].trim();
	}

	public String getOperator() {
		return operator;
	}
	
	public String getOperand1() {
		return operand1;
	}

	public String getOperand2() {
		return operand2;
	}

	public String getOperand3() {
		return operand3;
	}

	public List<String> getOperandsThatAreVars() {
		List<String> varOperators = new ArrayList<String>();
		varOperators.addAll(Arrays.asList("+","-",":="));
		varOperators.addAll(CharTypes.MUL_OPS);
		varOperators.addAll(CharTypes.REL_OPS);		
		varOperators.add("par");		
		List<String> notVars = Arrays.asList("_", "ret", "cv" );
		List<String> varList = new ArrayList<String>();
		String operands[] = {operand1, operand2, operand3};
		for (int i = 0; i < operands.length; i++) {
			if (varOperators.contains(operator) &&
					(!isNumeric(operands[i]) && !notVars.contains(operands[i]))) {
				varList.add(operands[i]);
			}
		}
		return varList;	
	}
	
	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public void setOperand3(String operand3) {
		this.operand3 = operand3;
	}

	public String toString() {
		return operator + ", " + operand1 + ", " + operand2 + ", " + operand3;
	}
}

class QuadManager {
	private int currentLabel;
	private int tempCounter;
	private String tempPrefix;
	private Map<Integer, Quad> intermedCodeMap; 

	public QuadManager() {
		currentLabel = 99;
		tempCounter = 0;
		intermedCodeMap = new LinkedHashMap<Integer, Quad>();
		tempPrefix = "T_";
	}
	
	public QuadManager(int startingLabel) {
		currentLabel = startingLabel-1;
		tempCounter = 0;
		intermedCodeMap = new LinkedHashMap<Integer, Quad>();
		tempPrefix = "&";
	}
	
	public void setTempPrefix(String prefix) {
		tempPrefix = prefix;	
	}

	public Map<Integer, Quad> getIntermedCodeMap() {
		return intermedCodeMap;
	}

	public void setIntermedCodeMap(Map<Integer, Quad> intermidCodeMap) {
		this.intermedCodeMap = intermidCodeMap;
	}

	public void genQuad(String operator, String operand1, String operand2, String operand3) {
		currentLabel++;
		intermedCodeMap.put(currentLabel, new Quad(operator, operand1, operand2, operand3));
	}

	public int nextQuad() {
		return currentLabel + 1;
	}

	public String newTemp() {
		tempCounter++;
		return tempPrefix + tempCounter;
	}

	public List<Integer> emptyList() {
		return new ArrayList<Integer>();
	}

	public List<Integer> makeList(int label) {
		List<Integer> list = new ArrayList<Integer>();
		list.add(label);
		return list;
	}

	public List<Integer> mergeList(List<Integer> list1, List<Integer> list2) {
		List<Integer> merged = new ArrayList<Integer>();
		merged.addAll(list1);
		merged.addAll(list2);
		return merged;
	}

	public void backpatch(List<Integer> list, int label) {
		for (Integer alabel : list) {
			Quad q = intermedCodeMap.get(alabel);
			q.setOperand3(label + "");
		}
	}

	public String getIntermediateCode() {
		String s = "";
		for (Integer label : intermedCodeMap.keySet()) {
			s += label + ": " + intermedCodeMap.get(label).toString() + "\n";
		}
		return s;
	}
	
	public boolean isValidForTransformationToC() {
		return false;
	}
	
	@Override
	public String toString() {
		return getIntermediateCode();
	}
}


class CTransformer {
	private String cCode = "";
	
	public CTransformer() {}
	
	public String getCcode() {
		return cCode;
	}
	
	public void transformIntermidateCodeToC(Map<Integer, Quad> intermedCode) {
		addToCCode("#include <stdio.h>\n", 0);
		addToCCode("int main(){", 0);
		addToCCode(getDeclerationOfVars(intermedCode), 1);
		for (Integer label : intermedCode.keySet()) {
			Quad quad = intermedCode.get(label);
			addToCCode(getEquivalentCCode(label, quad), 1);
		}
		addToCCode("}", 0);
	}
	
	public String getDeclerationOfVars(Map<Integer, Quad> intermedCode) {
		List<String> varsList = new ArrayList<String>();
		for (Integer label : intermedCode.keySet()) {
			Quad quad = intermedCode.get(label);
			List<String> quadVars = quad.getOperandsThatAreVars();
			for (String var: quadVars) {
				if (!varsList.contains(var)) {
					varsList.add(var);
				}
			} 
		}
		String declareVars = "int ";
		for (String variable : varsList) {
			declareVars += variable+", ";
		}
		String d = declareVars.substring(0, declareVars.length()-2)+";"; 
		return d.replaceAll("&", "T_");
	}

	private String getEquivalentCCode(Integer label, Quad quad) {
		String operator = quad.getOperator();
		String cLine = "";
		if (operator.equals("begin_block")) {
			// do nothing
		} else if (operator.equals("end_block")) {
			// do nothing
		} else if (operator.equals(":=")) {
			cLine = quad.getOperand3() + " = " + quad.getOperand1()+ ";";			
		} else if (CharTypes.ADD_OPS.contains(operator.charAt(0)) || CharTypes.MUL_OPS.contains(operator)) {
			cLine = quad.getOperand3() + " = " + quad.getOperand1() + " " + operator + " " + quad.getOperand2() + ";";
		} else if (CharTypes.REL_OPS.contains(operator)) {
			cLine = "if (" + quad.getOperand1() + " " + operator + " " + quad.getOperand2() +") goto L_" + quad.getOperand3()+ ";"; 
		} else if (operator.equals("jump")) {
			cLine = "goto L_"+ quad.getOperand3()+ ";";
		} else if (operator.equals("out")) {
			cLine = "printf(\"%d\", "+quad.getOperand3()+");";
		} else if (operator.equals("in")) {
			cLine = "scanf(\"%d\", &"+quad.getOperand1()+");";
			return getLblC(label) + cLine + getIntermedAsComment(quad);
		} else if (operator.equals("ret")) {
			cLine = "return "+quad.getOperand1()+";";
		} else if (operator.equals("par")) {
			// do nothing
		} else if (operator.equals("call")) {
			// do nothing
		} else if (operator.equals("halt")) {
			cLine = "return 0;";
		} else {
			return null;
		}
		return getLblC(label) + cLine.replaceAll("&", "T_") + getIntermedAsComment(quad);
	}
	
	private String getLblC(Integer label) {
		return "L_"+label+": ";
	}
	
	private void addToCCode(String s, int tabs) {
		for (int i = 0; i < tabs; i++) {
			cCode += "\t";
		}
		cCode += s + "\n";
	}
	
	private String getIntermedAsComment(Quad quad) {
		return "\t// "+quad;
	}
}

enum DataType {
	INTEGER
}

abstract class Entity {
	protected String name;
	private int foundScope = -1;
	
	public Entity() {}
	
	public Entity(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public int getFoundScope() throws CutePyException {
		if (foundScope == -1) {
			throw new CutePyException("Found scope of entity: ["+name+"] is -1");
		}
		return foundScope;
	}
	
	public void setFoundScope(int foundScope) {
		this.foundScope = foundScope;
	}

}

class EntityFactory {
	public static String MAIN_FUNC = "main_func";
	public static String LOCAL_FUNC = "local_func";
	public static String PARAMETER = "parameter";
	public static String FORMAL_PARAMETER = "formal_parameter";
	public static String VARIABLE = "variable";
	public static String TEMP_VARIABLE= "temp_variable";
	
	
	public static Entity createEntity(String objType, String[] args, Scope lastElement) {
		if (args[0].equals(MAIN_FUNC)) {
			return new MainFunction(args[1]);
		} else if (args[0].equals(LOCAL_FUNC)) {
			return new LocalFunction(args[1]);
		}
		
		
		return null;
		
	}
	
	public static Entity createFormalParameter(String name, ParameterMode mode) {
		return new FormalParameter(name, mode);
	}
	
	public static Entity createParameter(String name, ParameterMode mode) {
		return new Parameter(name, mode);
	}
	
	public static Entity createVariable(String name, int offset) {
		return new Variable(name, offset);
	}
	
	public static Entity createTemporaryVariable(String name, int offset) {
		return new TemporaryVariable(name, offset);
	}
	
	public static Function createLocalFunction(String name) {
		return new LocalFunction(name);
	}
	
	public static Function createLocalFunction(String name, List<String> formalParamsNames){
		List<FormalParameter> formalParams = new ArrayList<FormalParameter>();
		for (String fpName : formalParamsNames) {
			formalParams.add(new FormalParameter(fpName));
		}
		return new LocalFunction(name, formalParams);
	}
	 

	public static List<Parameter> createListOfParameters(List<String> paramsNames) {
		List<Parameter> params = new ArrayList<Parameter>();
		for (int i = 0; i < paramsNames.size(); i++) {
			params.add(new Parameter(paramsNames.get(i), 12+4*i));
		}
		return params;
	}
	
	public static Function createMainFunction(String name) {
		return new MainFunction(name);
	}

}

interface EntityWithOffset {
	int getOffset();
}

/**
 * Parameter as seen in a function call 
 *
 */
class FormalParameter extends Entity {
	protected DataType datatype;
	protected ParameterMode mode;
	
	public FormalParameter(String name) {
		super(name);
		this.mode = ParameterMode.CV;
		this.datatype = DataType.INTEGER;
	}
	
	public FormalParameter(String name, ParameterMode mode) {
		super(name);
		this.mode = mode;
		this.datatype = DataType.INTEGER;
	}

	public ParameterMode getMode() {
		return mode;
	}

	public void setMode(ParameterMode mode) {
		this.mode = mode;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(datatype, mode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormalParameter other = (FormalParameter) obj;
		return datatype == other.datatype && mode == other.mode;
	}

	@Override
	public String toString() {
		return super.name;
	}
	
	
}

abstract class Function extends Entity {
	protected int startingQuad;
	protected int framelength;
	
	public Function(String name) {
		super(name);
	}
	
	public Function(String name, int startingQuad, int framelength) {
		super(name);
		this.startingQuad = startingQuad;
		this.framelength = framelength;
	}

	public int getStartingQuad() {
		return startingQuad;
	}

	public void setStartingQuad(int startingQuad) {
		this.startingQuad = startingQuad;
	}

	public int getFramelength() {
		return framelength;
	}

	public void setFramelength(int framelength) {
		this.framelength = framelength;
	}

	@Override
	public String toString() {
		return "[Function: "+super.name+", SQ=" + startingQuad + ", FL=" + framelength + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(framelength, startingQuad);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Function other = (Function) obj;
		return framelength == other.framelength && startingQuad == other.startingQuad;
	}
	
}

class LocalFunction extends Function {
	private List<FormalParameter> formalParams;
	
	public LocalFunction(String name) {
		super(name);
		formalParams = new ArrayList<FormalParameter>();
	}

	public LocalFunction(String name, int startingQuad, int framelength, List<FormalParameter> formalParams) {
		super(name, startingQuad, framelength);
		this.formalParams = formalParams;
	}
	
	public LocalFunction(String name, List<FormalParameter> formalParams) {
		super(name);
		this.formalParams = formalParams;
	}

	public void addFormalParameter(FormalParameter formalParam) {
		formalParams.add(formalParam);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(formalParams);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalFunction other = (LocalFunction) obj;
		return Objects.equals(formalParams, other.formalParams);
	}
	
	private String getFormalParmasAsStr() {
		if (formalParams.size() > 0) {
			String s= "";
			for (FormalParameter fp : formalParams) {
				s += fp.getName() +", ";
			}
			return s.substring(0, s.lastIndexOf(","));
		}
		return ""; 
	}

	@Override
	public String toString() {
//		return "LocalFunction [formalParams=" + formalParams + "]";
		return "[LocalFunction: "+super.name+", SQ=" + startingQuad + ", FL=" + framelength + ", FPs =<"+getFormalParmasAsStr()+">]";
	}
	
}

class MainFunction extends Function {

	public MainFunction(String name) {
		super(name);
	}

	public MainFunction(String name, int startingQuad, int frameLength) {
		super(name, startingQuad, frameLength);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Function other = (Function) obj;
		return framelength == other.framelength && startingQuad == other.startingQuad;
	}
}

/**
 * Parameter as seen from inside a function
 * @author Panos
 * 
 */
class Parameter extends FormalParameter implements EntityWithOffset{
	private int offset;
	
	public Parameter(String name, ParameterMode mode) {
		super(name, mode);
	}
	
	public Parameter(String name, int offset) {
		super(name, ParameterMode.CV);
		this.offset = offset;
	}
	
	public Parameter(String name, int offset, ParameterMode mode) {
		super(name, mode);
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		return "[Parameter: "+super.name+"/"+offset+"/"+super.mode+"]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(offset);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Parameter other = (Parameter) obj;
		return offset == other.offset;
	}
}

enum ParameterMode {
	CV("cv"),RET("ret");

	private ParameterMode(String string) {}
}

class TemporaryVariable extends Variable {

	public TemporaryVariable(String name) {
		super(name);
	}

	public TemporaryVariable(String name, int offset) {
		super(name, offset);
	}

	@Override
	public String toString() {
		return "[TempVariable: "+super.name+"/"+ datatype + "/"+ offset+"]";
	}

}

class Variable extends Entity implements EntityWithOffset{
	protected DataType datatype;
	protected int offset;

	public Variable(String name) {
		super(name);
		this.datatype = DataType.INTEGER;

	}

	public Variable(String name, int offset) {
		super(name);
		this.datatype = DataType.INTEGER;
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public DataType getDatatype() {
		return datatype;
	}

	@Override
	public String toString() {
		return "[Variable: "+super.name+"/"+ datatype + "/"+ offset+"]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(datatype, offset);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Variable other = (Variable) obj;
		return datatype == other.datatype && offset == other.offset;
	}

}

class Scope {
	private LinkedList<Entity> entityList = new LinkedList<Entity>();
	private Function scopeFunc; 	// used as a reference to update the fields of the func
	
	public Scope() {}
	
	public Scope(Function scopeFunc) {
		super();
		this.scopeFunc = scopeFunc;
	}

	public Scope(Entity... entities) {
		for (int i = 0; i < entities.length; i++) {
			entityList.add(entities[i]);
		}
	}
	
	public void addEntity(Entity entity) throws CutePyException {
		if (findEntity(entity.getName()) != null) {
			throw new CutePyException("Duplicate entity with name " + entity.getName());
			// prosoxi prepei na diaxorisoyme tin addParameter kai tin addEnitty?
		}
		entityList.add(entity);
		
	}

	public Entity findEntity(String entityName) {
		for (Entity entity : entityList) {
			if (entity.getName().equals(entityName)) {
				return entity;
			}
		}
		return null;
	}
	
	
	public int getLengthOfScope() {
		return 12 + entityList.size()*4 - countFunctionsInsideScope()*4;
	}
	
	public void updateStartingQuadOfFunc(int startingQuad) {
		scopeFunc.setStartingQuad(startingQuad);
	}
	
	public void updateFrameLengthOfFunc() {
		scopeFunc.setFramelength(getLengthOfScope());
	}
	
	private int countFunctionsInsideScope() {
		int count = 0;
		for (Entity entity : entityList) {
		    if (entity instanceof Function) {
		        count++;
		    }
		}
		return count;
	}

	@Override
	public String toString() {
		String s = "";
		for (Entity entity : entityList) {
			s += entity + " --- ";
		}
		return (s.length()-3 >0) ? s.substring(0, s.length()-3) : s;
	}

	@Override
	public int hashCode() {
		return Objects.hash(entityList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scope other = (Scope) obj;
		return Objects.equals(entityList, other.entityList);
	}
}

class SymbolTable {
	protected Stack<Scope> scopeStack = new Stack<Scope>();
	private String symbolTableHistory = "";
	
	public SymbolTable() {
		scopeStack = new Stack<Scope>();
		scopeStack.add(new Scope());
	}
	
	public SymbolTable(Stack<Scope> scopeStack) {
		super();
		this.scopeStack = scopeStack;
	}
	
	public void setScopeStack(Stack<Scope> scopeStack) {
		this.scopeStack = scopeStack;
	}
	public String getSymbolTableHistory() {
		return symbolTableHistory;
	}
	
	public void addScope(Function func) {
		scopeStack.push(new Scope(func));
	}
	
	public void removeScope() {
		symbolTableHistory += this+"\n\n";
		scopeStack.pop();
	}
	
	public void addEntity(Entity entity) throws CutePyException {
		scopeStack.lastElement().addEntity(entity);
	}
	
	public Entity searchEntity(String entityName) throws CutePyException {
		for (int i = scopeStack.size() - 1; i >= 0; i--) {
			Scope scope = scopeStack.get(i);
			Entity foundEntity = scope.findEntity(entityName);  
			if (foundEntity != null) {
				foundEntity.setFoundScope(i);
				return foundEntity; 
			}
		}
		throw new CutePyException("Entity '"+entityName+"' not found in symbol table.");
	}
	
	public void updateStartingQuadField(int uptValue) {
		scopeStack.lastElement().updateStartingQuadOfFunc(uptValue);
	}
	
	public void updateFrameLengthField() {
		scopeStack.lastElement().updateFrameLengthOfFunc();
	}
	
	public void addFormalParam(String name, String mode) {
		
	}
	
	public void addFunction(Function func) throws CutePyException {
		addEntity(func);
		addScope(func);
	}
	
	public void addLocalFunction(Function func, List<Parameter> params) throws CutePyException {
		addEntity(func);
		addScope(func);
		for (Parameter p : params) {
			addEntity(p);
		}
	}
		
	public int getOffsetOfNextEntity() {
		return scopeStack.lastElement().getLengthOfScope();
	}
	
	public int getLastScopeNum() {
		return scopeStack.size()-1;
	}
	

	@Override
	public String toString() {
		String s = "SymbolTable\n";
		for (int i = scopeStack.size() - 1; i > 0; i--) {
			s += "Scope "+ i + " "+scopeStack.get(i) +"\n";
			s += "   | \n";
		}
		
		s += "Scope "+ 0 + " "+scopeStack.get(0) +"\n";
		return s;
	}

}

class FinalCodeManager {
	private String finalCode = ".data\n\nstr_nl: .asciz \"\\n\"\n\n.text\n\n";
	private SymbolTable symbolTable;
	private String compiledFuncName; 	// used to determine the relation between the callee and called function
	private String funcNameToBeCalled;
	private int paramNum = 1;
	
	public FinalCodeManager() {}
	
	public FinalCodeManager(SymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}
	
	public void emptyFinalCode() {
		finalCode = "";
	}
	
	public String getFinalCode() {
		return finalCode;
	}
	
	public void initMainFinalCode() {
		finalCode += "L0:\n\tj main\n";
	}
	
	public void genFinalCode(int startingLabel, Map<Integer, Quad> intermedCode) throws CutePyException {
		List<Integer> keys = new ArrayList<>(intermedCode.keySet());	
		for (int i = 0; i < keys.size(); i++) {
			Integer key = keys.get(i);
			if (key >= startingLabel) {
				if (intermedCode.get(key).getOperator().equals("par")) {
					int j = i;
					Integer key2 = keys.get(j);
					Quad quad  = intermedCode.get(key2);
					while (!quad.getOperator().equals("call")) {
						j++;
						key2 = keys.get(j);
						quad = intermedCode.get(key2);
					}
					funcNameToBeCalled= quad.getOperand1();
				}
				transformQuadToFinalCode(key ,intermedCode.get(key)); 
			}
		}
	}
	
	public void transformQuadToFinalCode(int label, Quad quad) throws CutePyException {
		String operator = quad.getOperator();
		if (operator.equals("begin_block")) {
			compiledFuncName = quad.getOperand1();
			if (quad.getOperand1().equals("main")) {
				addLabelToFinalCodeFunc(quad.getOperand1(), label, quad);
				addToFinalCode("addi sp, sp, 12");
				addToFinalCode("mv gp, sp");
			} else {
				addLabelToFinalCodeFunc(quad.getOperand1(), label, quad);
				addToFinalCode("sw ra, -0(sp)");
			}
			return;
		} else if (operator.equals("jump")) {
			addLabelToFinalCode(label, quad);
			addToFinalCode("j "+"L"+quad.getOperand3());				
			return;
		}
		
		addLabelToFinalCode(label, quad);
		if (operator.equals("end_block")) {
			if (quad.getOperand1().equals("main")) {

			} else {
				addToFinalCode("lw ra, (sp)");
				addToFinalCode("jr ra");
			}
			
		} else if (operator.equals(":=")) {
			loadvr(quad.getOperand1(), "t0");		
			storerv("t0", quad.getOperand3());				
		} else if (CharTypes.ADD_OPS.contains(operator.charAt(0)) || CharTypes.MUL_OPS.contains(operator)) {
			loadvr(quad.getOperand1(),"t1");
			loadvr(quad.getOperand2(),"t2");
			if (operator.equals("+")) {
				addToFinalCode("add t1, t1, t2"); 	// add rd r1 r2 = rd = r1 + r2
			} else if (operator.equals("-")) {
				addToFinalCode("sub t1, t1, t2");
			} else if  (operator.equals("*")) {
				addToFinalCode("mul t1, t1, t2");
			} else if  (operator.equals("//")) {
				addToFinalCode("div t1, t1, t2");
			}
			storerv("t1",quad.getOperand3());
		} else if (CharTypes.REL_OPS.contains(operator)) {
			loadvr(quad.getOperand1(), "t1");
			loadvr(quad.getOperand2(), "t2");			
			if (operator.equals("==")) { 
				addToFinalCode("beq t1, t2, L"+quad.getOperand3());
			} else if (operator.equals("!=")) {
				addToFinalCode("bne t1, t2, L"+quad.getOperand3());
			} else if (operator.equals(">")) {
				addToFinalCode("bgt t1, t2, L"+quad.getOperand3());
			} else if (operator.equals("<")) {
				addToFinalCode("blt t1, t2, L"+quad.getOperand3());
			} else if (operator.equals(">=")) {
				addToFinalCode("bge t1, t2, L"+quad.getOperand3());
			} else if (operator.equals("<=")) {
				addToFinalCode("ble t1, t2, L"+quad.getOperand3());
			}
		} else if (operator.equals("out")) {
			loadvr(quad.getOperand3(), "a0");
			addToFinalCode("li a7, 1");
			addToFinalCode("ecall");
			addToFinalCode("la a0, str_nl"); 	// go to next line !!!
			addToFinalCode("li a7, 4");
			addToFinalCode("ecall");
		} else if (operator.equals("in")) {
			addToFinalCode("li a7, 5");
			addToFinalCode("ecall");
			EntityWithOffset entity = (EntityWithOffset) symbolTable.searchEntity(quad.getOperand1());
			addToFinalCode("sw a0, -"+entity.getOffset()+"(sp)");
			
		} else if (operator.equals("ret")) {
			loadvr(quad.getOperand1(), "t1");
			addToFinalCode("lw t0, -8(sp)" );
			addToFinalCode("sw t1, (t0)" );
			addToFinalCode("lw ra, (sp)" );
			addToFinalCode("jr ra" );
		} else if (operator.equals("par")) {
			Function funcToBeCalled = (Function) symbolTable.searchEntity(funcNameToBeCalled);	// this must be the function  that will be called later!!!
			if (paramNum == 1) {
				addToFinalCode("addi fp, sp, "+funcToBeCalled.getFramelength());
			}
			
			if (quad.getOperand2().equals("cv")) {
				loadvr(quad.getOperand1(), "t0");
				addToFinalCode("sw t0, -"+(12+4*(paramNum-1))+"(fp)"); 	// paramNum starts from 1 so we need that paramNum-1 
			} else if (quad.getOperand2().equals("ret")) {
				TemporaryVariable tempVar = (TemporaryVariable) symbolTable.searchEntity(quad.getOperand1());
				addToFinalCode("addi t0, sp, -"+tempVar.getOffset());
				addToFinalCode("sw t0, -"+8+"(fp)");
			} else {
				throw new CutePyException("Unknown type of parameter with name "+quad.getOperand1());
			}

			paramNum ++;
		} else if (operator.equals("call")) {
			paramNum = 1;
			Function entity = (Function) symbolTable.searchEntity(quad.getOperand1());
			
			if (quad.getOperand1().startsWith("main_")) {
				addToFinalCode("addi fp, sp, "+entity.getFramelength());
				addToFinalCode("sw sp, -4(fp)");
				addToFinalCode("addi sp, sp, "+ entity.getFramelength());
				addToFinalCode("jal "+quad.getOperand1());
				addToFinalCode("addi sp, sp, -"+entity.getFramelength());
				return;
			}
			
			Function calleeEntity = (Function) symbolTable.searchEntity(compiledFuncName);
			if (calleeEntity.getFoundScope() == entity.getFoundScope()) {
				addToFinalCode("lw t0, -4(sp)");
				addToFinalCode("sw t0, -4(fp)");
			} else if (calleeEntity.getFoundScope() < entity.getFoundScope()) {
				addToFinalCode("sw sp, -4(fp)");
			} else {
				throw new CutePyException("Called and callee functions found in wrong scopes !!!");
			}
			addToFinalCode("addi sp, sp, "+ entity.getFramelength());
			addToFinalCode("jal "+quad.getOperand1());
			addToFinalCode("addi sp, sp, -"+entity.getFramelength());
			
		} else if (operator.equals("halt")) {
			addToFinalCode("li a0, 0");
			addToFinalCode("li a7, 93");
			addToFinalCode("ecall");
		}
	}

	private boolean isNumber(String operand1) {
	    if (operand1 == null) {
	        return false;
	    }
	    try {
	        Integer.parseInt(operand1);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}

	public void gnvlcode(String variableName) throws CutePyException {
		Entity entity =  symbolTable.searchEntity(variableName);
		int foundScope = entity.getFoundScope();
		int lastScope = symbolTable.getLastScopeNum();
		int scopesToClimb = lastScope - foundScope;
		
		addToFinalCode("lw t0, -4(sp)");
		for (int i = 0; i < scopesToClimb-1; i++) {
			addToFinalCode("lw t0, -4(t0)");
		}
		addToFinalCode("addi t0, t0, -"+ ((EntityWithOffset) entity).getOffset());
		
		
	}
	
	public void loadvr(String varName, String reg) throws CutePyException {
		if (isNumber(varName)) {
			addToFinalCode("li "+reg+", "+varName);
			return;
		}		
		Entity entity = symbolTable.searchEntity(varName);
		if (entity instanceof TemporaryVariable ||
				isLocalVariable(entity) || isParamPassedByValue(entity)) {
			addToFinalCode("lw "+reg+", -"+ ((EntityWithOffset) entity).getOffset() + "(sp)");
		} else if (isAncestorsLocalVariable(entity) || isAncestorsParamByValue(entity)) {
			gnvlcode(varName);
			addToFinalCode("lw "+reg+", (t0)");
		} else {
			throw new CutePyException("FinalCodeManager.laodvr("+varName+","+reg+") unknwon load case");
		}		 
	}
	
	public void storerv(String reg, String varName) throws CutePyException {	
		Entity entity = symbolTable.searchEntity(varName);
		if (entity instanceof TemporaryVariable ||
				isLocalVariable(entity) || isParamPassedByValue(entity)) {
			addToFinalCode("sw "+reg+", -"+ ((EntityWithOffset) entity).getOffset() + "(sp)");
		} else if (isAncestorsLocalVariable(entity) || isAncestorsParamByValue(entity)) {
			// here you have to protect the data of reg from overwriting from gnvlcode
			// so move them to another reg and restore the data
			addToFinalCode("mv t5, "+reg);
			gnvlcode(varName);
			addToFinalCode("sw t5, ("+reg+")");
		} else {
			throw new CutePyException("FinalCodeManager.storerv("+varName+","+reg+") unknwon store case");
		}
			 
	}
	
	private boolean isAncestorsParamByValue(Entity entity) throws CutePyException  {
		if (entity instanceof Parameter && !isOnTheLastScope(entity) &&
				((Parameter) entity).getMode().equals(ParameterMode.CV)) {
			return true;
		}
		return false;
	}
	
	private boolean isAncestorsLocalVariable(Entity entity) throws CutePyException {
		if (entity.getFoundScope() - symbolTable.getLastScopeNum() < 0 /* different scopes*/  
				&& entity instanceof Variable) {
			return true;
		}
		return false;
	}
	
	private boolean isLocalVariable(Entity entity) throws CutePyException {
		if (isOnTheLastScope(entity)
				&& entity instanceof Variable) {
			return true;
		}
		return false;
	}

	private boolean isParamPassedByValue(Entity entity) throws CutePyException {
		if (entity instanceof Parameter && isOnTheLastScope(entity) &&
				((Parameter) entity).getMode().equals(ParameterMode.CV)) {
			return true;
		}
		return false;
	}
	
	private boolean isOnTheLastScope(Entity entity) throws CutePyException {
		return entity.getFoundScope() - symbolTable.getLastScopeNum() == 0;
	}
	
	private void addToFinalCode(String str) {
		finalCode += "\t"+str+"\n"; 
	}
	
	private void addLabelToFinalCode(int lbl, Quad quad) {
		finalCode += "\nL"+lbl+":\t\t\t# "+lbl+": "+quad+"\n";
	}
	
	private void addLabelToFinalCodeFunc(String funcName, int lbl, Quad quad) {
		finalCode += "\n"+funcName+":\t\t\t# "+lbl+": "+quad+"\n";
	}
}





