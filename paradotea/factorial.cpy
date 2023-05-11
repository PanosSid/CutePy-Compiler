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

if __name__ == "__main__":
	main_factorial();