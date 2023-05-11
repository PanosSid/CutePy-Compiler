def main_negativeVars():
#{
	#declare neg_x,x
	
	def getThousand():
	#{
		return(1000); 
	#}
	
	x = 5;
	neg_x = -x;
	
	if (neg_x < 0 and [neg_x == -x]):
		print(1);
	else:
		print(0);
	
		
	if (0 > -getThousand()):
		print(1);
	else:
		print(0);
	

	if (0 == (-getThousand() + 1000)):
		print(1);
	else:
		print(0);

#}
	
if __name__ == "__main__":
	main_negativeVars();