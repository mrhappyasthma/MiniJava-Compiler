class Test {
	public static void main(String[] args) {
		System.out.println(new Test2().Start(0));  //Prints 3 (length)
	}
}
class Test2 extends Test3 {
	int d;
	int[] e;
	public int Start(int y) {
		e = new int[3];
		a = 3;
		d = 4;
		e[0] = a;
		e[1] = d;
		e[2] = e[0] + e[1];
		
		System.out.println(e[2]); //Prints 7
		
		return e.length;
	}
}

class Test3 extends Test4{
	int b;
	boolean c;
}

class Test4{
	int a;
}