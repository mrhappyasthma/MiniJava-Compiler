//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//Call.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import helper.*;
import symboltable.Variable;

public class Call extends Exp 
{
  public Exp e;
  public Identifier i;
  public ExpList el;
  public Variable t;
  public int lineNum;
  public int charNum;
  
  public Call(Exp ae, Identifier ai, ExpList ael, int lineNum, int charNum) 
  {
    e=ae; i=ai; el=ael;
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
