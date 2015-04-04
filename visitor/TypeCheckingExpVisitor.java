package visitor;

import syntaxtree.*;
import symboltable.*;

public class TypeCheckingExpVisitor extends TypeDepthFirstVisitor {


	Scope currentScope;
	public TypeCheckingExpVisitor(Scope s){
		currentScope = s;
	}

  // Exp e1,e2;
  public Type visit(And n) {
	if(!(n.e1.accept(this) instanceof BooleanType) || !(n.e2.accept(this) instanceof BooleanType )){
		System.out.println("Attempt to use boolean operator && on non-boolean operands at line 0, character 0");
	}
    	return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(LessThan n) {
        if (!( n.e1.accept(this) instanceof IntegerType)){
                System.out.println("Non-integer operand for operator <, at line 0, character 0");   
        }
        if(! (n.e2.accept(this) instanceof IntegerType)){
                System.out.println("Non-integer operand for operator <, at line 0, character 0");   
        }
	return new BooleanType();

   
  }

  // Exp e1,e2;
  public Type visit(Plus n) {
	if (!(n.e1.accept(this) instanceof IntegerType)){
                System.out.println("Non-integer operand for operator +, at line 0, character 0");   
        }
        if(! (n.e2.accept(this) instanceof IntegerType)){
                System.out.println("Non-integer operand for operator +, at line 0, character 0");   
        }
	return new IntegerType();

  }

  // Exp e1,e2;
  public Type visit(Minus n) {
	if (!( n.e1.accept(this) instanceof IntegerType)){
		System.out.println("Non-integer operand for operator -, at line 0, character 0");	
	}
	if(! (n.e2.accept(this) instanceof IntegerType)){
		System.out.println("Non-integer operand for operator -, at line 0, character 0");   
	}
	return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Times n) {
	if (!( n.e1.accept(this) instanceof IntegerType)){
                System.out.println("Non-integer operand for operator *, at line 0, character 0");   
        }
        if(! (n.e2.accept(this) instanceof IntegerType)){
                System.out.println("Non-integer operand for operator *, at line 0, character 0");   
        }
	return new IntegerType();

   
  }

  // Exp e1,e2;
  public Type visit(ArrayLookup n) {
    n.e1.accept(this);
    n.e2.accept(this);
    return new IntegerType();
  }

  // Exp e;
  public Type visit(ArrayLength n) {
	if(!(n.e.accept(this) instanceof IntArrayType)){
		System.out.println("Length property only applies to arrays line 0, character 0");
	}
	return new IntegerType();  
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public Type visit(Call n) {
    n.e.accept(this);
    n.i.accept(this);
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
  return null;	
  }

  // int i;
  public Type visit(IntegerLiteral n) {
	return new IntegerType();
  }

  public Type visit(True n) {
	return new BooleanType();
  }

  public Type visit(False n) {
	return new BooleanType();
  }

  // String s;
  public Type visit(IdentifierExp n) {
	//return var type
	return null;
  }

  public Type visit(This n) {
	//return current class type
	return null;
  }

  // Exp e;
  public Type visit(NewArray n) {
    n.e.accept(this);
	return new IntArrayType();
  }

  // Identifier i;
  public Type visit(NewObject n) {
	return new IdentifierType(n.i.s);
}

  // Exp e;
  public Type visit(Not n) {
	n.e.accept(this);
	return new BooleanType();
  }

  // String s;
  public Type visit(Identifier n) {
	return null;	
  }
}
