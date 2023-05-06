package acceptancefinal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import view.CutePyCompiler;

public class TestCompilerAcceptance {	
	private final String CORRECT_PATH = System.getProperty("user.dir")+File.separator+"tests"+File.separator +"acceptancefinal"+File.separator+"correct-results"+File.separator;
	private final static String TO_TEST_PATH = System.getProperty("user.dir")+File.separator+"tests"+File.separator +"acceptancefinal"+File.separator;
	private String generatedfileTypes[] = {".int", ".symb", ".asm"};
	
	@AfterAll
    public static void cleanUp() {
        File dir = new File(TO_TEST_PATH);
        for (File file : dir.listFiles()) {
            if (file.isFile() && !file.getName().endsWith(".cpy")
            		&& !file.getName().endsWith(".java")) {
                file.delete();
            }
        }
    }

	
	@Test
	public void testFibonacci() throws IOException {
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		String fileName = "fibonacci";
		cpyCompiler.compile(TO_TEST_PATH+fileName+".cpy", false);
		assertAllGeneratedFiles(fileName);
	}
	
	@Test
	public void testPrimes() throws IOException {
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		String fileName = "primes";
		cpyCompiler.compile(TO_TEST_PATH+fileName+".cpy", false);
		assertAllGeneratedFiles(fileName);
	}
	
	@Test
	public void testFactorial() throws IOException {
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		String fileName = "factorial";
		cpyCompiler.compile(TO_TEST_PATH+fileName+".cpy", false);
		assertAllGeneratedFiles(fileName);
	}
	
	@Test
	public void testNested() throws IOException {
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		String fileName = "nested";
		cpyCompiler.compile(TO_TEST_PATH+fileName+".cpy", false);
		assertAllGeneratedFiles(fileName);
	}
	
	@Test
	public void testCountDigits() throws IOException {
		CutePyCompiler cpyCompiler = new CutePyCompiler();
		String fileName = "countdigits";
		cpyCompiler.compile(TO_TEST_PATH+fileName+".cpy", false);
		assertAllGeneratedFiles(fileName);
	}

	private void assertAllGeneratedFiles(String fileName) throws IOException {
		for (int i = 0; i < generatedfileTypes.length; i++) {
			byte actualFileContent[] = Files.readAllBytes(Paths.get(TO_TEST_PATH+fileName+generatedfileTypes[i]));
			byte expectedFileContent[] = Files.readAllBytes(Paths.get(CORRECT_PATH+fileName+generatedfileTypes[i]));
			Assertions.assertArrayEquals(expectedFileContent, actualFileContent);
		}
	}
	
	
	
}
