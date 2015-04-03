//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//Temporary.java

package helper;

public class Temporary
{
	private static int nextNumber;
	private final int num;
	
	public Temporary()
	{
		num = nextNumber++;
	}
	
	public String toString()
	{
		return "t" + num;
	}
	
	public int getNum()
	{
		return num;
	}
	
	public void reset()
	{
		nextNumber = -1;
	}
}