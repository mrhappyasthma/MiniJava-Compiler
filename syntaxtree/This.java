//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//This.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class This extends Exp 
{
  public Variable t;
	
  public void accept(Visitor v) 
  {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) 
  {
    return v.visit(this);
  }
  
  public Variable generateTAC()
  {
	if(t == null)
	{
		t = new Variable("this", "this");
	}
	
	return t;
  }
}
