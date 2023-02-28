package syntax;

import lex.CharTypes;
import lex.FileReader;
import lex.LexAnalyser;
import lex.Token;

public class SyntaxAnalyser {
	private LexAnalyser lex;
	private Token currentToken;
	private Token prevToken;
	private String recognisedCode =""; 	// used for debugging
	
	public SyntaxAnalyser(LexAnalyser lex) {
		this.lex = lex;
	}
		
	public Token getCurrentToken() {
		return currentToken;
	}

	public Token getPrevToken() {
		return prevToken;
	}
	
	public void analyzeSyntax() throws Exception {
		loadNextTokenFromLex();
		startRule();
		
//		try {
//			startRule();
//			System.out.println("Compilation successfully completed");
//		} catch (Exception e) {
//			System.out.println("Compilation FAILED");
//
//		}
	}
	
	
	private void startRule() throws Exception {
		System.out.println("startRule() "+ currentToken.getRecognizedStr());
		defMainPart();
		callMainPart();
	}
	
	private void defMainPart() throws Exception {
		System.out.println("defMainPart() "+ currentToken.getRecognizedStr());
		defMainFunction();
		while (currentToken.recognizedStrEquals("def")) {
			defMainFunction();
		}
	}

	private void defMainFunction() throws Exception { // TODO ERROR MSGS
		System.out.println("defMainFunction() "+ currentToken.getRecognizedStr());
		if (currentToken.getRecognizedStr().equals("def")) {
			loadNextTokenFromLex();
			if (isID(currentToken)) {
				loadNextTokenFromLex();
				String args[] = {"(",")",":","#{"};
				for (int i = 0; i < args.length; i++) {
					if (currentToken.getRecognizedStr().equals(args[i])) {
						loadNextTokenFromLex();
					} else {
						throw new Exception("Error expected '"+args[i]+"' found '"+currentToken.getRecognizedStr()+"'");
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
					throw new Exception("Error expected '#}' found '"+currentToken.getRecognizedStr()+"'");
				}
			}
		}
		
	}
	
	private void defFunction() throws Exception {	// TODO ERROR MSGS
		System.out.println("defFunction() "+ currentToken.getRecognizedStr());
		if (currentToken.getRecognizedStr().equals("def")) {
			loadNextTokenFromLex();
			if (isID(currentToken)) {
				loadNextTokenFromLex();
				if (currentToken.recognizedStrEquals("(")) {
					loadNextTokenFromLex();
					idList();
					String args[] = {")",":","#{"};
					for (int i = 0; i < args.length; i++) {
						if (currentToken.getRecognizedStr().equals(args[i])) {
							loadNextTokenFromLex();
						} else {
							throw new Exception("Error expected '"+args[i]+"' found '"+currentToken.getRecognizedStr()+"'");
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
						throw new Exception("[Error: at line" + currentToken.getLineNum()+ "] expected '#}' but found '" +  currentToken.getRecognizedStr()+ "' ");
					}
				} else { 
					throw new Exception("[Error: at line" + currentToken.getLineNum()+ "] expected '(' but found '" +  currentToken.getRecognizedStr()+ "' ");
				}
			} else {
				throw new Exception("Error expected identifier but found '"+currentToken.getRecognizedStr()+"'");
			}
		} else {
			throw new Exception("[Error: at line" + currentToken.getLineNum()+ "] expected 'def' but found '" +  currentToken.getRecognizedStr()+ "' ");
		}
	}
	
	private void declarations() throws Exception {
		System.out.println("declarations() "+ currentToken.getRecognizedStr());
		while (currentToken.recognizedStrEquals("#declare")) {
			loadNextTokenFromLex();
			declerationLine();
		}
	}
	
	private void declerationLine() throws Exception {
		System.out.println("declerationLine() "+ currentToken.getRecognizedStr());
		idList();
	}
	
	private void statement() throws Exception {
		System.out.println("statement() "+ currentToken.getRecognizedStr());
		if (isSimpleStatement()) {
			simpleStatement();
		} else if (isStructuredStatement()) {
			structuredStatement();			
		} else {
			throw new Exception("lathos stin statement");
		}
		
	}
	
	private void statements() throws Exception {
		System.out.println("statements() "+ currentToken.getRecognizedStr());
		statement();
		while (isStatement()) {
			statement();
		}
	}
	
	private boolean isStatement() {
		return isSimpleStatement() || isStructuredStatement();
	}
	
	private void simpleStatement() throws Exception {
		System.out.println("simpleStatement() "+ currentToken.getRecognizedStr());
		if (isID(currentToken)) {
			loadNextTokenFromLex();
			assignmentStat();
		} else if (currentToken.recognizedStrEquals("print")) {
			printStat();
		} else if (currentToken.recognizedStrEquals("return")) {
			returnStat();
		}
	}
	
	private boolean isSimpleStatement() {
		return isID(currentToken) || currentToken.recognizedStrEquals("print")
				|| currentToken.recognizedStrEquals("return");
	}
	
	private void structuredStatement() throws Exception {
		System.out.println("structuredStatement() "+ currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("if")) {
			loadNextTokenFromLex();
			ifStat();
		} else if (currentToken.recognizedStrEquals("while")) {
			loadNextTokenFromLex();
			whileStat();
		}
	}
	
	private boolean isStructuredStatement() {
		return currentToken.recognizedStrEquals("if") || 
				currentToken.recognizedStrEquals("while");
	}
	
	private void assignmentStat() throws Exception {
		System.out.println("assignmentStat() "+ currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("=")) {
			loadNextTokenFromLex();
			if (isExpression()) {
				expression();
				if (currentToken.recognizedStrEquals(";")) {
					loadNextTokenFromLex();
				}
			} else if (currentToken.recognizedStrEquals("int")) {
				loadNextTokenFromLex();
				String nextTks[] = {"(", "input", "(", ")", ")", ";"};
				for (int i = 0; i < nextTks.length; i++) {
					if (currentToken.recognizedStrEquals(nextTks[i])) {
						loadNextTokenFromLex();
					} else {
						throw new Exception("Error expected '"+nextTks[i]+"' found '"+currentToken.getRecognizedStr()+"'");
					}
				}
			} else {
				throw new Exception("[Error] expected '= integer' or '= int(input());' but found '="+currentToken.getRecognizedStr()+"'");
			}
		}
	}

	private boolean isExpression() {
		return CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))||
				isFactor();
	}
	
