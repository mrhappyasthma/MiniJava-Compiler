//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//BlockSymbolTable.java

package symboltable;

import java.util.Hashtable;
import java.util.Set;

public class BlockSymbolTable implements Scope
{
	private Scope parent;
	private Hashtable<String, Variable> vars;
	private Hashtable<String, BlockSymbolTable> blocks;

	public BlockSymbolTable()
	{
		this(null);
	}
	
	public BlockSymbolTable(Scope parent)
	{
		this.parent = parent;
		vars = new Hashtable<String, Variable>();
		blocks = new Hashtable<String, BlockSymbolTable>();
	}

	public Scope enterScope(String name)
	{
		return blocks.get(name);
	}

	public Scope exitScope()
	{
		return parent;
	}

	public void addBlock(String name)
	{
		blocks.put(name, new BlockSymbolTable(this));
	}
	
	public void addVariable(String name, String type)
	{
		int line = 0;  //placeholdr
		int col = 0;   //placeholder
		
		if(vars.get(name) != null)
		{
			System.err.println("Multiply defined identifier " + name + " at line " + line + ", character " + col);
		}
		
		vars.put(name, new Variable(name, type));
	}
	
	public Variable lookupVariable(String name)
	{
		Variable var = vars.get(name);
		
		if(var != null)
		{
			return var;
		}
		else
		{	
			return parent.lookupVariable(name);
		}
	}
	
	public boolean lookupMethod(String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		return parent.lookupMethod(name, paramNames, paramTypes, returnType);
	}
	
	public void printIndentation(int indentLevel)
	{
		System.out.println("");
		for(int i = 0; i < indentLevel; i++)
		{
			System.out.print("\t");
		}
	}
	
	public void print(int indentLevel)
	{
		Set<String> keys = vars.keySet();
		
		for(String key : keys)
		{
			printIndentation(indentLevel);
			System.out.print(vars.get(key).getType() + " " + vars.get(key).getName() + ";");
		}
		
		keys = blocks.keySet();
		
		for(String key : keys)
		{
			blocks.get(key).print(indentLevel+1);
		}
	}
}
