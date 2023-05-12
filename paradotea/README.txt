Sidiropoulos Panagiotis 4489 cse84489

CutePy compiler implemented in java.
- Generates the Intermediate code (.int), Symbol Table (.symb) and Final Code (.asm) files
- If compliled with the flag "-c" it generates the equivalent C code (works only for simple files*)

Command to compile cutePy_4489.java file: 
	$- javac cutePy_4489.java

Command to run the CutePy compiler for file x.cpy:
	$- java cutePy_4489 x.cpy

Command to run the CutePy compiler to generate the equivelent C code for file x.cpy:
	$- java cutePy_4489 x.cpy -c

NOTES: 
1. *After completing the generation of the intermediate code, I implemented
the transformation from cpy to C code. However, it was later announced that
this step was optional, so I included it as an optional feature. It's important to
note that the generated C code will only work if the compiled file contains
only one main function, without any nested definitions or other function calls.
Additionally, all variables used in the file must be correctly declared because
the conversion to C code does not utilize the symbol table. The symbol table
was implemented at a later stage, and there was insufficient time to integrate it.

2.Java doesn't have an equivelant method to seek() so in order to not use
an external library I implemented the loading and reading of the file
manually using the FileReader class. So I setted the EOF char to '~' which
is not a part of the cutePy alphabet.Therefore the compiler works fine
 for all CutePy files not containing the char '~'.