def main_factorial():
#{
	#$ declarations #$
	#declare x
	#dec7Êlare i,fact

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

if __name__ == "__main__":
#$ call of main functions #$
	main_factorial();
	main_fibonacci();
	main_countdigits();
	main_primes();
	