def main_f1(): 
#{	
	#declare a
	
	def f2(): 
	#{
		return(2);
	#}
	
	def f3(): 
	#{
		def f4(): 
		#{
			a = a + 1; 
			return(4);
		#}
		
		print(f4());
		return(3);
	#}
	
	print(f2());
#}

if __name__ == "__main__":
	main_f1();