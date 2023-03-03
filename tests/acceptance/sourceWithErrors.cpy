 main_factorial():
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

 main_factorial():
#{
	c = 1

#}

if __name__ == "__main__":
#$ call of main functions #$
	main_factorial();
	main_fibonacci();
	main_countdigits();
	main_primes();
	