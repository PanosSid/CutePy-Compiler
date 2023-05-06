def main_f():
#{
	#declare glob
	
	def F1(param):
	#{
		#$ param = glob = 1 #$
		
		def F11():
		#{
			#declare var
			
			def F111():
			#{
				def F1111():
				#{
					#declare var
					var = 0;
					return(glob + param + var);
				#}
				return(F1111());
			#}
			
			var = glob;	
			return(F111());
		#}	
			
		return(F11());
	#}
	
	glob = 1;
	print(F1(glob)); 	#$ prints 2 #$
#}

if __name__ == "__main__":
	main_f();