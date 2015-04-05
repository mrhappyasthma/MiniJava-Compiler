//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//VoidType.java

package syntaxtree;

import visitor.Visitor;
import visitor.TypeVisitor;

public class VoidType extends Type 
{
  public void accept(Visitor v) 
  {}

  public Type accept(TypeVisitor v) 
  {
    return null;
  }
}
