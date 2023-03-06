package view;

import java.io.IOException;
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
		try {
			checkForCpyExtension(filePath);
			reader.initFileContents(filePath);	
			syntaxAnalyzer.analyzeSyntax();
			System.out.println("Compilation of '"+filePath+"' successfully completed");
		} catch (CutePyException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			e.printStackTrace();
		}
	}
	
	private void checkForCpyExtension(String filePath) throws CutePyException {
		String fileName = filePath.substring(filePath.lastIndexOf("\\")+1);
		String extention = fileName.substring(fileName.lastIndexOf(".")+1);
		if (!extention.equals("cpy")) {
			throw new CutePyException("File extention must be 'cpy' but was '"+extention+"', for file "+fileName);
		}
	}
	
	public static void main(String[] args) {	
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		for (int i = 0; i < args.length; i++) {
			cpyCompiler.compile(args[i]);
		}		
	}

}
