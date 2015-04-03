//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//IdentifierExp.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class IdentifierExp extends Exp 
{
  public String s;
  public int lineNum;
  public int charNum;

  public IdentifierExp(String as, int lineNum, int charNum) 
  { 
    s=as;
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
	  return new Variable(s, "id");
  }
}
