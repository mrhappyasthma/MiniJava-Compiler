package visitor;

import syntaxtree.*;
import symboltable.*;

public class TypeCheckingVisitor implements TypeVisitor 
{
	private Scope currentScope;
	private ClassSymbolTable currClass;
	private MethodSymbolTable currMethod;
    private SymbolTable symTable;
    public boolean errorDetected;
	private int blockNumber;
	
	public TypeCheckingVisitor(Scope s)
	{
		currentScope = s; 
		errorDetected = false;
        symTable = (SymbolTable)currentScope;
		blockNumber=0;
	}
	
	//create numbers (as strings) for the blocks
	public String nextBlockNumber()
	{
		blockNumber++;
		return ("" + blockNumber);
	}
	
	//Helper function to compare types
	public boolean compareTypes(Type t1, Type t2)
	{
		if(t1 instanceof IntegerType && t2 instanceof IntegerType)
		{
			return true;
		}	
		else if(t1 instanceof BooleanType && t2 instanceof BooleanType)
		{
            return true;
        }
		else if(t1 instanceof IntArrayType && t2 instanceof IntArrayType)
		{
            return true;
        }
		else if(t1 instanceof IdentifierType || t2 instanceof IdentifierType)  //If we have an identifer, there is more work to do
		{
			//If we have both identifiers, we need to find the type of each
			if(t1 instanceof IdentifierType && t2 instanceof IdentifierType)
			{
				//If we have a variable name, get the type (as a str) otherwise use the type name directly
				Variable v = currentScope.lookupVariable(((IdentifierType)t1).s);
				Variable v2 = currentScope.lookupVariable(((IdentifierType)t2).s);
				String s1;
				String s2;
				
				if(v == null)
				{
					s1 = ((IdentifierType)t1).s;
				}
				else
				{
					s1 = v.getType();
				}
				
				if(v2 == null)
				{
					s2 = ((IdentifierType)t2).s;
				}
				else
				{
					s2 = v2.getType();
				}
			
				if(s1.equals(s2))
				{
					return true;
				}
				else
				{
					//Check for cases of inheritance and compare parent classes
					if(symTable.isClass(s1))
					{
						if(((ClassSymbolTable)symTable.enterScope(s1)).getParentClass() != null)
						{
							s1 = ((ClassSymbolTable)symTable.enterScope(s1)).getParentClass();
						}
					}
					
					if(symTable.isClass(s2))
					{
						if(((ClassSymbolTable)symTable.enterScope(s2)).getParentClass() != null)
						{
							s2 = ((ClassSymbolTable)symTable.enterScope(s2)).getParentClass();
						}
					}
						
					if(s1.equals(s2))
					{
						return true;
					}
					else
					{
						return false;
					}
				}
			}
			else if(t1 instanceof IdentifierType)  //If we only have one identifier, look up the class
			{
				Variable v = currentScope.lookupVariable(((IdentifierType)t1).s);
				if(v == null)
				{
					return true; //Assume undeclared variable is of right type
				}
				else 
				{	
					Type t = strToType(((IdentifierType)t1).s);
					return compareTypes(t, t2);
				}
			}
			else  //If we only have one identifier, look up the class
			{
				Variable v = currentScope.lookupVariable(((IdentifierType)t2).s);
				if(v == null)
				{
					return true; //Assume undeclared variable is of right type
				}
				else
				{
					Type t = strToType(((IdentifierType)t2).s);
					return compareTypes(t1, t);
				}
			}
        }
		
		return false;
	}

	//Helper function to turn Strings (i.e. "int", "boolean", etc.) into Type
	public Type strToType(String str)
	{
		if(str.equals("int"))
		{
			return new IntegerType();
		}
		else if(str.equals("int[]"))
		{
			return new IntArrayType();
		}
		else if(str.equals("boolean"))
		{
			return new BooleanType();
		}
		else
		{
			Variable v = currentScope.lookupVariable(str);
			if(v == null)
			{
				return new IdentifierType(str);
			}
			else
			{
				return strToType(v.getType());
			}
		}
	}

