package syntax;

import exceptions.CutePyException;
import lex.CharTypes;
import lex.FileReader;
import lex.LexAnalyser;
import lex.Token;

public class SyntaxAnalyser {
	private LexAnalyser lex;
	private Token currentToken;
	private String recognisedCode = ""; // used for debugging

	public SyntaxAnalyser(LexAnalyser lex) {
		this.lex = lex;
	}

	public Token getCurrentToken() {
		return currentToken;
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
				loadNextTokenFromLex();
				String args[] = { "(", ")", ":", "#{" };
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
				statements();

				if (currentToken.getRecognizedStr().equals("#}")) {
					loadNextTokenFromLex();
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
		if (currentToken.getRecognizedStr().equals("def")) {
			loadNextTokenFromLex();
			if (isID(currentToken) && !isMainFuncId()) {
				loadNextTokenFromLex();
				if (currentToken.recognizedStrEquals("(")) {
					loadNextTokenFromLex();
					idList();
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
					statements();

					if (currentToken.getRecognizedStr().equals("#}")) {
						loadNextTokenFromLex();
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
		System.out.println("declarations() " + currentToken.getRecognizedStr());
		while (currentToken.recognizedStrEquals("#declare")) {
			loadNextTokenFromLex();
			declerationLine();
		}
	}

	private void declerationLine() throws CutePyException {
		System.out.println("declerationLine() " + currentToken.getRecognizedStr());
		idList();
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

	private void statements() throws CutePyException {
		System.out.println("statements() " + currentToken.getRecognizedStr());
		statement();
		while (isStatement()) {
			statement();
		}
	}

	private boolean isStatement() {
		return isSimpleStatement() || isStructuredStatement();
	}

	private void simpleStatement() throws CutePyException {
		System.out.println("simpleStatement() " + currentToken.getRecognizedStr());
		if (isID(currentToken)) {
			loadNextTokenFromLex();
			assignmentStat();
		} else if (currentToken.recognizedStrEquals("print")) {
			printStat();
		} else if (currentToken.recognizedStrEquals("return")) {
			returnStat();
		} else {
			// TODO will never be reached ?
			throw new CutePyException(getErrorMsg("<identifier> or 'print' or 'return'"));
		}
	}

	private boolean isSimpleStatement() {
		return isID(currentToken) || currentToken.recognizedStrEquals("print")
				|| currentToken.recognizedStrEquals("return");
	}

	private void structuredStatement() throws CutePyException {
		System.out.println("structuredStatement() " + currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("if")) {
			loadNextTokenFromLex();
			ifStat();
		} else if (currentToken.recognizedStrEquals("while")) {
			loadNextTokenFromLex();
			whileStat();
		} else {
			// TODO will never be reached ?
			throw new CutePyException(getErrorMsg("expected a stuctured statement 'if' or 'while'"));
		}
	}

	private boolean isStructuredStatement() {
		return currentToken.recognizedStrEquals("if") || currentToken.recognizedStrEquals("while");
	}

	private void assignmentStat() throws CutePyException {
		System.out.println("assignmentStat() " + currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("=")) {
			loadNextTokenFromLex();
			if (isExpression()) {
				expression();
				if (currentToken.recognizedStrEquals(";")) {
					loadNextTokenFromLex();
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
		expression();
		String args2[] = { ")", ";" };
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args2[i])) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(args2[i]));
			}
		}
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
		expression();
		String args2[] = { ")", ";" };
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args2[i])) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(args2[i]));
			}
		}
	}

	private void ifStat() throws CutePyException {
		System.out.println("ifStat() " + currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			condition();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
				if (currentToken.recognizedStrEquals(":")) {
					loadNextTokenFromLex();
					if (currentToken.recognizedStrEquals("#{")) {
						loadNextTokenFromLex();
						statements();
						if (currentToken.recognizedStrEquals("#}")) {
							loadNextTokenFromLex();
							if (currentToken.recognizedStrEquals("else")) {
								elsePart();
							}
						} else {
							throw new CutePyException(getErrorMsg("#}"));
						}
					} else if (isStatement()) {
						statement();
						if (currentToken.recognizedStrEquals("else")) {
							elsePart();
						}
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

	private void whileStat() throws CutePyException {
		System.out.println("whileStat() " + currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			condition();
			if (currentToken.recognizedStrEquals(")")) {
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
				throw new CutePyException(getErrorMsg(")"));
			}
		} else {
			throw new CutePyException(getErrorMsg("("));
		}
	}

	private void idList() throws CutePyException {
		System.out.println("idList() " + currentToken.getRecognizedStr());
		if (isID(currentToken)) {
			loadNextTokenFromLex();
			while (currentToken.recognizedStrEquals(",")) {
				loadNextTokenFromLex();
				if (isID(currentToken)) {
					loadNextTokenFromLex();
				} else {
					throw new CutePyException(getErrorMsg("identifier after ','"));
				}
			}
		}
		// dont need to throw exception here
	}

	private void expression() throws CutePyException {
		System.out.println("expression() " + currentToken.getRecognizedStr());
		optionalSign();
		term();
		while (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))) {
			loadNextTokenFromLex();
			term();
		}
	}

	private void term() throws CutePyException {
		System.out.println("term() " + currentToken.getRecognizedStr());
		factor();
		while (CharTypes.MUL_OPS.contains(currentToken.getRecognizedStr())) {
			loadNextTokenFromLex();
			factor();
		}
	}

	private void factor() throws CutePyException {
		System.out.println("factor() " + currentToken.getRecognizedStr());
		if (isInteger(currentToken.getRecognizedStr())) {
			loadNextTokenFromLex();
		} else if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			expression();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(")"));
			}
		} else if (isID(currentToken)) {
			loadNextTokenFromLex();
			idTail();
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

	private void idTail() throws CutePyException {
		System.out.println("idTail() " + currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			actualParList();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg(")"));
			}
		}
		// dont need to throw exception here
	}

	private void actualParList() throws CutePyException {
		System.out.println("actualParList() " + currentToken.getRecognizedStr());
		if (isExpression()) {
			expression();
			while (currentToken.recognizedStrEquals(",")) {
				loadNextTokenFromLex();
				expression();
			}			
		}
	}

	private void optionalSign() throws CutePyException {
		System.out.println("optionalSign " + currentToken.getRecognizedStr());
		if (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))) {
			loadNextTokenFromLex();
		}
	}

	private void condition() throws CutePyException {
		System.out.println("condition() " + currentToken.getRecognizedStr());
		boolTerm();
		while (currentToken.recognizedStrEquals("or")) {
			loadNextTokenFromLex();
			boolFactor();
		}
	}

	private void boolTerm() throws CutePyException {
		System.out.println("boolTerm() " + currentToken.getRecognizedStr());
		boolFactor();
		while (currentToken.recognizedStrEquals("and")) {
			loadNextTokenFromLex();
			boolFactor();
		}
	}

	private void boolFactor() throws CutePyException {
		System.out.println("boolFactor() " + currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("not")) {
			loadNextTokenFromLex();
			if (currentToken.recognizedStrEquals("[")) {
				loadNextTokenFromLex();
				condition();
				if (currentToken.recognizedStrEquals("]")) {
					loadNextTokenFromLex();
				} else {
					throw new CutePyException(getErrorMsg("]"));
				}
			} else {
				throw new CutePyException(getErrorMsg("["));
			}

		} else if (currentToken.recognizedStrEquals("[")) {
			loadNextTokenFromLex();
			condition();
			if (currentToken.recognizedStrEquals("]")) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg("]"));
			}
		} else if (isExpression()) {
			expression();
			if (CharTypes.REL_OPS.contains((currentToken.getRecognizedStr()))) {
				loadNextTokenFromLex();
			} else {
				throw new CutePyException(getErrorMsg("<relation operator>"));
			}
			expression();
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
			mainFunctionCall();
			while (isID(currentToken)) {
				mainFunctionCall();
			}
		}
	}

	public void mainFunctionCall() throws CutePyException {
		if (isMainFuncId()) {
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
