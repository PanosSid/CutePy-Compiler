def main_countdigits():
#{
	#declare x, count

	x = int(input());
	count = 0;
	while (x>0):
	#{
		x = x // 10;
		count = count + 1;
	#}
	print(count);

#}

if __name__ == "__main__":
	main_countdigits();
