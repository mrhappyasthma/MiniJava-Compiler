package visitor;

import syntaxtree.*;
import symboltable.*;

public class TypeCheckingExpVisitor extends TypeDepthFirstVisitor {
	
	MethodSymbolTable currMethod;
	ClassSymbolTable currClass;
	SymbolTable symTable;


	public TypeCheckingExpVisitor(MethodSymbolTable m, ClassSymbolTable c, SymbolTable s){
		
		currMethod = m;
		currClass = c;
		symTable = s;

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
	//call something that isn't a method
    	String methName = n.i.s;
	String className = ((IdentifierType) n.e.accept(this)).s;
	ClassSymbolTable cst = symTable.getClass(className);
	if(!(cst.isMethod(methName))){
		System.out.println("Attempt to call a non-method at line"+n.i.lineNum+", character "+n.i.charNum);
	}
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
	//???? NOT SURE IF IT IS WHAT I NEED
	return new IdentifierType(n.s);
  }

  public Type visit(This n) {
	//currClass == main class?
	//return currClass type
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
	if(!(n.e.accept(this) instanceof BooleanType )){
                System.out.println("Attempt to use boolean operator ! on non-boolean operand at line 0, character 0");
        }
        return new BooleanType();
  }

  // String s;
  public Type visit(Identifier n) {
	return new IdentifierType(n.s);	
  }
}
