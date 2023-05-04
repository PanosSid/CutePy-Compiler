package syntax;

import java.util.Map;

import exceptions.CutePyException;
import finalcode.FinalCodeManager;
import intermediatecode.Quad;

/*
 * Dummy FinalCodeManager used to isolate test of different classes
 */
public class FakeFinalCodeManager extends FinalCodeManager {

	@Override
	public String getFinalCode() {
		return "";
	}

	@Override
	public void initMainFinalCode() {

	}

	@Override
	public void genFinalCode(int startingLabel, Map<Integer, Quad> intermedCode) throws CutePyException {

	}

	@Override
	public void gnvlcode(String variableName) throws CutePyException {

	}

	@Override
	public void loadvr(String varName, String reg) throws CutePyException {

	}

	@Override
	public void storerv(String reg, String varName) throws CutePyException {

	}
	
	
}
