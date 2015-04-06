class TestClass{
	public static void main(String[] a){
		System.out.println(this);    //"this" in a static method:  line 3
	}
}

class SecondClass {
    public int Foo(int num){
	int num_aux; 
	int num_aux;                    //Redefined Variable: line 10
	boolean b;
	int[] x;
	
	fakevar = 3;                    //Undefined Variable: line 14
	b = SecondClass;                //Invalid r-value: line 15
	num_aux = this.Foo();           //Wrong number of params:  line 16
	num_aux = this.Foo(false);      //Wrong types of params:  line 17
	num_aux = 1 + SecondClass;      //Invalid operands:  line 18
	num_aux = 1 + false;            //Non-integer operand:  line 19
	b = 3 && true;                  //Boolean operator on non-boolean operand:  line 20
	num_aux = b.length;             //.length on non-aarry:  Line 21
	if(1){							//Non-boolean expr in statement:  Line 22
		System.out.println(1);
	} else {
		System.out.println(2);
	}
	num_aux = false;                //Type mismatch:  Line 27
	return num_aux;
    }
}