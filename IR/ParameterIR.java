//Mark Klara
//mak241@pitt.edu
//Project 3
//ParameterIR.java

package IR;

public class ParameterIR extends Quadruple
{
	//param x
	public ParameterIR(Object x)
	{
		op = "param";   //param
		arg1 = x;       //x
		arg2 = null;
		result = null;
	}
	
	public String toString()
	{
		return op + " " + arg1;
	}
}