package view;

import lex.FileReader;
import lex.LexAnalyser;
import syntax.SyntaxAnalyser;

public class Launcher {
	private static String TEST_FILES_PATH = "D:\\Panos\\CSE UOI\\10o εξάμηνο\\Μεταφραστές\\project-Compilers\\CutePy-Compiler\\tests\\acceptance\\"; 

	public static void main(String[] args) {
		FileReader reader = new FileReader(TEST_FILES_PATH+"allFuncsModified.cpy");
		LexAnalyser lex = new LexAnalyser(reader);
		SyntaxAnalyser syntax = new SyntaxAnalyser(lex);
		try {
			syntax.analyzeSyntax();
			System.out.println("Compilation successfully completed");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Compilation FAILED");
			System.out.println(e.getMessage());
		}

	}

}
