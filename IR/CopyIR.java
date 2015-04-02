//Mark Klara
//mak241@pitt.edu
//Project 3
//CopyIR.java

package IR;

public class CopyIR extends Quadruple
{
	//x := y
	public CopyIR(String argument1, String result)
	{
		op = null;
		arg1 = argument1;     //y
		arg2 = null;
		this.result = result; //x
	}
	
	public String toString()
	{
		return result + " := " + arg1;
	}
}