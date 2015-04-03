//Mark Klara
//mak241@pitt.edu
//Project 3
//UnaryAssignmentIR.java

package IR;

public class UnaryAssignmentIR extends Quadruple
{
	//x := op y
	public UnaryAssignmentIR(Object operator, Object y, Object x)
	{
		op = operator;   //op
		arg1 = y;        //y
		arg2 = null;
		result = x;      //x
	}
	
	public String toString()
	{
		return result + " := " + op + " " + arg1;
	}
}