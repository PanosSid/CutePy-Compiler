package view;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import exceptions.CutePyException;
import finalcode.FinalCodeManager;
import intermediatecode.CTransformer;
import intermediatecode.QuadManager;
import lex.FileReader;
import lex.LexAnalyser;
import symboltable.SymbolTable;
import syntax.SyntaxAnalyser;

public class CutePyCompiler {
	private SyntaxAnalyser syntaxAnalyzer;
	private LexAnalyser lex ;
	private FileReader reader;
	private QuadManager quadManager; 
	private CTransformer cTransformer;
	private SymbolTable symbolTable;
	private FinalCodeManager finManager;
	
	public CutePyCompiler() {
		reader = new FileReader();
		lex = new LexAnalyser(reader);
		quadManager = new QuadManager(1);
		symbolTable = new SymbolTable();
		finManager = new FinalCodeManager(symbolTable);
		syntaxAnalyzer = new SyntaxAnalyser(lex, quadManager, symbolTable, finManager);
		cTransformer = new CTransformer(); 
	}
	
	public void compile(String filePath, boolean cflag) {
		try {
			checkForCpyExtension(filePath);
			reader.initFileContents(filePath);	
			syntaxAnalyzer.analyzeSyntax();
			writeToFile(filePath, "int", quadManager.getIntermediateCode(), "//");
			if (cflag) {
				quadManager.setTempPrefix("T_");
				transformToC(filePath);			
			}
			writeToFile(filePath, "symb", symbolTable.getSymbolTableHistory(), "//");
			writeToFile(filePath, "asm", finManager.getFinalCode(), "#");
			System.out.println("Compilation of '"+filePath+"' successfully completed");
		} catch (CutePyException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Compilation of '"+filePath+"'FAILED");
			e.printStackTrace();
		}
	}
	
	public void writeToFile(String filePath, String extension, String data, String commentType) throws IOException {
		String fileName = changeFilePathExtenstion(filePath, extension);
		FileWriter writer = new FileWriter(fileName);
		writer.write(commentType+"SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n\n");
		writer.write(data);
		writer.close();
	}
	
	public void transformToC(String filePath) throws IOException {
		cTransformer.transformIntermidateCodeToC(quadManager.getIntermedCodeMap());
		writeToFile(filePath, "c", cTransformer.getCcode(), "//");
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
		List<String> argsList = new ArrayList<String>(Arrays.asList(args));
		boolean cFlag = argsList.remove("-c");
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		cpyCompiler.compile(argsList.get(0), cFlag);	
	}

}
