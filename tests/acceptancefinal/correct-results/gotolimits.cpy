def main_gotolimits(): 
#{
	#declare x, r
	#declare upper_limit, lower_limit
	
	def goToLower():
	#{
		while( x > lower_limit):
			x = x - 1;
		return(x);
	#}
	
	def goToUpper():
	#{	
		while( x < upper_limit): #{
			x = x + 1;
		#}
		return(x);
	#}

	lower_limit = int(input());	
	x = int(input());
	upper_limit = int(input());	
	
	if ([[x >= 0] and [x <=0]] or [[lower_limit < 0 or upper_limit < 0 ] or [lower_limit >= upper_limit] or [x < lower_limit] or [x > upper_limit]] ):
		print(-99999); 
	else:
	#{
		if (not [(x - lower_limit) != (upper_limit - x)]):
			print(0);   
		else:
		#{
			if ((x - lower_limit) < (upper_limit - x)):
				r = goToLower();  
			else:#{
				print(x);
				print(upper_limit);
				r = goToUpper();
			#}
			print(r);	
		#}
	#}	
#}

if __name__ == "__main__":
	main_gotolimits();
	