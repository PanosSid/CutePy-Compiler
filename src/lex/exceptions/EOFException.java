package lex.exceptions;

public class EOFException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EOFException() {
		super("Reached end of file while reading");
	}
	
}
