def main_fc_example1():
#{
	#declare a,b,c
	
	def f(a,b):
	#{
		b = a + 1;
		c = 4; 
		return(b);
	#}
	
	a = 1;
	c = f(a,b);
	print(c);
	print(b);
#}

if __name__ == "__main__":
	main_fc_example1();