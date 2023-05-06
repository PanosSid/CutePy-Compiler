package syntax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.CutePyException;
import finalcode.FinalCodeManager;
import intermediatecode.QuadManager;
import lex.CharTypes;
import lex.FileReader;
import lex.LexAnalyser;
import lex.Token;
import symboltable.SymbolTable;
import symboltable.entities.EntityFactory;

public class SyntaxAnalyser {
	private LexAnalyser lex;
	private Token currentToken;
	private QuadManager quadManager;
	private SymbolTable symbolTable;
	private FinalCodeManager finManager;
	
	private String recognisedCode = ""; // used for debugging
	private boolean localFunctionhasReturn = false;
	
	public SyntaxAnalyser(LexAnalyser lex) {
		this.lex = lex;
		quadManager = new QuadManager();
		symbolTable = new SymbolTable();
	}
	
	public SyntaxAnalyser(LexAnalyser lex, QuadManager quadManager) {
		this.lex = lex;
		this.quadManager = quadManager;
	}
	
	public SyntaxAnalyser(LexAnalyser lex, QuadManager quadManager, SymbolTable symbolTable) {
		this.lex = lex;
		this.quadManager = quadManager;
		this.symbolTable = symbolTable;
	}

	public SyntaxAnalyser(LexAnalyser lex, QuadManager quadManager, SymbolTable symbolTable,
			FinalCodeManager finManager) {
		this.lex = lex;
		this.quadManager = quadManager;
		this.symbolTable = symbolTable;
		this.finManager = finManager;
		finManager.initMainFinalCode();
	}

	public QuadManager getQuadManager() {
		return quadManager;
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
		System.out.println("startRule() " + currentToken.getRecognizedStr());
		defMainPart();
		callMainPart();
	}

	private void defMainPart() throws CutePyException {
		System.out.println("defMainPart() " + currentToken.getRecognizedStr());
		defMainFunction();
		while (currentToken.recognizedStrEquals("def")) {
			defMainFunction();
		}
	}

	private void defMainFunction() throws CutePyException {
		System.out.println("defMainFunction() " + currentToken.getRecognizedStr());
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
		System.out.println("defFunction() " + currentToken.getRecognizedStr());
		localFunctionhasReturn = false;
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
					checkIfHasAReturnStat(funcName);
					quadManager.genQuad("end_block", funcName, "_", "_");
					symbolTable.updateFrameLengthField();

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

	private void checkIfHasAReturnStat(String funcName) throws CutePyException {
		if (localFunctionhasReturn == false) {
			throw new CutePyException("Error: the local function: '"+ funcName +"' does not return a value");
		}
		localFunctionhasReturn = false;
	}

	private void declarations() throws CutePyException {
		System.out.println("declarations() " + currentToken.getRecognizedStr());
		while (currentToken.recognizedStrEquals("#declare")) {
			loadNextTokenFromLex();
//			symbolTable.addEntity(EntityFactory.createVariable(currentToken.getRecognizedStr(), symbolTable.getOffsetOfNextEntity()));
			declerationLine();
		}
	}

	private void declerationLine() throws CutePyException {
		System.out.println("declerationLine() " + currentToken.getRecognizedStr());
		List<String> vars = idList();
		for (String var : vars) {
			symbolTable.addEntity(EntityFactory.createVariable(var, symbolTable.getOffsetOfNextEntity()));
		}
	}

	private void statement() throws CutePyException {
		System.out.println("statement() " + currentToken.getRecognizedStr());
		if (isSimpleStatement()) {
			simpleStatement();
		} else if (isStructuredStatement()) {
			structuredStatement();
			
		} else {
			throw new CutePyException(getErrorMsg("at least one simple or structured statement"));
		}
	}

	public void statements() throws CutePyException {
		System.out.println("statements() " + currentToken.getRecognizedStr());
		statement();
		while (isStatement()) {
			statement();
		}
	}

	private boolean isStatement() {
		return isSimpleStatement() || isStructuredStatement();
	}

	private String simpleStatement() throws CutePyException {
		System.out.println("simpleStatement() " + currentToken.getRecognizedStr());
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
			// TODO will never be reached ?
			throw new CutePyException(getErrorMsg("<identifier> or 'print' or 'return'"));
		}
	}

	private boolean isSimpleStatement() {
		return isID(currentToken) || currentToken.recognizedStrEquals("print")
				|| currentToken.recognizedStrEquals("return");
	}

	public String structuredStatement() throws CutePyException {
		System.out.println("structuredStatement() " + currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("if")) {
			loadNextTokenFromLex();
			ifStat();
			return "if";
		} else if (currentToken.recognizedStrEquals("while")) {
			loadNextTokenFromLex();
			whileStat();
			return "while";
		} else {
			// TODO will never be reached ?
			throw new CutePyException(getErrorMsg("expected a stuctured statement 'if' or 'while'"));
		}
	}

