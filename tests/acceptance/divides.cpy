def main_divides():
#{
	#declare x, y
	x = int(input());
	y = int(input());
	if (y == (y//x) * x):
		print(1);
	else:
		print(0);
#}

if __name__ == "__main__":
	main_divides();