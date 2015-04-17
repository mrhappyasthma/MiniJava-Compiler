//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//Variable.java

package symboltable;

public class Variable
{
	private String name;
	private String type;
	private int offset; //Used by class member variables for an offset
	private String register; //Used by local variables to store the register string mapped to it by the register allocator

	public Variable(String name, String type)
	{
		this.name = name;
		this.type = type;
		offset = -1;
		register = null;
	}
	
	public Variable(String name, String type, int x)
	{
		this(name, type);
		offset = x;
	}
	
	public Variable(String name, String type, String reg)
	{
		this(name, type);
		register = reg;
	}
	
	public void setRegister(String reg)
	{
		register = reg;
	}
	
	public void setOffset(int x)
	{
		offset = x;
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
	
	public int getOffset()
	{
		return offset;
	}
	
	public String getRegister()
	{
		return register;
	}
}
