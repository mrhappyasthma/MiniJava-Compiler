package visitor;

import syntaxtree.*;
import symboltable.*;

public class TypeCheckingVisitor implements TypeVisitor {
	private Scope currentScope;
	private ClassSymbolTable currClass;
    private MethodSymbolTable currMethod;
    private SymbolTable symTable;
    public boolean errorDetected;
	private int blockNumber;
	
	public TypeCheckingVisitor(Scope s){
		currentScope = s; 
		errorDetected = false;
        symTable = (SymbolTable)currentScope;
		blockNumber=0;
	}
	//create numbers (as strings) for the blocks
	public String nextBlockNumber(){
		blockNumber++;
		return ("" + blockNumber);
	}
	
	//Helper function to compare types
	public boolean compareTypes(Type t1, Type t2){
		if(t1 instanceof IntegerType && t2 instanceof IntegerType){
			return true;
		}	
		else if(t1 instanceof BooleanType && t2 instanceof BooleanType){
            return true;
        }
		else if(t1 instanceof IntArrayType && t2 instanceof IntArrayType){
            return true;
        }
		else if(t1 instanceof IdentifierType && t2 instanceof IdentifierType){
			if(((IdentifierType)t1).s.equals(((IdentifierType)t2).s))
				return true;
			else
				return false;
        }
		
		return false;
	}

	//Helper function to turn Strings (i.e. "int", "boolean", etc.) into Type
	public Type strToType(String str){
		if(str.equals("int")){
			return new IntegerType();
		}
		else if(str.equals("int[]")){
			return new IntArrayType();
		}
		else if(str.equals("boolean")){
			return new BooleanType();
		}
		else{
			Variable v = currentScope.lookupVariable(str);
			if(v == null)
				return new IdentifierType(str);
			else
				return strToType(v.getType());
		}
	}

	//Helper function to check if we have a valid integer types
	public boolean isInteger(Type t)
	{
		if(t instanceof IntegerType)
			return true;
		
		if(t instanceof IdentifierType){
			if(strToType(((IdentifierType)t).s) instanceof IntegerType)
				return true;
			else
				return false;
		}
		
		return false;
	}
	
	//Helper function to check if we have a valid boolean types
	public boolean isBoolean(Type t)
	{
		if(t instanceof BooleanType)
			return true;
		
		if(t instanceof IdentifierType){
			if(strToType(((IdentifierType)t).s) instanceof BooleanType)
				return true;
			else
				return false;
		}
		
		return false;
	}
	
	//Helper function to check if we have a valid int array types
	public boolean isIntArray(Type t)
	{
		if(t instanceof IntArrayType)
			return true;
		
		if(t instanceof IdentifierType){
			if(strToType(((IdentifierType)t).s) instanceof IntArrayType)
				return true;
			else
				return false;
		}
		
		return false;
	}
	
  // MainClass m;
  // ClassDeclList cl;
  public Type visit(Program n) {
	n.m.accept(this);
	for ( int i = 0; i < n.cl.size(); i++ ) {
		n.cl.elementAt(i).accept(this);
	}
  	return null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public Type visit(MainClass n) {
        
	currentScope = currentScope.enterScope(n.i1.toString()); //Enter class
    currClass = (ClassSymbolTable) currentScope;
    currentScope = currentScope.enterScope("main");
    currMethod = (MethodSymbolTable)currentScope;
		
	n.i1.accept(this);
	n.i2.accept(this);
    n.s.accept(this);

	currentScope = currentScope.exitScope(); //Exit "main" method
	currentScope = currentScope.exitScope(); //Exit class
  	return null; 
  }
  
  // Identifier i;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclSimple n) {
	currentScope = currentScope.enterScope(n.i.toString()); //Enter class
	currClass = (ClassSymbolTable) currentScope;	
        n.i.accept(this);
    	for ( int i = 0; i < n.vl.size(); i++ ) {
       		 n.vl.elementAt(i).accept(this);
    	}
    	for ( int i = 0; i < n.ml.size(); i++ ) {
        	n.ml.elementAt(i).accept(this);
    	}
			
   	currentScope = currentScope.exitScope(); //Exit class
  	return null;	
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclExtends n) {
    	currentScope = currentScope.enterScope(n.i.toString()); //Enter class
		currClass = (ClassSymbolTable) currentScope;	
    	n.i.accept(this);
    	n.j.accept(this);
    	for ( int i = 0; i < n.vl.size(); i++ ) {
       		n.vl.elementAt(i).accept(this);
    	}
    	for ( int i = 0; i < n.ml.size(); i++ ) {
			n.ml.elementAt(i).accept(this);
    	}		
    	currentScope =currentScope.exitScope(); //Exit class
   	return null; 
   }

