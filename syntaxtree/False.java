//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//False.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import symboltable.Variable;

public class False extends Exp 
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
	  return new Variable("false", "constant");
  }
}
