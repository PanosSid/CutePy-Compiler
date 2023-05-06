def main_primes():
#{
	#declare i
	
	def isPrime(x):
	#{
		#declare i

		def divides(x,y):
		#{
			if (y == (y//x) * x):
				return (1);
			else:
				return (0);
		#}

		i = 2;
		while (i<x):
		#{
			if (divides(i,x)==1):
				return (0);
			i = i + 1;
		#}
		return (1);

	#}
	
	#$ body of main_primes #$
	i = 2;
	while (i<=30):
	#{
		if (isPrime(i)==1):
		#{
			print(i);
			i = i + 1;
		#}
		else:				
			i = i + 1;
	#}
#}

if __name__ == "__main__":
	main_primes();