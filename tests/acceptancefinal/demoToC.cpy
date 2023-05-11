#$ 
	SIDIROPOULOS PANAGIOTIS AM:4489 cse84489
	This file demonstates the correct generation of the intermediate code
	through the generation and execution of the equivalent c code.
	Note: in order to generate the c code you have to add a "-c"
	arg to the compile command.
	
	command: java cutyPy_4489 demoToC.cpy -c
	
	Race to 10, "a" increments by 5, "b" doubles
	etc execution
	a = 1 -> 6 -> 11
	b = 2 -> 4 -> 8
	output = 011 (means a won with value 11)
#$

def main_firstToTen():
#{
	#declare a,b,i_a,i_b 
	a = int(input());
	b = int(input());
	
	while (a < 10 and b < 10 ):
	#{	
		i_a = 0;
		i_b = 1;
		
		while (i_a < 5):
		#{
			a = a + 1;
			i_a = i_a + 1;
		#}
		
		
		while (i_b > 0):
		#{
			b = b * 2;
			i_b = i_b - 1;
		#}
	#}
	
	if (a >= 10 and b >= 10):
	#{
		print(22222);	#$ draw #$
	#}
	else:
	#{
		if (a >= 10): 
		#{
			print(0);	#$ a won #$
			print(a);
		#}
		else:
		#{
			
			print(1);	#$ b won #$
			print(b);
		#}
	#}
	 
#}
if __name__ == "__main__":
	main_firstToTen();
	