	private boolean isStructuredStatement() {
		return currentToken.recognizedStrEquals("if") || currentToken.recognizedStrEquals("while");
	}
	
	// TODO dikos mou autosxediasmos gia ton endiameso kodika
	private void assignmentStat() throws CutePyException {
		System.out.println("assignmentStat() " + currentToken.getRecognizedStr());
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
		System.out.println("printStat() " + currentToken.getRecognizedStr());
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
		System.out.println("returnStat() " + currentToken.getRecognizedStr());
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
		localFunctionhasReturn = true;
	}

	private void ifStat() throws CutePyException {
		System.out.println("ifStat() " + currentToken.getRecognizedStr());
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
								quadManager.backpatch(ifList, quadManager.nextQuad());
							}
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
		System.out.println("elsePart() " + currentToken.getRecognizedStr());
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
		System.out.println("whileStat() " + currentToken.getRecognizedStr());
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
		System.out.println("idList() " + currentToken.getRecognizedStr());
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
		System.out.println("expression() " + currentToken.getRecognizedStr());
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
		System.out.println("term() " + currentToken.getRecognizedStr());
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
		System.out.println("factor() " + currentToken.getRecognizedStr());
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
			return fPlace; 	//TODO check maybe its is not correct !!
		} else {
			throw new CutePyException(getErrorMsg("<identifier> or <integer> or '('"));
		}
	}

	private boolean isInteger(String str) {
		System.out.println("isInteger() " + currentToken.getRecognizedStr());
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private String idTail(String funcName) throws CutePyException {
		System.out.println("idTail() " + currentToken.getRecognizedStr());
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
		System.out.println("actualParList() " + currentToken.getRecognizedStr());
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
		System.out.println("optionalSign " + currentToken.getRecognizedStr());
		if (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))) {
			String sign = currentToken.getRecognizedStr(); 
			loadNextTokenFromLex();
			return sign;
		}
		return "";
		
	}

	public Map<String, List<Integer>> condition() throws CutePyException {
		System.out.println("condition() " + currentToken.getRecognizedStr());
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
		System.out.println("boolTerm() " + currentToken.getRecognizedStr());
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
		System.out.println("boolFactor() " + currentToken.getRecognizedStr());
		
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
			/*
			 * TODO before this maybe i have to check if e1Place of e2Place is a negative variable
			 * and have to produce genQuad(-, 0, ePalce_without_sign, newTemp());
			 */
			boolean hasNegSign = false;
			String negTempE1 = null;
			if (e1Place.charAt(0) == '-') {
				hasNegSign = true;
				e1Place = e1Place.substring(1);	// ommit the negative sign
				negTempE1 = quadManager.newTemp();
				quadManager.genQuad("-", "0", e1Place, negTempE1);
			}
			negTempE1 = e1Place;
			
			String negTempE2 = null;
			if (e2Place.charAt(0) == '-') {
				hasNegSign = true;
				e2Place = e2Place.substring(1);	// ommit the negative sign
				negTempE2 = quadManager.newTemp();
				quadManager.genQuad("-", "0", e2Place, negTempE2);
			}
//			negTempE2 = e2Place;
			
			if (hasNegSign == true) {
				quadManager.genQuad(relOp, negTempE1, negTempE2, "_");
				boolFactorMap.put("false", quadManager.makeList(quadManager.nextQuad()));
				quadManager.genQuad("jump", "_", "_", "_");			
			} else {
				/* this part of code was beforethe negative sign checks */
				quadManager.genQuad(relOp, e1Place, e2Place, "_");
				boolFactorMap.put("false", quadManager.makeList(quadManager.nextQuad()));
				quadManager.genQuad("jump", "_", "_", "_");				
			}
			
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
			
			System.out.println(finManager.getFinalCode());
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
		System.out.println("isID() " + currentToken.getRecognizedStr());
		return token.getFamily().equals("identifier");
	}

	private void loadNextTokenFromLex() throws CutePyException {
		currentToken = lex.getToken();
		recognisedCode += currentToken.getRecognizedStr() + "\n";
//		System.out.println(recognisedCode);
	}
	
	private boolean isMainFuncId() {
		return isID(currentToken) && currentToken.getRecognizedStr().startsWith("main_");
	}

	private String getErrorMsg(String expected) {
		return "[Error in line " + currentToken.getLineNum() + "]"
				+ " expected '" + expected + "' but found '" + currentToken.getRecognizedStr() + "'";
	}
}
