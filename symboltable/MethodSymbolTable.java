//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//MethodSymbolTable.java

package symboltable;

import regalloc.*;
import helper.*;
import java.util.List;
import java.util.Hashtable;
import java.util.Set;
import java.util.ArrayList;

public class MethodSymbolTable extends BlockSymbolTable implements Scope
{
	private String name;
	private Hashtable<String, Variable> args;
	private String[] argNames;
	private String returnType;
	
	public MethodSymbolTable(Scope parent, String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		super(parent);
		this.name = name;
		args = new Hashtable<String, Variable>();
		argNames = paramNames;
		
		for(int i = 0; i < paramNames.length; i++)
		{
			String reg = "$a" + i; //Assumes only 4 parameters possible for a method
			args.put(paramNames[i], new Variable(paramNames[i], paramTypes[i], reg));
		}
		
		this.returnType = returnType;
	}
	
	public Variable[] getParameters()
	{
		Variable[] params = new Variable[argNames.length];
		
		for(int i = 0; i < params.length; i++)
		{
			params[i] = args.get(argNames[i]);
		}

		return params;
	}
	
	public int numParameters()
	{
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
	
	public void assignRegisters(RegisterAllocator allocator)
	{
		List<String> keys = Helper.keysToSortedList(vars.keySet());
		
		for(int i = 0; i < keys.size(); i++)
		{
			Variable v = vars.get(keys.get(i));
			v.setRegister(allocator.allocateReg());
		}
	}
}
