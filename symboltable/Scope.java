//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//Scope.java

package symboltable;

public interface Scope
{
	public Scope enterScope(String name);
	public Variable lookupVariable(String name);
	public boolean lookupMethod(String name, String[] paramNames, String[] paramTypes, String returnType);
	public Scope exitScope();
	public void print(int indentLevel);
}
