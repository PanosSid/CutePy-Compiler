def main_paradeigma3():
#{ 
    #declare a,b,c

    def A1(x):
    #{ 
        #declare a,e

        def B1(x,y):
        #{
            #declare a
			def C1(y):
			#{
				#declare a
				a = a + 1;	#$ 1 temp var #$
				return(a);
			#}
			
			def C2(x,y):
			#{
				#declare a
				a = a + 1;	#$ 1 temp var #$
				a = a * 2;	#$ 2 temp var #$
				return(a);
			#}
			
            a = a + 9;
			return(a);
        #}

        def B2(x):
        #{ 
            #declare a,e
            a = a + 1;	#$ 1 temp var #$
			e = a // 2;	#$ 2 temp var #$
			return(e);
        #}

		return(3);	#$ 0 temp vars in A1#$
    #}

    def A2(x, y):
    #{ 
        #declare a
        a = a - 1;	#$ 1 temp var #$
        return(a); 
    #}

    #$ ma program #$
    b = b + 2;
	c = c + 3;	

#}

if __name__ == "__main__":
	main_paradeigma3();