	private boolean isFactor() {
		return isInteger(currentToken.getRecognizedStr()) ||
				currentToken.recognizedStrEquals("(") || isID(currentToken);
	}
	
	private void printStat() throws Exception {
		System.out.println("printStat() "+ currentToken.getRecognizedStr());
		String args1[] = {"print", "("};
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args1[i])) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("Error expected '"+args1[i]+"' found '"+currentToken.getRecognizedStr()+"'");
			}
		}
		expression();
		String args2[] = {")", ";"};
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args2[i])) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("Error expected '"+args2[i]+"' found '"+currentToken.getRecognizedStr()+"'");
			}	
		}
	}
	
	private void returnStat() throws Exception {
		System.out.println("returnStat() "+ currentToken.getRecognizedStr());
		String args1[] = {"return", "("};
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args1[i])) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("Error expected '"+args1[i]+"' found '"+currentToken.getRecognizedStr()+"'");
			}
		}
		expression();
		String args2[] = {")", ";"};
		for (int i = 0; i < args1.length; i++) {
			if (currentToken.recognizedStrEquals(args2[i])) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("Error expected '"+args2[i]+"' found '"+currentToken.getRecognizedStr()+"'");
			}	
		}
	}

	private void ifStat() throws Exception {	//TODO  write tests and add error messages 
		System.out.println("ifStat() "+ currentToken.getRecognizedStr());
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
							elsePart();
						} else {
							throw new Exception("[Error] expected '#}' or a statement but found "+currentToken.getRecognizedStr());
						}
					} else if (isStatement()) {
						statement();
						if (currentToken.recognizedStrEquals("else")) {
							elsePart();
						}
					} else {
						throw new Exception("[Error] expected '#{' or a statement but found "+currentToken.getRecognizedStr());
					}
				} else {
					throw new Exception("[Error] expected ':' but found "+currentToken.getRecognizedStr());
				}
			} else {
				throw new Exception("[Error] expected ')' but found "+currentToken.getRecognizedStr());
			}
		}
	}

	private void elsePart() throws Exception { //TODO add error messages + check name of method
		System.out.println("elsePart() "+ currentToken.getRecognizedStr());
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
						throw new Exception("[Error] expected '#}' or a statement but found "+currentToken.getRecognizedStr());
					}
				} else if (isStatement()) {
					statement();
				} else {
					throw new Exception("[Error] expected '#{' or a statement but found "+currentToken.getRecognizedStr());
				}
			} else {
				throw new Exception("[Error] expected ':' but found "+currentToken.getRecognizedStr());
			}
		}
		
	}

	private void whileStat() throws Exception {
		System.out.println("whileStat() "+ currentToken.getRecognizedStr());
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
							throw new Exception("[Error] expected '#}' or a statement but found "+currentToken.getRecognizedStr());
						}
					} else if (isStatement()) {
						statement();
					} else {
						throw new Exception("[Error] expected '#{' or a statement but found "+currentToken.getRecognizedStr());
					}
				} else {
					throw new Exception("[Error] expected ':' but found "+currentToken.getRecognizedStr());
				}
			} else {
				throw new Exception("[Error] expected ')' but found "+currentToken.getRecognizedStr());
			}
		} else {
			throw new Exception("[Error] expected '(' but found "+currentToken.getRecognizedStr());
		}
	}
	
	private void idList() throws Exception {
		System.out.println("idList() "+ currentToken.getRecognizedStr());
		if (isID(currentToken)) {
			loadNextTokenFromLex();
			while (currentToken.recognizedStrEquals(",")) {
				loadNextTokenFromLex();
				if (isID(currentToken)) {
					loadNextTokenFromLex();
				} else {
					throw new Exception("[Error] expected identifier after ',' but found "+currentToken.getRecognizedStr());
				}
			}
		}
	}
	
	private void expression() throws Exception {
		System.out.println("expression() "+ currentToken.getRecognizedStr());
		optionalSign();
		term();
		while (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))) {
			loadNextTokenFromLex();
			term();
		}
	}
	
	private void term() throws Exception {
		System.out.println("term() "+ currentToken.getRecognizedStr());
		factor();
		while (CharTypes.MUL_OPS.contains(currentToken.getRecognizedStr())) {
			loadNextTokenFromLex();
			factor();
		}
	}
	
	private void factor() throws Exception {
		System.out.println("factor() "+ currentToken.getRecognizedStr());
		if (isInteger(currentToken.getRecognizedStr())) {
			loadNextTokenFromLex();
		} else if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			expression();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("[Error:"+currentToken.getLineNum()+"] expected ')' found '"+currentToken.getRecognizedStr()+"'");
			}
		} else if (isID(currentToken)) {
			loadNextTokenFromLex();
			idTail();
		} else {
			throw new Exception("[Error:"+currentToken.getLineNum()+"] at factor !"); 	//TODO make this message more useful
		}
	}
	
	private boolean isInteger(String str) {
		System.out.println("isInteger() "+ currentToken.getRecognizedStr());
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private void idTail() throws Exception {
		System.out.println("idTail() "+ currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("(")) {
			loadNextTokenFromLex();
			actualParList();
			if (currentToken.recognizedStrEquals(")")) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("[Error:" + currentToken.getLineNum() + "] expected ')' found '"
						+ currentToken.getRecognizedStr() + "'");
			}
		}
	}
	
	private void actualParList() throws Exception {	// TODO write tests
		System.out.println("actualParList() "+ currentToken.getRecognizedStr());
		expression();
		while (currentToken.recognizedStrEquals(",")) {
			loadNextTokenFromLex();
			expression();
		}
	}
	
	private void optionalSign() throws Exception {
		System.out.println("optionalSign "+ currentToken.getRecognizedStr());
		if (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr().charAt(0))) {
			loadNextTokenFromLex();
		}
	}
	
	private void condition() throws Exception { 
		System.out.println("condition() "+ currentToken.getRecognizedStr());
		boolTerm();
		while (currentToken.recognizedStrEquals("or")) {
			loadNextTokenFromLex();
			boolFactor();
		}		
	}
	
