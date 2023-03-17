package intermediate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Ignore;
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
		
		String expectedTmpDecl = "int c, d, T_1, T_2, b, T_3, a, T_4;"; 
		String actualTmpDecl = transformer.getDeclerationOfVars(intermedCode);
		Assertions.assertEquals(expectedTmpDecl, actualTmpDecl);
		
	}
	
	@Test 
	public void testTransformIntermidateCodeToC() {
		Map<Integer, Quad> intermedCode = new HashMap<Integer, Quad>();
		intermedCode.put(100, new Quad("begin_block, main_arithmetic, _, _\n"));
		intermedCode.put(101, new Quad("par, x, cv, _\n"));
		intermedCode.put(102, new Quad("par, y, cv, _\n"));
		intermedCode.put(103, new Quad("par, T_1, ret, _\n"));
		intermedCode.put(104, new Quad("call, func, _, _\n"));
		intermedCode.put(105, new Quad("+, 1, T_1, T_2"));
		intermedCode.put(106, new Quad(":=, T_2, _, x\n"));
		intermedCode.put(107, new Quad("end_block, main_arithmetic, _, _\n"));
		intermedCode.put(108, new Quad("call, main_arithmetic, _, _\n"));
		
		transformer.transformIntermidateCodeToC(intermedCode);

		String expectedCcode = ""
				+ "//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n"
				+ "\n"
				+ "#include <stdio.h>\n"
				+ "\n"
				+ "int main(){\n"
				+ "\tint x, y, T_1, T_2;\n"
				+ "	L_100: \n"
				+ "	L_101: \n"
				+ "	L_102: \n"
				+ "	L_103: \n"
				+ "	L_104: T_1 = func(x, y);\n"
				+ "	L_105: T_2 = 1 + T_1;\n"
				+ "	L_106: x = T_2;\n"
				+ "	L_107: \n"
				+ "	L_108: main_arithmetic();\n"
				+ "}\n";
		String actualCcode = transformer.getCcode();
		Assertions.assertEquals(expectedCcode, actualCcode);	
		
	}
	
	@Ignore
//	@Test 
	public void testTransformIntermidateCodeToC2() {
		Map<Integer, Quad> intermedCode = new HashMap<Integer, Quad>();
		intermedCode.put(100, new Quad("begin_block, main_arithmetic, _, _\n"));
		intermedCode.put(101, new Quad("par, x, cv, _\n"));
		intermedCode.put(102, new Quad("par, y, cv, _\n"));
		intermedCode.put(103, new Quad("par, T_1, ret, _\n"));
		intermedCode.put(104, new Quad("call, func, _, _\n"));
		intermedCode.put(105, new Quad("end_block, main_arithmetic, _, _\n"));
		intermedCode.put(106, new Quad("call, main_arithmetic, _, _\n"));
		
		transformer.transformIntermidateCodeToC(intermedCode);

		String expectedCcode = ""
				+ "//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n"
				+ "\n"
				+ "#include <stdio.h>\n"
				+ "\n"
				+ "int main(){\n"
				+ "\tint x, y;\n"
				+ "	L_100: \n"
				+ "	L_101: \n"
				+ "	L_102: \n"
				+ "	L_103: \n"
				+ "	L_104: func(x, y);\n"
				+ "	L_105: \n"
				+ "	L_106: main_arithmetic();\n"
				+ "}\n";
		String actualCcode = transformer.getCcode();
		Assertions.assertEquals(expectedCcode, actualCcode);	
		
	}

	@Test 
	public void testTransformIntermidateCodeToC3() {
		Map<Integer, Quad> intermedCode = strToMapOfQuads(""
				+ "100: begin_block, main_exams, _, _\n"
				+ "101: :=, 1, _, a\n"
				+ "102: +, a, b, T_1\n"
				+ "103: <, T_1, 1, 105\n"
				+ "104: jump, _, _, 127\n"
				+ "105: <, b, 5, 107\n"
				+ "106: jump, _, _, 127\n"
				+ "107: ==, t, 1, 109\n"
				+ "108: jump, _, _, 111\n"
				+ "109: :=, 2, _, c\n"
				+ "110: jump, _, _, 116\n"
				+ "111: ==, t, 2, 113\n"
				+ "112: jump, _, _, 115\n"
				+ "113: :=, 4, _, c\n"
				+ "114: jump, _, _, 116\n"
				+ "115: :=, 0, _, c\n"
				+ "116: <, a, 1, 118\n"
				+ "117: jump, _, _, 126\n"
				+ "118: ==, a, 2, 120\n"
				+ "119: jump, _, _, 125\n"
				+ "120: ==, b, 1, 122\n"
				+ "121: jump, _, _, 124\n"
				+ "122: :=, 2, _, c\n"
				+ "123: jump, _, _, 120\n"
				+ "124: jump, _, _, 125\n"
				+ "125: jump, _, _, 116\n"
				+ "126: jump, _, _, 102\n"
				+ "127: end_block, main_exams, _, _\n"
				+ "128: call, main_exams, _, _\n"
				);
		transformer.transformIntermidateCodeToC(intermedCode);
		
		String expectedCcode = ""
				+ "//SIDIROPOULOS PANAGIOTIS AM:4489 cse84489\n"
				+ "\n"
				+ "#include <stdio.h>\n"
				+ "\n"
				+ "int main(){\n"
				+ "	int a, b, T_1, t, c;\n"
				+ "	L_100: \n"
				+ "	L_101: a = 1;\n"
				+ "	L_102: T_1 = a + b;\n"
				+ "	L_103: if (T_1 < 1) goto L_105;\n"
				+ "	L_104: goto L_127;\n"
				+ "	L_105: if (b < 5) goto L_107;\n"
				+ "	L_106: goto L_127;\n"
				+ "	L_107: if (t == 1) goto L_109;\n"
				+ "	L_108: goto L_111;\n"
				+ "	L_109: c = 2;\n"
				+ "	L_110: goto L_116;\n"
				+ "	L_111: if (t == 2) goto L_113;\n"
				+ "	L_112: goto L_115;\n"
				+ "	L_113: c = 4;\n"
				+ "	L_114: goto L_116;\n"
				+ "	L_115: c = 0;\n"
				+ "	L_116: if (a < 1) goto L_118;\n"
				+ "	L_117: goto L_126;\n"
				+ "	L_118: if (a == 2) goto L_120;\n"
				+ "	L_119: goto L_125;\n"
				+ "	L_120: if (b == 1) goto L_122;\n"
				+ "	L_121: goto L_124;\n"
				+ "	L_122: c = 2;\n"
				+ "	L_123: goto L_120;\n"
				+ "	L_124: goto L_125;\n"
				+ "	L_125: goto L_116;\n"
				+ "	L_126: goto L_102;\n"
				+ "	L_127: \n"
				+ "	L_128: main_exams();\n"
				+ "}\n"
				+ "";
		String actualCcode = transformer.getCcode();
		Assertions.assertEquals(expectedCcode, actualCcode);	
		
	}
	
	private Map<Integer, Quad> strToMapOfQuads(String s) {
		Map<Integer, Quad> intermedCode = new LinkedHashMap<Integer, Quad>();
		String lines[] = s.split("\\r?\\n");
		for (int i = 0; i < lines.length; i++) {
			String[] l = lines[i].split(":", 2);
			int lbl = Integer.parseInt(l[0]);
			Quad q = new Quad(l[1].trim());
			intermedCode.put(lbl, q);
		}
		return intermedCode;
	}
}
