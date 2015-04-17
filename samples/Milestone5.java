class Test {
	public static void main(String[] args) {
		System.out.println(new Test2().Start());  //Prints 5
	}
}
class Test2 {
	public int Start() {
		//Arbitrary straight-line code -- do not use registers $zero, $a0-a3, $v0, or $sp/$fp
		int a;
		int b;
		
		a = 3;
		b = 1;
		
		if(b < a)
		{
			a = 1;
		}
		else
		{
			a = 0;
		}
		
		while(a < 5)
		{
			a = a + 1;
		}
		
		return a;
	}
}