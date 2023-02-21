package syntax;

import lex.CharTypes;
import lex.LexAnalyser;
import lex.Token;

public class SyntaxAnalyser {
	private LexAnalyser lex;
	private Token currentToken;
	private Token prevToken;
	
	public SyntaxAnalyser(LexAnalyser lex) {
		this.lex = lex;

	}
		
	public Token getCurrentToken() {
		return currentToken;
	}
	
	public void setCurrentToken(Token currenToken) {
		this.currentToken = currenToken;
	}

	public Token getPrevToken() {
		return prevToken;
	}
	
	public void analyzeSyntax() throws Exception {
		loadNextTokenFromLex();
		startRule();
		System.out.println("Compilation successfully completed");
	}
	
	
	private void startRule() {
		defMainPart();
		callMainPart();
	}
	
	private void defMainPart() {
		// loop
			defMainFunction();
		
	}
	
	private void defMainFunction() {
		// TODO Auto-generated method stub
		
	}
	
	private void defFunction() {
		
	}
	
	private void declarations() {
		
	}
	
	private void declerationLine() {
		
	}
	
	private void statement() {
		
	}
	
	private void statements() {
		
	}
	
	private void simpleStatement() {
		
	}
	
	private void structuredStatement() {
		
	}
	
	private void assignmentStat() {
		
	}
	
	private void printStat() {
		
	}
	
	private void returnStat() {
		
	}
	
	private void ifStat() {
		
	}
	
	private void whileStat() {
		
	}
	
	private void idList() {
		
	}
	
	private void expression() {
		
	}
	
	private void term() {
		
	}
	
	private void factor() {
		
	}
	
	private void idTail() {
		
	}
	
	private void actualParList() {
		
	}
	
	public void optionalSign() throws Exception {
		if (CharTypes.ADD_OPS.contains(currentToken.getRecognizedStr())) {
			loadNextTokenFromLex();
		}
	}
	
	private void condition() {
		
	}
	
	private void boolTerm() {
		
	}
	
	private void boolFactor() {
		
	}
	
	private void callMainPart() {
		
	}

	public void mainFunctionCall() throws Exception {
		loadNextTokenFromLex();
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
		return token.getFamily().equals("identifier");
	}

	private void loadNextTokenFromLex() throws Exception {
		prevToken = currentToken;
		currentToken = lex.getToken();
	}
	
}
