package view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import exceptions.CutePyException;
import intermediatecode.CTransformer;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import syntax.SyntaxAnalyser;

public class CutePyCompiler {
	private SyntaxAnalyser syntaxAnalyzer;
	private LexAnalyser lex ;
	private FileReader reader;
	private QuadManager quadManager; 
	private CTransformer cTransformer;
	
	public CutePyCompiler() {
		reader = new FileReader();
		lex = new LexAnalyser(reader);
		quadManager = new QuadManager();
		syntaxAnalyzer = new SyntaxAnalyser(lex, quadManager);
		cTransformer = new CTransformer(); 
	}
	
	public void compile(String filePath) {
		try {
			checkForCpyExtension(filePath);
			reader.initFileContents(filePath);	
			syntaxAnalyzer.analyzeSyntax();
			writeToFile(filePath, "int", quadManager.getIntermediateCode());
			System.out.println("\n"+quadManager.getIntermediateCode()+"\n");
			cTransformer.transformIntermidateCodeToC(quadManager.getIntermedCodeMap());
			System.out.println(cTransformer.getCcode());
			System.out.println("Compilation of '"+filePath+"' successfully completed");
		} catch (CutePyException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			e.printStackTrace();
		}
	}
	
	public void writeToFile(String filePath, String extension, String data) throws IOException {
		String fileName = changeFilePathExtenstion(filePath, extension);
		FileWriter writer = new FileWriter(fileName);
		writer.write("//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n\n");
		writer.write(data);
		writer.close();
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
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		for (int i = 0; i < args.length; i++) {
			cpyCompiler.compile(args[i]);
		}	
//		cpyCompiler.compile("tests\\intermediate\\validTest.cpy");	
	}

}
