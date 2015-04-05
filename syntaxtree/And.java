//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//And.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class And extends Exp 
{
  public Exp e1,e2;
  public Variable t;
  public int lineNum;
  public int charNum;
  
  public And(Exp ae1, Exp ae2, int lineNum, int charNum)
  { 
    e1=ae1; e2=ae2;
	this.lineNum = lineNum;
	this.charNum = charNum;
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