//	private boolean isBoolFactor() {
//		return  currentToken.recognizedStrEquals("[") ||
//				currentToken.recognizedStrEquals("not") || 
//				isExpression();
//	}
	
	private void boolTerm() throws Exception {
		System.out.println("boolTerm() "+ currentToken.getRecognizedStr());
		boolFactor();	//isos kati edo paizei
		while (currentToken.recognizedStrEquals("and")) {
			loadNextTokenFromLex();
			boolFactor();
		}
	}
	
	private void boolFactor() throws Exception { 	// TODO write tests
		System.out.println("boolFactor() "+ currentToken.getRecognizedStr());
		if (currentToken.recognizedStrEquals("not")) {
			loadNextTokenFromLex();
			if (currentToken.recognizedStrEquals("[")) {
				loadNextTokenFromLex();
				condition();
				if (currentToken.recognizedStrEquals("]")) {
					loadNextTokenFromLex();
				} else {
					throw new Exception("[Error:" + currentToken.getLineNum() + "] expected ']' found '"
							+ currentToken.getRecognizedStr() + "'");
				}
			} else {
				throw new Exception("[Error:" + currentToken.getLineNum() + "] expected '[' found '"+ currentToken.getRecognizedStr() + "'");
			}
			
		} else if (currentToken.recognizedStrEquals("[")) {
			loadNextTokenFromLex();
			condition();
			if (currentToken.recognizedStrEquals("]")) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("[Error:" + currentToken.getLineNum() + "] expected ']' found '"
						+ currentToken.getRecognizedStr() + "'");
			}
		} else if (isExpression()) {
			expression();
			if (CharTypes.REL_OPS.contains((currentToken.getRecognizedStr()))) {
				loadNextTokenFromLex();
			} else {
				throw new Exception("[Error:" + currentToken.getLineNum() + "] expected relation operator but found '"+ currentToken.getRecognizedStr() + "'");
			} 
			expression();
		} else {
			throw new Exception("[Error:" + currentToken.getLineNum() + "] 'not[condition]' or '[condition]' or 'expression relOp expression' but found '"+ currentToken.getRecognizedStr() + "'");
		}
	}
	
	private void callMainPart() throws Exception {
		if (!currentToken.getRecognizedStr().equals(FileReader.EOF.toString())) {
			String args[] = {"if", "__name__", "==", "\"__main__\"", ":"};
			for (int i = 0; i < args.length; i++) {
				if (currentToken.getRecognizedStr().equals(args[i])) {
					loadNextTokenFromLex();
				} else {
					throw new Exception("[Error] expected '"+args[i]+"' but was '"+currentToken.getRecognizedStr());
				}
			}
			mainFunctionCall();
			while (isID(currentToken)) {
				mainFunctionCall();
			}			
		}
	}

	public void mainFunctionCall() throws Exception {
		if (isID(currentToken)) {
			loadNextTokenFromLex();
			String args[] = {"(",")",";"};
			for (int i = 0; i < args.length; i++) {
				if (currentToken.getRecognizedStr().equals(args[i])) {
					loadNextTokenFromLex();
				} else {
					throw new Exception("[Error] expected '"+args[i]+"' but was '"+currentToken.getRecognizedStr());
				}
			}
		} else {
			throw new Exception("[Error] expected identifier but was "+ currentToken.getRecognizedStr());			
		}
	}
	
	public boolean isID(Token token) {
		System.out.println("isID() "+ currentToken.getRecognizedStr());
		return token.getFamily().equals("identifier");
	}

	private void loadNextTokenFromLex() throws Exception {
		prevToken = currentToken;
		currentToken = lex.getToken();
		recognisedCode += currentToken.getRecognizedStr()+"\n";
		System.out.println(recognisedCode);
	}

}
