//Mark Klara
//mak241@pitt.edu
//Project 3
//CallIR.java

package IR;

public class CallIR extends Quadruple
{
	//x := call f, NUMPARAMS
	public CallIR(String f, String NUMPARAMS, String x)
	{
		op = "call";         //call
		arg1 = f;            //f
		arg2 = NUMPARAMS;    //NUMPARAMS
		result = x;          //x
	}
	
	public String toString()
	{
		return result + " := " + op + " " + arg1 + ", " + arg2;
	}
}