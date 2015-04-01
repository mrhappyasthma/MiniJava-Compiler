//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//ClassSymbolTable.java

package symboltable;

import java.util.Hashtable;
import java.util.Set;

public class ClassSymbolTable extends BlockSymbolTable implements Scope
{
	private String name;
	private String parentClass;
	private Hashtable<String, MethodSymbolTable> methods;
	
	public ClassSymbolTable(Scope parent, String name, String parentClass)
	{
		super(parent);
		this.parentClass = parentClass;
		this.name = name;
		methods = new Hashtable<String, MethodSymbolTable>();
	}
	
	public Scope enterScope(String name)
	{
		return methods.get(name);
	}
	
	public void addMethod(String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		methods.put(name, new MethodSymbolTable(this, name, paramNames, paramTypes, returnType));
	}
	
	//Helper function
	private boolean lookupParentsMethod(String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		if(parentClass != null)
		{
			Scope temp = parent.enterScope(parentClass);
						
			if(temp != null)
			{
				return temp.lookupMethod(name, paramNames, paramTypes, returnType);
			}
		}
		
		return false;
	}
	
	public boolean lookupMethod(String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		MethodSymbolTable method = methods.get(name);
		
		if(method == null)
		{
			return lookupParentsMethod(name, paramNames, paramTypes, returnType) | false;
		}
		
		if(!method.getReturnType().equals(returnType))
		{
			return lookupParentsMethod(name, paramNames, paramTypes, returnType) | false;
		}
	
		Object[] parameters = method.getParameters();
	
		if(parameters.length != paramNames.length)
		{
			return lookupParentsMethod(name, paramNames, paramTypes, returnType) | false;
		}
	
		for(int i = 0; i < parameters.length; i++)
		{
			Variable param = (Variable)parameters[i];
			if(!param.getName().equals(paramNames[i]))
			{
				return lookupParentsMethod(name, paramNames, paramTypes, returnType) | false;
			}
			
			if(!param.getType().equals(paramTypes[i]))
			{
				return lookupParentsMethod(name, paramNames, paramTypes, returnType) | false;
			}
		}
		
		return true;
	}
	
	public void print(int indentLevel)
	{
		System.out.print("class " + name);
		
		if(parentClass != null)
		{
			System.out.print(" extends " + parentClass);
		}
		
		Set<String> keys = vars.keySet();
		
		for(String key : keys)
		{
			printIndentation(indentLevel);
			System.out.print(vars.get(key).getType() + " " + vars.get(key).getName() + ";");
		}
		
		keys = methods.keySet();
		
		for(String key : keys)
		{
			printIndentation(indentLevel);
			System.out.print(" - (" + methods.get(key).getReturnType() + ") " + methods.get(key).getName() + "(");
			
			Object[] params = methods.get(key).getParameters();
			
			for(int i = 0; i < params.length-1; i++)
			{
				Variable param = (Variable) params[i];
				System.out.print(param.getType() + " " + param.getName() + ", ");
			}
			
			Variable param = (Variable) params[params.length-1];
			System.out.print(param.getType() + " " + param.getName() + ")");
			methods.get(key).print(indentLevel+1);
		}
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getParentClass()
	{
		return parentClass;
	}
	
	public Variable localVarLookup(String name)
	{
		Variable var = vars.get(name);
		
		if(var != null)
		{
			return var;
		}

		if(parentClass != null)
		{
			Scope temp = parent.enterScope(parentClass);
				
			if(temp != null)
			{
				return temp.lookupVariable(name);
			}
		}
		
		return null;
	}
}