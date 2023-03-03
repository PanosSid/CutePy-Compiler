package lex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CharTypes {
	
	public static final List<Character> ADD_OPS = Arrays.asList(new Character[] { '+', '-' });
	public static final List<Character> DELIMITERS = Arrays.asList(new Character[] { ',', ';', ':' });
	public static final List<String> MUL_OPS = Arrays.asList(new String[] { "*", "//" });
	public static final List<Character> GROUP_SUMBOLS = Arrays.asList(new Character[] { '{', '}', '(', ')', '[', ']'});
	public static final List<Character> ASGN = Arrays.asList(new Character[] { '=' });
	public static final List<Character> SMALLER = Arrays.asList(new Character[] { '<' });
	public static final List<Character> LARGER = Arrays.asList(new Character[] { '>' });	
	public static final List<Character> LETTERS = Arrays
			.asList(new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
					'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
					'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'});
	public static final List<Character> DIGITS = Arrays.asList(new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});
	
	public static final List<Character> UNDERCORE = Arrays.asList(new Character[] { '_' });
	
	public static final List<String> REL_OPS = Arrays.asList(new String[] { "<", "<=", ">", ">=", "!=", "=="});
	
	public static boolean isNotInAlphabet(Character c) {
		Set<Character> CUTEPY_ALPHABET = new HashSet<Character>();
		CUTEPY_ALPHABET.addAll(ADD_OPS);
		CUTEPY_ALPHABET.addAll(DELIMITERS);
		CUTEPY_ALPHABET.addAll(GROUP_SUMBOLS);
		CUTEPY_ALPHABET.addAll(ASGN);
		CUTEPY_ALPHABET.addAll(SMALLER);
		CUTEPY_ALPHABET.addAll(LARGER);
		CUTEPY_ALPHABET.addAll(LETTERS);
		CUTEPY_ALPHABET.addAll(DIGITS);
		CUTEPY_ALPHABET.addAll(UNDERCORE);
		CUTEPY_ALPHABET.add('*');
		CUTEPY_ALPHABET.add('<');
		CUTEPY_ALPHABET.add('>');
		CUTEPY_ALPHABET.add('!');
		return !CUTEPY_ALPHABET.contains(c);
	}
}
