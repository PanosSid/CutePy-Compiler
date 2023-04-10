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
				+ "	main_example1();"
				);
		List<Scope> expectedScopes = new ArrayList<Scope>();
		expectedScopes.add(
				new Scope(
						new Parameter("a", 12, ParameterMode.CV),
						new Variable("c", 16),
						new TemporaryVariable("T_1", 20)
						)
				);
		syntax.analyzeSyntax();
		List<Scope> actualScopes = symbolTable.getCompletedScopes();
		assertEqualsListOfScopes(expectedScopes, actualScopes);
	}
	
	private void assertEqualsListOfScopes(List<Scope> expectedScopes, List<Scope> actualScopes) {
		for (int i = 0; i < expectedScopes.size(); i++) {
			Assertions.assertEquals(expectedScopes.get(i), actualScopes.get(i));
		}
	}
}
