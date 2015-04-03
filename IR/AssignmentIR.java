//Mark Klara
//mak241@pitt.edu
//Project 3
//AssignmentIR.java

package IR;

public class AssignmentIR extends Quadruple
{
	//x := y op z
	public AssignmentIR(Object operator, Object y, Object z, Object x)
	{
		op = operator;    //op
		arg1 = y;         //y
		arg2 = z;         //z
		result = x;       //x
	}
	
	public String toString()
	{
		return result + " := " + arg1 + " " + op + " " + arg2;
	}
}