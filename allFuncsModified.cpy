def main_factorial():
#{
	#$ declarations #$
	#declare x
	#declare i,fact

	#$ body of main_factorial #$
	x = int(input());
	fact = 1;
	i = 1;
	while (i<=x):
	#{
		fact = fact * i;
		i = i + 1;
	#}
	print(fact);

#}

def main_fibonacci():
#{
	#declare x

	def fibonacci(x):
	#{
		if (x<=1):
			return(x);
		else:
			return (fibonacci(x-1)+fibonacci(x-2));
	#}

	x = int(input());
	print(fibonacci(x));

#}

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
		if (isPrime(i)==1):
			print(i);
	i = i + 1;

#}

def main_factorial2():
#{
	#$ declarations #$
	#declare x
	#declare i,fact

	#$ body of 
		main_factorial2 # panos 
		%^&(
	#$
	x = int(input());
	fact =1;
	i = 1;
	while ([i<=x] and [x == 0] or not [i != -x] ):
	#{
		fact = fact * i;
		i = i + 1;
	#}
	if ([(a+b)+d>5 and x>5]):
	#{
		print(fact);
	#}
	else:
	#{
		print(fact);
		return(-fact);
	#}
	

#}


if __name__ == "__main__":
#$ call of main functions #$
	main_factorial();
	main_fibonacci();
	main_countdigits();
	main_primes();
	main_factorial2();
	