	//Helper function to check if we have a valid integer types
	public boolean isInteger(Type t)
	{
		if(t instanceof IntegerType)
		{
			return true;
		}
		
		if(t instanceof IdentifierType)
		{
			if(strToType(((IdentifierType)t).s) instanceof IntegerType)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
	//Helper function to check if we have a valid boolean types
	public boolean isBoolean(Type t)
	{
		if(t instanceof BooleanType)
		{
			return true;
		}
		
		if(t instanceof IdentifierType)
		{
			if(strToType(((IdentifierType)t).s) instanceof BooleanType)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
	//Helper function to check if we have a valid int array types
	public boolean isIntArray(Type t)
	{
		if(t instanceof IntArrayType)
		{
			return true;
		}
		
		if(t instanceof IdentifierType)
		{
			if(strToType(((IdentifierType)t).s) instanceof IntArrayType)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		
		return false;
	}
	
  // MainClass m;
  // ClassDeclList cl;
  public Type visit(Program n) 
  {
	n.m.accept(this);
	for ( int i = 0; i < n.cl.size(); i++ ) 
	{
		n.cl.elementAt(i).accept(this);
	}
  	return null;
  }
  
  // Identifier i1,i2;
  // Statement s;
  public Type visit(MainClass n) 
  {
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
  public Type visit(ClassDeclSimple n) 
  {
	currentScope = currentScope.enterScope(n.i.toString()); //Enter class
	currClass = (ClassSymbolTable) currentScope;	
        n.i.accept(this);
    	for ( int i = 0; i < n.vl.size(); i++ ) 
		{
       		 n.vl.elementAt(i).accept(this);
    	}
    	for ( int i = 0; i < n.ml.size(); i++ ) 
		{
        	n.ml.elementAt(i).accept(this);
    	}
			
   	currentScope = currentScope.exitScope(); //Exit class
  	return null;	
  }
 
  // Identifier i;
  // Identifier j;
  // VarDeclList vl;
  // MethodDeclList ml;
  public Type visit(ClassDeclExtends n) 
  {
    	currentScope = currentScope.enterScope(n.i.toString()); //Enter class
		currClass = (ClassSymbolTable) currentScope;	
    	n.i.accept(this);
    	n.j.accept(this);
    	for ( int i = 0; i < n.vl.size(); i++ ) 
		{
       		n.vl.elementAt(i).accept(this);
    	}
    	for ( int i = 0; i < n.ml.size(); i++ ) 
		{
			n.ml.elementAt(i).accept(this);
    	}		
    	currentScope =currentScope.exitScope(); //Exit class
   	return null; 
   }

  // Type t;
  // Identifier i;
  public Type visit(VarDecl n) 
  {
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
  public Type visit(MethodDecl n) 
  {
	currentScope = currentScope.enterScope(n.i.toString()); //Enter method
	currMethod = (MethodSymbolTable)currentScope;	
    	n.t.accept(this);
    	n.i.accept(this);
    	for ( int i = 0; i < n.fl.size(); i++ ) 
		{
       		 n.fl.elementAt(i).accept(this);
    	}
		
    	for ( int i = 0; i < n.vl.size(); i++ ) 
		{
        	n.vl.elementAt(i).accept(this);
    	}
		
    	for ( int i = 0; i < n.sl.size(); i++ ) 
		{
        	n.sl.elementAt(i).accept(this);
    	}
    	n.e.accept(this);
		
    	currentScope = currentScope.exitScope(); //Exit method
  	return null;   
  }

  // Type t;
  // Identifier i;
  public Type visit(Formal n) 
  {
    n.t.accept(this);
    n.i.accept(this);
    return null;
  }

  public Type visit(IntArrayType n) 
  {
  	return null;
  }

  public Type visit(BooleanType n) 
  {
  	return null;
  }

  public Type visit(IntegerType n) 
  {
  	return null;
  }

  // String s;
  public Type visit(IdentifierType n) 
  {
  	return null;
  }

  // StatementList sl;
  public Type visit(Block n) 
  {
		String blockNum = nextBlockNumber();
		currentScope = currentScope.enterScope(blockNum); //Enter block
		
		for ( int i = 0; i < n.sl.size(); i++ ) 
		{
			n.sl.elementAt(i).accept(this);
		}
		
		currentScope = currentScope.exitScope(); //Exit block
  		return null;  
  }

  // Exp e;
  // Statement s1,s2;
  public Type visit(If n) 
  {
    if(!isBoolean(n.e.accept(this)))
	{
		System.out.println("Non-boolean expression used as the condition of if statement at line " + n.lineNum + ", character " + n.charNum);
		errorDetected = true;
	}
	
    n.s1.accept(this);
    n.s2.accept(this);
	
  	return null;
  }

  // Exp e;
  // Statement s;
  public Type visit(While n) 
  {
	if(!isBoolean(n.e.accept(this)))
	{
        errorDetected=true;
		System.out.println("Non-boolean expression used as the condition of while statement at line " + n.lineNum + ", character " + n.charNum);
    }
    	
	n.s.accept(this);
	
	return null;
  }

  // Exp e;
  public Type visit(Print n) 
  {
    n.e.accept(this);
  	return null;
  }
  
  // Identifier i;
  // Exp e;
  public Type visit(Assign n) 
  {
    n.i.accept(this);
	boolean internalError = false;
	
	//Check for invalid l-values
	if(n.i.s.equals("this"))
	{
		internalError = true;
		errorDetected=true;
		System.out.println("Invalid l-value: "+n.i.s+" is a this, at line "+ n.i.lineNum+", character "+ n.i.charNum);
	}
	else if(symTable.isClass(n.i.s))
	{
		internalError = true;
		errorDetected=true;
		System.out.println("Invalid l-value: "+n.i.s+" is a class, at line "+ n.i.lineNum+", character "+ n.i.charNum);
	}
	else
	{
		if(currClass.isMethod(n.i.s))
		{
			internalError = true;
			errorDetected=true;
			System.out.println("Invalid l-value: "+n.i.s+" is a method, at line "+ n.i.lineNum+", character "+ n.i.charNum);
		}		
	}
	
	//Check for invalid r-values
	Type t = n.e.accept(this);
	
	if(t instanceof IdentifierType)
	{
		IdentifierType id = (IdentifierType) t;
		
		//if(symTable.isClass(id.s))
		//{
		//	internalError = true;
		//	errorDetected = true;
		//	System.out.println("Invalid r-value: " + id.s + " is a class, at line " + n.lineNum + ", character " + n.charNum);
		//}
		//else
		//{
			if(currClass.isMethod(id.s))
			{
				internalError = true;
				errorDetected = true;
				System.out.println("Invalid r-value: " + id.s + " is a method, at line " + n.lineNum + ", character " + n.charNum);
			}
		//}
	}
	
	//Check for type mismatch
	if(!compareTypes(strToType(n.i.s), t) && !internalError)
	{
		errorDetected = true;
		System.out.println("Type mismatch during assignment at line " + n.lineNum + ", character " + n.charNum);
	}
	
	return null;
 }
 
  // Identifier i;
  // Exp e1,e2;
  public Type visit(ArrayAssign n) 
  {
    n.i.accept(this);
    n.e1.accept(this);
    Type t = n.e2.accept(this);
	boolean internalError = false;
	
	//Check for invalid l-values
	if(n.i.s.equals("this"))
	{
		internalError = true;
		errorDetected=true;
		System.out.println("Invalid l-value: "+n.i.s+" is a this, at line "+ n.i.lineNum+", character "+ n.i.charNum);
	}
	else if(symTable.isClass(n.i.s))
	{
		internalError = true;
		errorDetected=true;
		System.out.println("Invalid l-value: "+n.i.s+" is a class, at line "+ n.i.lineNum+", character "+ n.i.charNum);
	}
	else
	{
		if(currClass.isMethod(n.i.s))
		{
			internalError = true;
			errorDetected=true;
			System.out.println("Invalid l-value: "+n.i.s+" is a method, at line "+ n.i.lineNum+", character "+ n.i.charNum);
		}		
	}
	
	//Check for invalid r-values
	if(t instanceof IdentifierType)
	{
		IdentifierType id = (IdentifierType) t;
		
		if(symTable.isClass(id.s))
		{
			internalError = true;
			errorDetected = true;
			System.out.println("Invalid r-value: " + id.s + " is a class, at line " + n.lineNum + ", character " + n.charNum);
		}
		else
		{
			if(currClass.isMethod(id.s))
			{
				internalError = true;
				errorDetected = true;
				System.out.println("Invalid r-value: " + id.s + " is a method, at line " + n.lineNum + ", character " + n.charNum);
			}
		}
	}
	
	//Check for type mismatch
	if(!compareTypes(new IntegerType(), t) && !internalError)
	{
		errorDetected = true;
		System.out.println("Type mismatch during assignment at line " + n.lineNum + ", character " + n.charNum);
	}
	
	return null;
  }

  // Exp e1,e2;
  public Type visit(And n) 
  {
	if(!isBoolean(n.e1.accept(this)) || !isBoolean(n.e2.accept(this)))
	{
		errorDetected = true;
        System.out.println("Attempt to use boolean operator && on non-boolean operands at line " + n.lineNum + ", character " + n.charNum);   
	}

    return new BooleanType();
   }

  // Exp e1,e2;
  public Type visit(LessThan n) 
  {
    Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);
	
	boolean invalidOperands = false;

    if(t1 instanceof IdentifierType)
	{
        IdentifierType id = (IdentifierType) t1;

        if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
			invalidOperands = true;
            errorDetected = true;
            System.out.println("Invalid operands for < operator, at line "+n.lineNum+", character"+n.charNum);
        }
    }

    if(t2 instanceof IdentifierType)
	{
        IdentifierType id = (IdentifierType) t2;

        if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
            invalidOperands = true;
			errorDetected = true;
            System.out.println("Invalid operands for < operator, at line "+n.lineNum+", character"+n.charNum);
        }
    }

	if(!invalidOperands)
	{
    	if(!isInteger(n.e1.accept(this)) || !isInteger(n.e2.accept(this)))
		{
			errorDetected = true;
        	System.out.println("Non-integer operand for operator <, at line " + n.lineNum + ", character " + n.charNum);   
		}
	}
	return new BooleanType();
  }

  // Exp e1,e2;
  public Type visit(Plus n) 
  {
	Type t1 = n.e1.accept(this); 
	Type t2 = n.e2.accept(this);
	
	boolean invalidOperands = false;	

	if(t1 instanceof IdentifierType)
	{
		IdentifierType id = (IdentifierType) t1;
		
		if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
			invalidOperands = true;
			errorDetected = true;
			System.out.println("Invalid operands for + operator, at line "+n.lineNum+", character"+n.charNum);
		}
	}	

	if(t2 instanceof IdentifierType)
	{
        IdentifierType id = (IdentifierType) t2;

        if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
			invalidOperands = true;
            errorDetected = true;
            System.out.println("Invalid operands for + operator, at line "+n.lineNum+", character"+n.charNum);
        }
    }
	
	if(!invalidOperands)
	{
		if (!isInteger(n.e1.accept(this)) || !isInteger(n.e2.accept(this)))
		{
			errorDetected = true;
       		System.out.println("Non-integer operand for operator +, at line " + n.lineNum + ", character " + n.charNum);   
   		}
	}

	return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Minus n) 
  {
    Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);

	boolean invalidOperands = false;

    if(t1 instanceof IdentifierType)
	{
        IdentifierType id = (IdentifierType) t1;

        if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
            invalidOperands = true;
			errorDetected = true;
            System.out.println("Invalid operands for - operator, at line "+n.lineNum+", character"+n.charNum);
        }
    }

    if(t2 instanceof IdentifierType)
	{
        IdentifierType id = (IdentifierType) t2;

        if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
			invalidOperands = true;
            errorDetected = true;
            System.out.println("Invalid operands for - operator, at line "+n.lineNum+", character"+n.charNum);
        }
    }