  // Type t;
  // Identifier i;
  public Type visit(VarDecl n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  // Type t;
  // Identifier i;
  // FormalList fl;
  // VarDeclList vl;
  // StatementList sl;
  // Exp e;
  public Type visit(MethodDecl n) {
	currentScope = currentScope.enterScope(n.i.toString()); //Enter method
	currMethod = (MethodSymbolTable)currentScope;	
    	n.t.accept(this);
    	n.i.accept(this);
    	for ( int i = 0; i < n.fl.size(); i++ ) {
       		 n.fl.elementAt(i).accept(this);
    	}
    	for ( int i = 0; i < n.vl.size(); i++ ) {
        	n.vl.elementAt(i).accept(this);
    	}
    	for ( int i = 0; i < n.sl.size(); i++ ) {
        	n.sl.elementAt(i).accept(this);
    	}
    	n.e.accept(this);
		
    	currentScope = currentScope.exitScope(); //Exit method
  	return null;   
  }

  // Type t;
  // Identifier i;
  public Type visit(Formal n) {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  public Type visit(IntArrayType n) {
  	return null;
  }

  public Type visit(BooleanType n) {
  	return null;
  }

  public Type visit(IntegerType n) {
  	return null;
  }

  // String s;
  public Type visit(IdentifierType n) {
  	return null;
  }

  // StatementList sl;
  public Type visit(Block n) {
		String blockNum = nextBlockNumber();
		currentScope = currentScope.enterScope(blockNum); //Enter block
		
		for ( int i = 0; i < n.sl.size(); i++ ) {
			n.sl.elementAt(i).accept(this);
		}
		
		currentScope = currentScope.exitScope(); //Exit block
  		return null;  
  }

  // Exp e;
  // Statement s1,s2;
  public Type visit(If n) {
    if(!isBoolean(n.e.accept(this))){
		System.out.println("Non-boolean expression used as the condition of if statement at line 0, character 0");
	}
	
    n.s1.accept(this);
    n.s2.accept(this);
	
  	return null;
  }

  // Exp e;
  // Statement s;
  public Type visit(While n) {
	if(!isBoolean(n.e.accept(this))){
        errorDetected=true;
		System.out.println("Non-boolean expression used as the condition of while statement at line 0, character 0");
    }
    	
	n.s.accept(this);
	
	return null;
  }

  // Exp e;
  public Type visit(Print n) {
    	n.e.accept(this);
  	return null;
  }
  
  // Identifier i;
  // Exp e;
  public Type visit(Assign n) {
    	n.i.accept(this);
	
	if(n.i.s.equals("this")){
		errorDetected=true;
		System.out.println("Invalid l-value, "+n.i.s+" is a this, at line "+ n.i.lineNum+", character "+ n.i.charNum);
	}
	if(symTable.isClass(n.i.s)){
		errorDetected=true;
		System.out.println("Invalid l-value, "+n.i.s+" is a class, at line "+ n.i.lineNum+", character "+ n.i.charNum);
	}
	else{
		if(currClass.isMethod(n.i.s)){
			errorDetected=true;
			System.out.println("Invalid l-value, "+n.i.s+" is a method, at line "+ n.i.lineNum+", character "+ n.i.charNum);
		}
		
	}
	return null;

 }
  // Identifier i;
  // Exp e1,e2;
  public Type visit(ArrayAssign n) {
    n.i.accept(this);
    n.e1.accept(this);
    n.e2.accept(this);
	return null;
  }

  // Exp e1,e2;
  public Type visit(And n) {
	if(!isBoolean(n.e1.accept(this))){
        System.out.println("Attempt to use boolean operator && on non-boolean operands at line 0, character 0");   
	}
    if(!isBoolean(n.e1.accept(this))){
        System.out.println("Attempt to use boolean operator && on non-boolean operands at line 0, character 0");
    }

    return new BooleanType();
   }

  // Exp e1,e2;
  public Type visit(LessThan n) {
    if(!isInteger(n.e1.accept(this))){
        System.out.println("Non-integer operand for operator <, at line 0, character 0");   
	}
    if(!isInteger(n.e1.accept(this))){
        System.out.println("Non-integer operand for operator <, at line 0, character 0");   
    }
	
	return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(Plus n) {
	if (!isInteger(n.e1.accept(this))){
        System.out.println("Non-integer operand for operator +, at line 0, character 0");   
    }
    if(!isInteger(n.e2.accept(this))){
        System.out.println("Non-integer operand for operator +, at line 0, character 0");   
    }
	
	return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Minus n) {
	if (!isInteger(n.e1.accept(this))){
		System.out.println("Non-integer operand for operator -, at line 0, character 0");	
	}
	if(!isInteger(n.e2.accept(this))){
		System.out.println("Non-integer operand for operator -, at line 0, character 0");   
	}
	return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Times n) {
	if (!isInteger(n.e1.accept(this))){
       System.out.println("Non-integer operand for operator *, at line 0, character 0");   
    }
		
    if(!isInteger(n.e2.accept(this))){
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
	if(!isIntArray(n.e.accept(this))){
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
	
	String returnType = cst.getMethod(methName).getReturnType();
	
	n.e.accept(this);
    n.i.accept(this);
	
    for ( int i = 0; i < n.el.size(); i++ ) {
        n.el.elementAt(i).accept(this);
    }
	
	if(returnType.equals("void"))
	{
		return new VoidType();
	}
	else
	{
		return strToType(returnType);
	}
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
	return new IdentifierType(n.s);
  }

  public Type visit(This n) {
	return new IdentifierType(currClass.getName());
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
	if(!isBoolean(n.e.accept(this))){
		System.out.println("Attempt to use boolean operator ! on non-boolean operand at line 0, character 0");
    }

    return new BooleanType();
  }

  // String s;
  public Type visit(Identifier n) {
	return strToType(n.s);	
  }
}

