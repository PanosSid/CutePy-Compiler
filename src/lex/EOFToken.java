package lex;

public class EOFToken extends Token {

	public EOFToken(int lineNum) {
		super(FileReader.EOF.toString(), "End Of File", lineNum);

	}

}
