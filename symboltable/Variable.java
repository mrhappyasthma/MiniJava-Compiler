//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//Variable.java

package symboltable;

public class Variable
{
	private String name;
	private String type;

	public Variable(String name, String type)
	{
		this.name = name;
		this.type = type;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String toString()
	{
		return getName();
	}
	
	public String getType()
	{
		return type;
	}
}
