package lex;

import java.util.Objects;

public class Token {
	private String recognizedStr;
	private String family;
	private int lineNum;
	
	public Token(String recognizedStr, String family, int lineNum) {
		super();
		this.recognizedStr = recognizedStr;
		this.family = family;
		this.lineNum = lineNum;
	}

	public String getRecognizedStr() {
		return recognizedStr;
	}
	
	public String getFamily() {
		return family;
	}
	
	public int getLineNum() {
		return lineNum;
	}
	
	public boolean recognizedStrEquals(String otherRecognStr) {
		return recognizedStr.equals(otherRecognStr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(family, lineNum, recognizedStr);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Token other = (Token) obj;
		return Objects.equals(family, other.family) && lineNum == other.lineNum
				&& Objects.equals(recognizedStr, other.recognizedStr);
	}

	@Override
	public String toString() {
		return "Token [recognizedStr=" + recognizedStr + ", family=" + family + ", lineNum=" + lineNum + "]";
	}

	
	
	
}
