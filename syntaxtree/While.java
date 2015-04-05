package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class While extends Statement {
  public Exp e;
  public Statement s;
  public int lineNum;
  public int charNum;

  public While(Exp ae, Statement as, int lineNum, int charNum) {
    e=ae; s=as; 
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

