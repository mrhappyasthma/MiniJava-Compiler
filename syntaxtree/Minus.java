//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//Minus.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class Minus extends Exp 
{
  public Exp e1,e2;
  public Variable t;
  
  public Minus(Exp ae1, Exp ae2) 
  {
    e1=ae1; e2=ae2;
  }

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
		Temporary temp = new Temporary();
		t = new Variable(temp.toString(), "temporary");
	}
	
	return t;
  }
}