	if(!invalidOperands)
	{
		if (!isInteger(n.e1.accept(this)) || !isInteger(n.e2.accept(this)))
		{
			errorDetected = true;
			System.out.println("Non-integer operand for operator -, at line " + n.lineNum + ", character " + n.charNum);	
		}
	}

	return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(Times n) 
  {
    Type t1 = n.e1.accept(this);
    Type t2 = n.e2.accept(this);

	boolean invalidOperands = false;

    if(t1 instanceof IdentifierType)
	{
        IdentifierType id = (IdentifierType) t1;

        if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
			invalidOperands = true;
            errorDetected = true;
            System.out.println("Invalid operands for * operator, at line "+n.lineNum+", character"+n.charNum);
        }
    }

    if(t2 instanceof IdentifierType)
	{
        IdentifierType id = (IdentifierType) t2;

        if(symTable.isClass(id.s) || currClass.isMethod(id.s))
		{
			invalidOperands = true;
            errorDetected = true;
            System.out.println("Invalid operands for * operator, at line "+n.lineNum+", character"+n.charNum);
        }
    }
	
	if(!invalidOperands)
	{
		if (!isInteger(n.e1.accept(this)) || !isInteger(n.e2.accept(this)))
		{
			errorDetected = true;
        	System.out.println("Non-integer operand for operator *, at line " + n.lineNum + ", character " + n.charNum);       	
   		}
	}
		
