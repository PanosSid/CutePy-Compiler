package symboltable.entities;

import java.util.ArrayList;
import java.util.List;

public class LocalFunction extends Function {
	private List<FormalParameter> formalParams;
	
	public LocalFunction(String name) {
		super(name);
		formalParams = new ArrayList<FormalParameter>();
		
	}
	
	public void addFormalParameter(FormalParameter formalParam) {
		formalParams.add(formalParam);
	}

}
