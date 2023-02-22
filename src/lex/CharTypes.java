package lex;

import java.util.Arrays;
import java.util.List;

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
	
	public static final List<String> REL_OPS = Arrays.asList(new String[] { "<", "<=", ">", ">=", "<>", "=="});
	
//	public static final List<List<Character>> ALL_TYPES = new ArrayList<>();
//	
//	public static void initListWithAllCharTypes() {
//		ALL_TYPES.add(ADD_OPS);
//		ALL_TYPES.add(DELIMITERS);
//		ALL_TYPES.add(GROUP_SUMBOLS);
//		ALL_TYPES.add(ASGN);
//		ALL_TYPES.add(SMALLER);
//		ALL_TYPES.add(LARGER);
//		ALL_TYPES.add(LETTERS);
//		ALL_TYPES.add(DIGITS);
//	}
}