	return new IntegerType();
  }

  // Exp e1,e2;
  public Type visit(ArrayLookup n) 
  {
	n.e1.accept(this);
    n.e2.accept(this);
	
    return new IntegerType();
  }

  // Exp e;
  public Type visit(ArrayLength n) 
  {
	if(!isIntArray(n.e.accept(this)))
	{
		errorDetected = true;
		System.out.println("Length property only applies to arrays line " + n.lineNum + ", character " + n.charNum);
	}
	
	return new IntegerType();  
  }

  // Exp e;
  // Identifier i;
  // ExpList el;
  public Type visit(Call n) 
  {
    String methName = n.i.s;
	String varName = ((IdentifierType) n.e.accept(this)).s;
	Variable callee = currentScope.lookupVariable(varName);
	String className;
	
	n.i.accept(this);
	
	//Resolve identifier vs type name problem
	if(callee == null)
	{
		className = varName;
	}
	else
	{
		className = callee.getType();
	}

	ClassSymbolTable cst = symTable.getClass(className);
	
	if(!(cst.isMethod(methName)))
	{
		errorDetected = true;
		System.out.println("Attempt to call a non-method at line"+n.i.lineNum+", character "+n.i.charNum);
	
		return new VoidType();
	}
	
	String returnType = cst.getMethod(methName).getReturnType();
	
	boolean numParamError = false;
	
	if(!(cst.getMethod(methName).numParameters() == (n.el.size()+1)))
	{
		numParamError = true;
		errorDetected = true;
		System.out.println("Call of method " + methName + " does not match its declared number of arguments at line " + n.lineNum + ", character " + n.charNum);
	}
	
	Variable[] params = cst.getMethod(methName).getParameters();
	
    for ( int i = 0; i < n.el.size(); i++ ) 
	{
        Type t = n.el.elementAt(i).accept(this);
		
		if(!numParamError)
		{
			Variable v = params[i+1];

			if(!compareTypes(t, strToType(v.getType())))
			{
				errorDetected = true;
				System.out.println("Call of method " + methName + " does not match its declared signature at line " + n.lineNum + ", character " + n.charNum);
				break;
			}
		}
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
  public Type visit(IntegerLiteral n) 
  {
	return new IntegerType();
  }

  public Type visit(True n) 
  {
	return new BooleanType();
  }

  public Type visit(False n) 
  {
	return new BooleanType();
  }

  // String s;
  public Type visit(IdentifierExp n) 
  {
	return new IdentifierType(n.s);
  }

  public Type visit(This n) 
  {
	if(currMethod.getName().equals("main"))
	{
		errorDetected = true;
		System.out.println("Illegal use of keyword 'this' in static method at line " + n.lineNum + ", character " + n.charNum);
		return new VoidType();
	}
	else
	{
		return new IdentifierType(currClass.getName());
	}
  }

  // Exp e;
  public Type visit(NewArray n) 
  {
    n.e.accept(this);
	return new IntArrayType();
  }

  // Identifier i;
  public Type visit(NewObject n) 
  {
	return new IdentifierType(n.i.s);
  }

  // Exp e;
  public Type visit(Not n) 
  {
	if(!isBoolean(n.e.accept(this)))
	{
		errorDetected = true;
		System.out.println("Attempt to use boolean operator ! on non-boolean operand at line " + n.lineNum + ", character " + n.charNum);
    }

    return new BooleanType();
  }

  // String s;
  public Type visit(Identifier n) 
  {
	return strToType(n.s);	
  }
}

