package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;

public class ArrayLength extends Exp {
  public Exp e;
  
  public ArrayLength(Exp ae) {
    e=ae; 
  }

  public void accept(Visitor v) {
    v.visit(this);
  }

  public Type accept(TypeVisitor v) {
    return v.visit(this);
  }
}
