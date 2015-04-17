//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//BuildSymbolTableVisitor.java

package visitor;

import syntaxtree.*;
import symboltable.*;

public class BuildSymbolTableVisitor implements Visitor
{
	private SymbolTable symbolTable;
	private Scope currentScope;
	private int blockNumber;
	public boolean errorDetected;
	
	public BuildSymbolTableVisitor()
	{
		symbolTable = new SymbolTable();
		currentScope = symbolTable;
		blockNumber = 0;
		errorDetected = false;
	}
	
	public Scope getFirstScope()
	{
		return symbolTable;
	}
	
	//Helper function to create unique numbers (as strings) for the blocks
	public String nextBlockNumber()
	{
		blockNumber++;
		return ("" + blockNumber);
	}
	
	//Helper function to turn Type into String containing the type:  i.e. "int", "boolean", etc.
	public String getTypeStr(Type t)
	{
		String type;
		
		if(t instanceof IntegerType)
		{
			type = "int";
		}
		else if(t instanceof IntArrayType)
		{
			type = "int[]";
		}
		else if(t instanceof BooleanType)
		{
			type = "boolean";
		}
		else
		{
			IdentifierType t1 = (IdentifierType)t;
			type = t1.s;
		}
		
		return type;
	}
	
	//Helper function to report Redefinition Errors
	private void redefError(String name, int line, int character)
	{
		System.err.println("Multiply defined identifier " + name + " at line " + line + ", character " + character);
		errorDetected = true;
	}
	
	// MainClass m;
	// ClassDeclList cl;
	public void visit(Program n) 
	{
		n.m.accept(this);
    
		for ( int i = 0; i < n.cl.size(); i++ ) 
		{
			n.cl.elementAt(i).accept(this);
		}
	}
	
