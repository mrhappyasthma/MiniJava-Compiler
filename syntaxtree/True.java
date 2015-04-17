//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//True.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class True extends Exp 
{
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
	  return new Variable("1", "constant");
  }
}
