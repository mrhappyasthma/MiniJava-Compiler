//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//NewObject.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class NewObject extends Exp
{
  public Identifier i;
  public Variable t;
  
  public NewObject(Identifier ai) 
  {
    i=ai;
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
