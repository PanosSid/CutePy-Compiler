package view;

import exceptions.CutePyException;
import lex.FileReader;
import lex.LexAnalyser;
import syntax.SyntaxAnalyser;

public class CutePyCompiler {
	private SyntaxAnalyser syntaxAnalyzer;
	private LexAnalyser lex ;
	private FileReader reader;
	
	public CutePyCompiler() {
		reader = new FileReader();
		lex = new LexAnalyser(reader);
		syntaxAnalyzer = new SyntaxAnalyser(lex);
	}
	
	public void compile(String filePath) {
		reader.initFileContents2(filePath);	
		try {
			syntaxAnalyzer.analyzeSyntax();
			System.out.println("Compilation successfully completed");
		} catch (CutePyException e) {
			System.out.println("Compilation FAILED");
			System.out.println(e.getMessage());
		}

	}
	
	public static void main(String[] args) {
		String TEST_FILES_PATH = "D:\\Panos\\CSE UOI\\10o εξάμηνο\\Μεταφραστές\\project-Compilers\\CutePy-Compiler\\tests\\acceptance\\";
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		cpyCompiler.compile(TEST_FILES_PATH+"allFuncs.cpy");
//		cpyCompiler.compile(TEST_FILES_PATH+"sourceWithErrors.cpy");
//		cpyCompiler.compile(args[1]);
		
		
	}

}