	// Identifier i1,i2;
	// Statement s;
	public void visit(MainClass n) 
	{
		symbolTable.addClass(n.i1.toString());
		currentScope = symbolTable.enterScope(n.i1.toString());
		ClassSymbolTable cst = (ClassSymbolTable) currentScope;
		
		//Add System.out.println
		String[] paramNames = {"output"};
		String[] paramTypes = {"int"};
		
		cst.addMethod("System.out.println", paramNames, paramTypes, "void");
		
		paramTypes[0] = "String[]";
		paramNames[0] = n.i2.toString();
		
		cst.addMethod("main", paramNames, paramTypes, "void");
		
		currentScope = currentScope.enterScope("main");
		
		n.i1.accept(this);
		n.i2.accept(this);
		n.s.accept(this);
		
		currentScope = currentScope.exitScope();
		currentScope = currentScope.exitScope();
	}
  
	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclSimple n) 
	{
		symbolTable.addClass(n.i.toString());
		currentScope = symbolTable.enterScope(n.i.toString());
		
		//Add System.out.println
		String[] paramNames = {"output"};
		String[] paramTypes = {"int"};
		
		ClassSymbolTable cst = (ClassSymbolTable)currentScope;
		cst.addMethod("System.out.println", paramNames, paramTypes, "void");
		
		n.i.accept(this);
    
		for ( int i = 0; i < n.vl.size(); i++ ) 
		{
			n.vl.elementAt(i).accept(this);
		}
		
		for ( int i = 0; i < n.ml.size(); i++ ) 
		{
			n.ml.elementAt(i).accept(this);
		}
	
		currentScope = currentScope.exitScope();
	}
 
	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclExtends n) 
	{
		symbolTable.addClass(n.i.toString(), n.j.toString());
		currentScope = symbolTable.enterScope(n.i.toString());
		
		//Add System.out.println
		String[] paramNames = {"output"};
		String[] paramTypes = {"int"};
		
		ClassSymbolTable cst = (ClassSymbolTable) currentScope;
		cst.addMethod("System.out.println", paramNames, paramTypes, "void");
		
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
		
		currentScope = currentScope.exitScope();
	}

	// Type t;
	// Identifier i;
	public void visit(VarDecl n) 
	{
		String type = getTypeStr(n.t);
		
		BlockSymbolTable bst = (BlockSymbolTable) currentScope;
		
		if(bst.localVarLookup(n.i.toString()) != null)
		{
			redefError(n.i.toString(), n.i.lineNum, n.i.charNum);
		}
		
		bst.addVariable(n.i.toString(), type);
		
		n.t.accept(this);
		n.i.accept(this);
	}

	// Type t;
	// Identifier i;
	// FormalList fl;
	// VarDeclList vl;
	// StatementList sl;
	// Exp e;
	public void visit(MethodDecl n) 
	{
		ClassSymbolTable cst = (ClassSymbolTable) currentScope;
	
		String returnType = getTypeStr(n.t);
		String name = n.i.toString();
		String[] paramNames = new String[n.fl.size()+1];
		String[] paramTypes = new String[n.fl.size()+1];
		
		//Add "this" parameter of the class type to the first arg
		paramNames[0] = "this";
		paramTypes[0] = cst.getName();
		
		n.t.accept(this);
		n.i.accept(this);
    
		for ( int i = 0; i < n.fl.size(); i++ ) 
		{
			paramNames[n.fl.size()-i] = n.fl.elementAt(i).i.toString();
			paramTypes[n.fl.size()-i] = getTypeStr(n.fl.elementAt(i).t);
			n.fl.elementAt(i).accept(this);
		}
		
		cst.addMethod(name, paramNames, paramTypes, returnType);
		currentScope = cst.enterScope(name);
		
		for ( int i = 0; i < n.vl.size(); i++ ) 
		{
			n.vl.elementAt(i).accept(this);
		}
		
		for ( int i = 0; i < n.sl.size(); i++ ) 
		{
			n.sl.elementAt(i).accept(this);
		}
    
		n.e.accept(this);
		
		currentScope = currentScope.exitScope();
	}

	// Type t;
	// Identifier i;
	public void visit(Formal n) 
	{
		n.t.accept(this);
		n.i.accept(this);
	}

	public void visit(IntArrayType n) 
	{}

	public void visit(BooleanType n) 
	{}

	public void visit(IntegerType n) 
	{}

	// String s;
	public void visit(IdentifierType n)
	{}

	// StatementList sl;
	public void visit(Block n) 
	{
		BlockSymbolTable bst = (BlockSymbolTable) currentScope;
		String blockNum = nextBlockNumber();
		bst.addBlock(blockNum);
		currentScope = bst.enterScope(blockNum);
		
		for ( int i = 0; i < n.sl.size(); i++ ) 
		{
			n.sl.elementAt(i).accept(this);
		}
		
		currentScope = bst;
	}

	// Exp e;
	// Statement s1,s2;
	public void visit(If n) 
	{
		n.e.accept(this);
		n.s1.accept(this);
		n.s2.accept(this);
	}

	// Exp e;
	// Statement s;
	public void visit(While n) 
	{		
		n.e.accept(this);
		n.s.accept(this);
	}

	// Exp e;
	public void visit(Print n) 
	{
		n.e.accept(this);
	}
  
	// Identifier i;
	// Exp e;
	public void visit(Assign n) 
	{
		n.i.accept(this);
		n.e.accept(this);
	}

	// Identifier i;
	// Exp e1,e2;
	public void visit(ArrayAssign n) 
	{	
		n.i.accept(this);
		n.e1.accept(this);
		n.e2.accept(this);
	}

	// Exp e1,e2;
	public void visit(And n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
	}

	// Exp e1,e2;
	public void visit(LessThan n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
	}

	// Exp e1,e2;
	public void visit(Plus n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
	}

	// Exp e1,e2;
	public void visit(Minus n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
	}

	// Exp e1,e2;
	public void visit(Times n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
	}

	// Exp e1,e2;
	public void visit(ArrayLookup n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
	}

	// Exp e;
	public void visit(ArrayLength n) 
	{
		n.e.accept(this);
	}

	// Exp e;
	// Identifier i;
	// ExpList el;
	public void visit(Call n) 
	{
		n.e.accept(this);
		n.i.accept(this);
    
		for ( int i = 0; i < n.el.size(); i++ )
		{
			n.el.elementAt(i).accept(this);
		}
	}

	// int i;
	public void visit(IntegerLiteral n) 
	{}

	public void visit(True n) 
	{}

	public void visit(False n) 
	{}

	// String s;
	public void visit(IdentifierExp n) 
	{}

	public void visit(This n) 
	{}

	// Exp e;
	public void visit(NewArray n) 
	{
		n.e.accept(this);
	}

	// Identifier i;
	public void visit(NewObject n) 
	{}

	// Exp e;
	public void visit(Not n) 
	{
		n.e.accept(this);
	}

	// String s;
	public void visit(Identifier n) 
	{}
}
