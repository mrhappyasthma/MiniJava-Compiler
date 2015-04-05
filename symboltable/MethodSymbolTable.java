//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//MethodSymbolTable.java

package symboltable;

import java.util.Hashtable;
import java.util.Set;

public class MethodSymbolTable extends BlockSymbolTable implements Scope
{
	private String name;
	private Hashtable<String, Variable> args;
	private String returnType;
	
	public MethodSymbolTable(Scope parent, String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		super(parent);
		this.name = name;
		args = new Hashtable<String, Variable>();
		
		for(int i = 0; i < paramNames.length; i++)
		{
			args.put(paramNames[i], new Variable(paramNames[i], paramTypes[i]));
		}
		
		this.returnType = returnType;
	}
	
	public Object[] getParameters()
	{
		return args.values().toArray();
	}

	public int numParameters(){
            return getParameters().length;
        }
	
	public String getReturnType()
	{
		return returnType;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Variable localVarLookup(String name)
	{
		Variable var = vars.get(name);
		
		if(var != null)
			return var;
		else
			return args.get(name);
	}
}
