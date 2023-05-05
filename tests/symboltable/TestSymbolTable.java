package symboltable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import exceptions.CutePyException;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import symboltable.entities.Entity;
import symboltable.entities.FormalParameter;
import symboltable.entities.LocalFunction;
import symboltable.entities.MainFunction;
import symboltable.entities.Parameter;
import symboltable.entities.ParameterMode;
import symboltable.entities.TemporaryVariable;
import symboltable.entities.Variable;
import syntax.FakeFinalCodeManager;
import syntax.SyntaxAnalyser;

public class TestSymbolTable {
	private SyntaxAnalyser syntax;
	private FakeSymbolTable symbolTable;
	
	private void setUpSyntaxAnalyser(String sourceCode) {
		FileReader reader = new FileReader();
		reader.setFileContents(sourceCode);
		LexAnalyser lex = new LexAnalyser(reader);
		symbolTable = new FakeSymbolTable();
		syntax = new SyntaxAnalyser(lex,  new QuadManager(), symbolTable, new FakeFinalCodeManager());
	}
	
	
	@Test 
	public void testSearchEnitty() throws CutePyException {
		Stack<Scope> scopeStack = new Stack<Scope>();
		scopeStack.add(new Scope(
				new Variable("a", 12),
				new Variable("b", 16),
				new MainFunction("main_func", 101, 24)
				));
		
		ArrayList<FormalParameter> fParams = new ArrayList<FormalParameter>();
		fParams.add(new FormalParameter("x1"));
		scopeStack.add(new Scope(
					new Variable("c", 12),
					new Variable("d", 16),
					new LocalFunction("f", 101, 24, fParams)
					));
		
		scopeStack.add(new Scope(
				new Parameter("b", 12),
				new Parameter("y", 16),
				new Variable("d", 20)
				));
		

		SymbolTable symbolTable = new SymbolTable(scopeStack);
		
		Assertions.assertEquals(new Variable("a", 12), symbolTable.searchEntity("a"));
		Assertions.assertEquals(new Parameter("b", 12), symbolTable.searchEntity("b"));
		Assertions.assertEquals(new LocalFunction("f", 101, 24, fParams), symbolTable.searchEntity("f"));
		Assertions.assertThrows(CutePyException.class, () -> { symbolTable.searchEntity("k12"); });
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
				+ "		#$ body of P1 #$\r\n"
				+ "		y = x;\r\n"
				+ "		return(y);\r\n"
				+ "    #}\r\n"
				+ "\r\n"
				+ "    def P2(y):\r\n"
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
						new Parameter("x", 12, ParameterMode.CV),
						new Variable("a", 16),
						new TemporaryVariable("T_3", 20)
						));
		ArrayList<FormalParameter> p1Params = new ArrayList<FormalParameter>();
		p1Params.add(new FormalParameter("x"));
		p1Params.add(new FormalParameter("y"));
		
		ArrayList<FormalParameter> p2Params = new ArrayList<FormalParameter>();
		p2Params.add(new FormalParameter("x"));
		expectedScopes.add(
				new Scope(
						new Variable("a", 12),
						new Variable("b", 16),
						new Variable("c", 20),
						new LocalFunction("P1", 117, 24, p1Params),
						new LocalFunction("P2", 121, 24, p2Params),
						new TemporaryVariable("T_4", 24),
						new TemporaryVariable("T_5", 28)
						));
		
		expectedScopes.add(
				new Scope(
						(Entity) new MainFunction("main_symbol", 129, 32)
						));
		syntax.analyzeSyntax();
		List<Scope> actualScopes = symbolTable.getCompletedScopes();
		assertEqualsListOfScopes(expectedScopes, actualScopes);
	}
	
	
	@Test
	public void testSymbolTableParadeigma3() throws CutePyException {
		setUpSyntaxAnalyser(""
				+ "def main_paradeigma3():\r\n"
				+ "#{ \r\n"
				+ "    #declare a,b,c\r\n"
				+ "\r\n"
				+ "    def A1(x):\r\n"
				+ "    #{ \r\n"
				+ "        #declare a,e\r\n"
				+ "\r\n"
				+ "        def B1(x,y):\r\n"
				+ "        #{\r\n"
				+ "            #declare a\r\n"
				+ "			def C1(y):\r\n"
				+ "			#{\r\n"
				+ "				#declare a\r\n"
				+ "				a = a + 1;	#$ 1 temp var #$\r\n"
				+ "				return(a);\r\n"
				+ "			#}\r\n"
				+ "			\r\n"
				+ "			def C2(x,y):\r\n"
				+ "			#{\r\n"
				+ "				#declare a\r\n"
				+ "				a = a + 1;	#$ 1 temp var #$\r\n"
				+ "				a = a * 2;	#$ 2 temp var #$\r\n"
				+ "				return(a);\r\n"
				+ "			#}\r\n"
				+ "			\r\n"
				+ "            a = a + 9;\r\n"
				+ "			return(a);\r\n"
				+ "        #}\r\n"
				+ "\r\n"
				+ "        def B2(x):\r\n"
				+ "        #{ \r\n"
				+ "            #declare a,e\r\n"
				+ "            a = a + 1;	#$ 1 temp var #$\r\n"
				+ "			e = a // 2;	#$ 2 temp var #$\r\n"
				+ "			return(e);\r\n"
				+ "        #}\r\n"
				+ "\r\n"
				+ "		return(3);	#$ 0 temp vars in A1#$\r\n"
				+ "    #}\r\n"
				+ "\r\n"
				+ "    def A2(x, y):\r\n"
				+ "    #{ \r\n"
				+ "        #declare a\r\n"
				+ "        a = a - 1;	#$ 1 temp var #$\r\n"
				+ "        return(a); \r\n"
				+ "    #}\r\n"
				+ "\r\n"
				+ "    #$ ma program #$\r\n"
				+ "    b = b + 2;\r\n"
				+ "	c = c + 3;	\r\n"
				+ "\r\n"
				+ "#}\r\n"
				+ "\r\n"
				+ "if __name__ == \"__main__\":\r\n"
				+ "	main_paradeigma3();"
				);
		List<Scope> expectedScopes = new ArrayList<Scope>();
		expectedScopes.add(
				new Scope(
						new Parameter("y", 12, ParameterMode.CV),
						new Variable("a", 16),
						new TemporaryVariable("T_1", 20)
						));
		
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new Parameter("y", 16, ParameterMode.CV),
						new Variable("a", 20),
						new TemporaryVariable("T_1", 24),
						new TemporaryVariable("T_2", 28)
						));
		
		ArrayList<FormalParameter> c1Params = new ArrayList<FormalParameter>();
		c1Params.add(new FormalParameter("y"));
		ArrayList<FormalParameter> c2Params = new ArrayList<FormalParameter>();
		c2Params.add(new FormalParameter("x"));
		c2Params.add(new FormalParameter("y"));
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new Parameter("y", 16, ParameterMode.CV),
						new Variable("a", 20),
						new LocalFunction("C1", 101, 24, c1Params),
						new LocalFunction("F12", 106, 32, c2Params),
						new TemporaryVariable("T_4", 24)
						));
		
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new Variable("a", 16),
						new Variable("e", 20),
						new TemporaryVariable("T_5", 24),
						new TemporaryVariable("T_6", 28)
						));
		ArrayList<FormalParameter> b1Params = new ArrayList<FormalParameter>();
		b1Params.add(new FormalParameter("x"));
		b1Params.add(new FormalParameter("y"));
		
		ArrayList<FormalParameter> b2Params = new ArrayList<FormalParameter>();
		b2Params.add(new FormalParameter("x"));
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new Variable("a", 16),
						new Variable("e", 20),
						new LocalFunction("B1", 113, 28, b1Params),
						new LocalFunction("B2", 118, 32, b2Params)
						));
		
		expectedScopes.add(
				new Scope(
						new Parameter("x", 12, ParameterMode.CV),
						new Parameter("y", 16, ParameterMode.CV),
						new Variable("a", 20),
						new TemporaryVariable("T_7", 24)
						));
		
		ArrayList<FormalParameter> a1Params = new ArrayList<FormalParameter>();
		a1Params.add(new FormalParameter("x"));
		
		ArrayList<FormalParameter> a2Params = new ArrayList<FormalParameter>();
		a2Params.add(new FormalParameter("x"));
		a2Params.add(new FormalParameter("y"));
		expectedScopes.add(
				new Scope(
						new Variable("a", 12),
						new Variable("b", 16),
						new Variable("c", 20),
						new LocalFunction("A1", 125, 24, a1Params),
						new LocalFunction("A2", 128, 28, a2Params),
						new TemporaryVariable("T_8", 24),
						new TemporaryVariable("T_9", 28)
						));
		
		expectedScopes.add(
				new Scope(
						(Entity) new MainFunction("main_paradeigma3", 133, 32)
						));
		syntax.analyzeSyntax();
		List<Scope> actualScopes = symbolTable.getCompletedScopes();
		assertEqualsListOfScopes(expectedScopes, actualScopes);
	}
	
}
