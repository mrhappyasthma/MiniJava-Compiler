package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class Assign extends Statement {
  public Identifier i;
  public Exp e;
  public int lineNum;
  public int charNum;

  public Assign(Identifier ai, Exp ae, int lineNum, int charNum) {
    i=ai; e=ae; 
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

