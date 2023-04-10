package symboltable.entities;

import java.util.ArrayList;
import java.util.List;

public class LocalFunction extends Function {
	private List<FormalParameter> formalParams;
	
	public LocalFunction(String name) {
		super(name);
		formalParams = new ArrayList<FormalParameter>();
	}

	public LocalFunction(String name, int startingQuad, int framelength, List<FormalParameter> formalParams) {
		super(name, startingQuad, framelength);
		this.formalParams = formalParams;
	}
	
	public LocalFunction(String name, List<FormalParameter> formalParams) {
		super(name);
		this.formalParams = formalParams;
	}

	public void addFormalParameter(FormalParameter formalParam) {
		formalParams.add(formalParam);
	}

}
