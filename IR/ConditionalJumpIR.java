//Mark Klara
//mak241@pitt.edu
//Project 3
//ConditionalJumpIR.java

package IR;

public class ConditionalJumpIR extends Quadruple
{
	//iffalse x goto LABEL
	public ConditionalJumpIR(String x, String label)
	{
		op = "iffalse";       //iffalse
		arg1 = x;             //x
		arg2 = "goto";        //goto
		result = label;       //LABEL
	}
	
	public String toString()
	{
		return op + " " + arg1 + " " + arg2 + " " + result;
	}
}