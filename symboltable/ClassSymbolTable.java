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
	private int size; //Size in bytes of the entire class
	private int offset; //Offset variable locations based on the offset from any inherited sizes
	
	public ClassSymbolTable(Scope parent, String name, String parentClass)
	{
		super(parent);
		this.parentClass = parentClass;
		this.name = name;
		methods = new Hashtable<String, MethodSymbolTable>();
		size = -1;
		offset = -1;
	}
	
	public Scope enterScope(String name)
	{
		return methods.get(name);
	}
	
	public void addMethod(String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		methods.put(name, new MethodSymbolTable(this, name, paramNames, paramTypes, returnType));
	}
	
	public Hashtable<String, MethodSymbolTable> getMethods()
	{
		return methods;
	}
	
	public void calculateVarOffsets()
	{
		int parentOffset = this.getOffset();
		int localOffset = 0;
		
		Set<String> keys = vars.keySet();
		
		for(String key : keys)
		{
			Variable v = vars.get(key);
			v.setOffset(parentOffset + localOffset);
			
			localOffset += 4;
		}
	}
	
	private int calculateSize()
	{
		size = 0;
		Set<String> keys = vars.keySet();
		
		for(String key : keys)
		{
			//All types of variables will be 4 bytes (int, int[], class references, boolean)
			size += 4;
		}
		
		if(parentClass != null)
		{
			size += ((ClassSymbolTable)parent.enterScope(parentClass)).getSize();
		}
		
		return size;
	}
	
	public int getSize()
	{
		if(size == -1)
		{
			size = calculateSize();
		}
		
		return size;
	}
	
	private int calculateOffset()
	{
		offset = 0;
		
		if(parentClass != null)
		{
			offset = ((ClassSymbolTable)parent.enterScope(parentClass)).getSize();
		}
		
		return offset;
	}
	
	public int getOffset()
	{
		if(offset == -1)
		{
			offset = calculateOffset();
		}
		
		return offset;
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

        public MethodSymbolTable getMethod(String name){
            if(isMethod(name)){
                return (MethodSymbolTable)methods.get(name);
            }
            else{
                return null;
            }
        }
        
        public boolean isMethod(String name){
            return methods.containsKey(name);
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
