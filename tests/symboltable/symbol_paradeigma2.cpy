def main_symbol():
#{ 
    #declare a,b,c

    def P1(x,y):
    #{ 
        #declare a

        def F11(x):
        #{
            #$ body of F11 #$
            #declare a
            b = a;
            a = x;
            c = F11(x);
            return (c);
        #}

        def F12(x):
        #{ 
            #$ body of F12 #$
            c = F11(x);
            return (c);
        #}

		#$ body of P1 #$
		y = x;
		return(y);
    #}

    def P2(y):
    #{ 
        #$ body of P2 #$
        #declare x
        y = 1;
        return(P1(x,y)); 
    #}

    #$ ma program #$
    print(P1(a,b)); 
    print(P2(c)); 

#}

if __name__ == "__main__":
	main_symbol();