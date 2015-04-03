//Modified by:  Mark Klara
//mak241@pitt.edu
//Project 3
//Exp.java

package syntaxtree;
import visitor.Visitor;
import visitor.TypeVisitor;
import symboltable.Variable;

public abstract class Exp {
  public abstract void accept(Visitor v);
  public abstract Type accept(TypeVisitor v);
  public abstract Variable generateTAC();
}
