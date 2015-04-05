package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class ArrayAssign extends Statement {
  public Identifier i;
  public Exp e1,e2;
  public int lineNum;
  public int charNum;

  public ArrayAssign(Identifier ai, Exp ae1, Exp ae2, int lineNum, int charNum) {
    i=ai; e1=ae1; e2=ae2;
	this.lineNum = lineNum;
	this.charNum = charNum;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }

}

