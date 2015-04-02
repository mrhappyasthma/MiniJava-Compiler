//Mark Klara
//mak241@pitt.edu
//Project 3
//IndexedAssignmentIR.java

package IR;

public class IndexedAssignmentIR extends Quadruple
{
	//x := y[i]
	public IndexedAssignmentIR(String y, String i, String x)
	{
		op = null;       
		arg1 = y;             //y
		arg2 = i;             //i
		result = x;           //x
	}
	
	public String toString()
	{
		return result + " := " + arg1 + "[" + arg2 + "]";
	}
}