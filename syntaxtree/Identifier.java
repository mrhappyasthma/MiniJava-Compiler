//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//Identifier.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Identifier {
  public String s;
  public int lineNum;
  public int charNum;

  public Identifier(String as, int lineNum, int charNum) { 
    s=as;
	this.lineNum = lineNum;
	this.charNum = charNum;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

  public String toString(){
    return s;
  }
}
