package intermediate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import intermediatecode.Quad;
import intermediatecode.QuadManager;

public class TestQuadManager {
	private QuadManager quadManager = new QuadManager();
	
	@Test
	public void testNewTemp() {
		Assertions.assertEquals("T_1", quadManager.newTemp());
		Assertions.assertEquals("T_2", quadManager.newTemp());
		quadManager.newTemp();
		quadManager.newTemp();
		Assertions.assertEquals("T_5", quadManager.newTemp());	
	}
	
	@Test
	public void testGenQuad() {
		quadManager.genQuad("+", "a", "b", "T_1");
		quadManager.genQuad("-", "c", "T_1", "T_2");
		quadManager.genQuad("*", "d", "T_2", "T_3");
		String expectedIntermedCode = ""
				+ "100: +, a, b, T_1\n"
				+ "101: -, c, T_1, T_2\n"
				+ "102: *, d, T_2, T_3\n";
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testMakeList() {
		List<Integer> actualList = quadManager.makeList(200);
		List<Integer> expectedList = Arrays.asList(200);
		Assertions.assertEquals(expectedList, actualList);
	}
	
	@Test
	public void testMergeList() {
		List<Integer> list1 = Arrays.asList(100, 200, 300);
		List<Integer> list2 = Arrays.asList(400, 500);
		List<Integer> actualList = quadManager.mergeList(list1, list2);
		List<Integer> expectedList = Arrays.asList(100, 200, 300, 400, 500);
		Assertions.assertEquals(expectedList, actualList);
	}
	
	@Test
	public void testBackPatch() {
		Map<Integer, Quad> map = new HashMap<Integer, Quad>();
		map.put(100, new Quad("+", "a", "b", "T_1"));
		map.put(101, new Quad("-", "c", "T_1", "_"));
		map.put(102, new Quad("*", "d", "T_2", "T_3"));
		map.put(150, new Quad("//", "e", "T_2", "_"));
		quadManager.setIntermedCodeMap(map);
		
		List<Integer> listChange = Arrays.asList(101, 150);
		quadManager.backpatch(listChange, 200);
		String expectedIntermedCode = ""
				+ "100: +, a, b, T_1\n"
				+ "101: -, c, T_1, 200\n"
				+ "102: *, d, T_2, T_3\n"
				+ "150: //, e, T_2, 200\n";
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
	}
	
	@Test
	public void testAllQuadManager() {
		List<Integer> x1 = quadManager.makeList(quadManager.nextQuad());
		quadManager.genQuad("jump", "_", "_", "_");
		quadManager.genQuad("+", "a", "1", "a");
		List<Integer> x2 = quadManager.makeList(quadManager.nextQuad());
		quadManager.genQuad("jump", "_", "_", "_");
		List<Integer> x = quadManager.mergeList(x1, x2);
		quadManager.genQuad("+", "a", "2", "a");
		quadManager.backpatch(x, quadManager.nextQuad());
		
		String expectedIntermedCode = ""
				+ "100: jump, _, _, 104\n"
				+ "101: +, a, 1, a\n"
				+ "102: jump, _, _, 104\n"
				+ "103: +, a, 2, a\n";
		Assertions.assertEquals(expectedIntermedCode, quadManager.getIntermediateCode());
		
	}
	
	
	
	
	
}
