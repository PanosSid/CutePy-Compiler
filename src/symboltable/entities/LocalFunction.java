package symboltable.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(formalParams);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalFunction other = (LocalFunction) obj;
		return Objects.equals(formalParams, other.formalParams);
	}
	
	private String getFormalParmasAsStr() {
		if (formalParams.size() > 0) {
			String s= "";
			for (FormalParameter fp : formalParams) {
				s += fp.getName() +", ";
			}
			return s.substring(0, s.lastIndexOf(","));
		}
		return ""; 
	}

	@Override
	public String toString() {
//		return "LocalFunction [formalParams=" + formalParams + "]";
		return "[LocalFunction: "+super.name+", SQ=" + startingQuad + ", FL=" + framelength + ", FPs =<"+getFormalParmasAsStr()+">]";
	}
	
	
	
	
}
