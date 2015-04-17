class Test {
	public static void main(String[] args) {
		System.out.println(new Test2().Start(0));  //Prints -25
	}
}
class Test2 {
	public int Start(int y) {
		//Arbitrary straight-line code -- do not use registers $zero, $a0-a3, $v0, or $sp/$fp
		int a;
		int b;
		
		boolean b1;
		boolean b2;
		
		a = 3;
		a = a + a;
		a = a + 1;
		a = 1 + a;
		a = a - 1 - 1;
		a = 1 - a;
		a = a * a;
		a = -1 * a;
		b = b * -1;
		b = 3 * 3;
		
		b1 = true;
		b2 = false;
		b1 = b1 && b2;
		
		return a;
	}
}