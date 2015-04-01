class Factorial{
	public static void main(String[] args){
		System.out.println(new Fac().ComputeFac());
	}
}

class Fac{
    public  ComputeFac (int num){
	int num_aux  ; 
	if (num < 1)
	    num_aux = 1 ;
	else 
	    num_aux = num * (this.ComputeFac (num-1)) ;
	return num_aux ;
    }
}
//line 8 - method error, but it also has an error on line 16
