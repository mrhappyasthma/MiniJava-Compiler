class Test {
	public static void main(String[] args) {
		System.out.println(new Test2().Start(0));
	}
}
class Test2 {
	public int Start(int y) {
		//Arbitrary straight-line code -- do nto use registers $zero, $a0-a3, $v0, or $sp/$fp
		int a;
		int b;
		int c;
		int d;
		
		a = 3;
		b = a;
		c = a + b;
		c = c + 1;
		c = 1 + c;
		d = c - 1 - 1;
		d = 1 - d;
		d = d * d;
		d = -1 * d;
		d = d * -1;
		
		return d;
	}
}