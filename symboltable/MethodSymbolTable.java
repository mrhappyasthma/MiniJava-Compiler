//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//MethodSymbolTable.java

package symboltable;

import regalloc.*;
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
			String reg = "$a" + i; //Assumes only 4 parameters possible for a method
			args.put(paramNames[i], new Variable(paramNames[i], paramTypes[i], reg));
		}
		
		this.returnType = returnType;
	}
	
	public Object[] getParameters()
	{
		Object[] params = args.values().toArray();
		Object[] temp = new Object[params.length-1];
		System.arraycopy(params, 1, temp, 0, temp.length);
		reverse(temp);
		System.arraycopy(temp, 0, params, 1, temp.length);
		return params;
	}
	
	private static void reverse(Object[] arr)
	{
		for(int i = 0; i < arr.length / 2; i++)
		{
			Object temp = arr[i];
			arr[i] = arr[arr.length - i - 1];
			arr[arr.length - i - 1 ] = temp;
		}
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
	
	public void assignRegisters(RegisterAllocator allocator)
	{
		Set<String> keys = vars.keySet();
		
		for(String key : keys)
		{
			Variable v = vars.get(key);
			v.setRegister(allocator.allocateReg());
		}
	}
}
