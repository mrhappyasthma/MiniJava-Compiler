class Test {
	public static void main(String[] args) {
		System.out.println(new Test2().Start(0));
	}
}
class Test2 {
	public int Start(int y) {
		//Arbitrary straight-line code -- do nto use registers $zero, $a0-a3, $v0, or $sp/$fp
		return y;
	}
}