package intermediate;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import intermediatecode.CTransformer;
import intermediatecode.Quad;

public class TestCTransformer {
	private CTransformer transformer = new CTransformer();
	
	@Test 
	public void testGetDeclerationOfVars() {
		Map<Integer, Quad> intermedCode = new HashMap<Integer, Quad>();
		intermedCode.put(100, new Quad("begin_block, main_arithmetic, _, _\n"));
		intermedCode.put(101, new Quad("+, c, d, T_1\n"));
		intermedCode.put(102, new Quad("+, T_1, 1, T_2\n"));
		intermedCode.put(103, new Quad("*, b, T_2, T_3\n"));
		intermedCode.put(104, new Quad("+, a, T_3, T_4\n"));
		intermedCode.put(105, new Quad("=, T_4, _, c\n"));
		intermedCode.put(106, new Quad("out, _, _, c\n"));
		intermedCode.put(107, new Quad("end_block, main_arithmetic, _, _\n"));
		intermedCode.put(108, new Quad("call, main_arithmetic, _, _\n"));
		
		String expectedTmpDecl = "int c, d, T_1, T_2, b, T_3, a, T_4"; 
		String actualTmpDecl = transformer.getDeclerationOfVars(intermedCode);
		Assertions.assertEquals(expectedTmpDecl, actualTmpDecl);
		
	}
}
