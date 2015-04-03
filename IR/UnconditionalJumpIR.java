//Mark Klara
//mak241@pitt.edu
//Project 3
//UnconditionalJumpIR.java

package IR;

public class UnconditionalJumpIR extends Quadruple
{
	//goto LABEL
	public UnconditionalJumpIR(Object operator, Object label)
	{
		op = "goto";      //goto
		arg1 = null;
		arg2 = null;
		result = label;   //LABEL
	}
	
	public String toString()
	{
		return op + " " + result;
	}
}