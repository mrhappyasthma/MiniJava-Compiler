//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//SymbolTable.java

package symboltable;

import java.util.Hashtable;
import java.util.Set;

public class SymbolTable implements Scope
{
	private Hashtable<String, ClassSymbolTable> classes;
	
	public SymbolTable()
	{
		classes = new Hashtable<String, ClassSymbolTable>();
	}
	
	public void addClass(String name)
	{
		addClass(name, null);
	}
	
	public void addClass(String name, String parentName)
	{
		classes.put(name, new ClassSymbolTable(this, name, parentName));
	}
	
	public Scope enterScope(String name)
	{
		return classes.get(name);
	}
	
	public Scope exitScope()
	{
		return null;
	}

	public ClassSymbolTable getClass(String name){
            if(isClass(name)){
                return (ClassSymbolTable)classes.get(name);
            }
            else{
                return null;
            }
        }
	
	public boolean isClass(String name){
            return classes.containsKey(name);
        }
	
	public Variable lookupVariable(String name)
	{
		return null;
	}
	
	public boolean lookupMethod(String name, String[] paramNames, String[] paramTypes, String returnType)
	{
		return false;
	}
	
	public void print(int indentLevel)
	{
		System.out.println("~~~~~~Symbol Table~~~~~~");
		
		Set<String> keys = classes.keySet();
		
		for(String key : keys)
		{
			classes.get(key).print(indentLevel+1);
			System.out.println();
		}
	}
	
	public Hashtable<String, ClassSymbolTable> getClasses()
	{
		return classes;
	}
}
