class Test {
	public static void main(String[] args) {
		System.out.println(new Test2().Start(0));  //Prints 3 (length)
	}
}
class Test2 {
	public int Start(int y) {
		int[] a;
		int x;
		
		a = new int[3];
		x = 3;
		a[0] = 7;
		a[1] = x;
		a[2] = a[0] + a[1];
		
		x = a[0];

		System.out.println(a[0] + a[0]); //Prints 14
		
		return a.length;
	}
}