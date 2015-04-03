//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//IntegerLiteral.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class IntegerLiteral extends Exp 
{
  public int i;
  public int lineNum;
  public int charNum;

  public IntegerLiteral(int ai, int lineNum, int charNum) 
  {
    i=ai;
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
	  return new Variable(Integer.toString(i), "constant");
  }
}
