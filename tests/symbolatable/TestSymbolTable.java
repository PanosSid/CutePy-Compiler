package symbolatable;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import symboltable.Scope;
import symboltable.entities.Entity;
import symboltable.entities.FormalParameter;
import symboltable.entities.LocalFunction;
import symboltable.entities.MainFunction;
import symboltable.entities.Parameter;
import symboltable.entities.ParameterMode;
import symboltable.entities.TemporaryVariable;
import symboltable.entities.Variable;
import syntax.SyntaxAnalyser;

public class TestSymbolTable {
	private SyntaxAnalyser syntax;
	private FakeSymbolTable symbolTable;
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		symbolTable = new FakeSymbolTable();
		syntax = new SyntaxAnalyser(lex,  new QuadManager(), symbolTable);
	}
	
	@Test
	public void testSymbolTableExample1() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_ps():\n"
				+ "#{ \n"
				+ "	#declare a,b \n"
				+ " 	def f(a):\n"
				+ " 	#{ \n"
				+ " 		#declare c \n"
				+ " 		c = a + 1; \n"
				+ " 		return(c); \n"
				+ " 	#} \n"
				+ " 	a = 1; \n"
				+ " 	b = f(a);\n"
				+ "#}\n"
				+ "\n"
				+ "if __name__ == \"__main__\":\n"
				+ "	main_ps();"
				);
		List<Scope> expectedScopes = new ArrayList<Scope>();
		expectedScopes.add(
				new Scope(
						new Parameter("a", 12, ParameterMode.CV),
						new Variable("c", 16),
						new TemporaryVariable("T_1", 20)
						));
		ArrayList<FormalParameter> fParams = new ArrayList<FormalParameter>();
		fParams.add(new FormalParameter("a"));
		expectedScopes.add(
				new Scope(
						new Variable("a", 12),
						new Variable("b", 16),
						new LocalFunction("f", 101, 24, fParams),
						new TemporaryVariable("T_2", 20)
						));
		expectedScopes.add(
				new Scope(
						(Entity) new MainFunction("main_ps", 106, 24)
						));
		syntax.analyzeSyntax();
		List<Scope> actualScopes = symbolTable.getCompletedScopes();
		assertEqualsListOfScopes(expectedScopes, actualScopes);
	}
	
	private void assertEqualsListOfScopes(List<Scope> expectedScopes, List<Scope> actualScopes) {
		Assertions.assertEquals(expectedScopes.size(), actualScopes.size());
		for (int i = 0; i < expectedScopes.size(); i++) {
			Assertions.assertEquals(expectedScopes.get(i), actualScopes.get(i));
		}
	}
	
	
	@Test
	public void testSymbolTableSymbol() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_symbol():\r\n"
				+ "#{ \r\n"
				+ "    #declare a,b,c\r\n"
				+ "\r\n"
				+ "    def P1(x,y):\r\n"
				+ "    #{ \r\n"
				+ "        #declare a\r\n"
				+ "\r\n"
				+ "        def F11(x):\r\n"
				+ "        #{\r\n"
				+ "            #$ body of F11 #$\r\n"
				+ "            #declare a\r\n"
				+ "            b = a;\r\n"
				+ "            a = x;\r\n"
				+ "            c = F11(x);\r\n"
				+ "            return (c);\r\n"
				+ "        #}\r\n"
				+ "\r\n"
				+ "        def F12(x):\r\n"
				+ "        #{ \r\n"
				+ "            #$ body of F12 #$\r\n"
				+ "            c = F11(x);\r\n"
				+ "            return (c);\r\n"
				+ "        #}\r\n"
				+ "\r\n"
				+ "    #$ body of P1 #$\r\n"
				+ "    y = x;\r\n"
				+ "    #}\r\n"
				+ "\r\n"
				+ "    def P2(x):\r\n"
				+ "    #{ \r\n"
				+ "        #$ body of P2 #$\r\n"
				+ "        #declare x\r\n"
				+ "        y = 1;\r\n"
				+ "        return(P1(x,y)); \r\n"
				+ "    #}\r\n"
				+ "\r\n"
				+ "    #$ ma program #$\r\n"
				+ "    print(P1(a,b)); \r\n"
				+ "    print(P2(c)); \r\n"
				+ "\r\n"
				+ "#}\r\n"
				+ "\r\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "	main_symbol();"
				);
		List<Scope> expectedScopes = new ArrayList<Scope>();
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new Variable("a", 16),
						new TemporaryVariable("T_1", 20)
						));
		
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new TemporaryVariable("T_2", 16)
						));
		
		ArrayList<FormalParameter> f11Params = new ArrayList<FormalParameter>();
		f11Params.add(new FormalParameter("x"));
		ArrayList<FormalParameter> f12Params = new ArrayList<FormalParameter>();
		f12Params.add(new FormalParameter("x"));
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new Parameter("y", 16, ParameterMode.CV),
						new Variable("a", 20),
						new LocalFunction("F11", 101, 24, f11Params),
						new LocalFunction("F12", 110, 20, f12Params)
						));
		
		expectedScopes.add(
				new Scope(
						new Parameter("y", 12, ParameterMode.CV),
						new Variable("x", 16)
						));
		ArrayList<FormalParameter> p1Params = new ArrayList<FormalParameter>();
		p1Params.add(new FormalParameter("a"));
		p1Params.add(new FormalParameter("b"));
		
		ArrayList<FormalParameter> p2Params = new ArrayList<FormalParameter>();
		p2Params.add(new FormalParameter("c"));
		expectedScopes.add(
				new Scope(
						new Variable("a", 12),
						new Variable("b", 16),
						new Variable("c", 20),
						new LocalFunction("P1", 117, 24, p1Params),
						new LocalFunction("P2", 120, 24, p2Params)
						));
		
		expectedScopes.add(
				new Scope(
						(Entity) new MainFunction("main_symbol", 106, 24)
						));
		syntax.analyzeSyntax();
		List<Scope> actualScopes = symbolTable.getCompletedScopes();
		assertEqualsListOfScopes(expectedScopes, actualScopes);
	}
}
