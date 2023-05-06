def main_isPrime():
#{
	#declare i,x

	def divides(x,y):
	#{
		if (y == (y//x) * x):
			return (1);
		else:
			return (0);
	#}
	
	x = int(input());
	i = 2;
	while (i<x):
	#{
		if (divides(i,x)==1):
		#{
			print(0);
		#}
		i = i + 1;
	#}
	print(1);

#}

if __name__ == "__main__":
	main_isPrime();
