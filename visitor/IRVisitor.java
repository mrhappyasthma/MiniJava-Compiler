//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//IRVisitor.java

package visitor;

import java.util.List;
import java.util.ArrayList;
import syntaxtree.*;
import symboltable.*;
import IR.*;

public class IRVisitor implements Visitor
{
	private Scope currentScope;
	private int blockNumber;
	private List<Quadruple> IRlist; 
	
	public IRVisitor(Scope symbolTable)
	{
		IRlist = new ArrayList<Quadruple>();
		currentScope = symbolTable;
		blockNumber = 0;
	}

	//Helper function to create unique numbers (as strings) for the blocks
	public String nextBlockNumber()
	{
		blockNumber++;
		return ("" + blockNumber);
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
		currentScope = currentScope.enterScope(n.i1.toString()); //Enter class
    	currentScope = currentScope.enterScope("main");    
		
		n.i1.accept(this);
    	n.i2.accept(this);
    	n.s.accept(this);

		currentScope = currentScope.exitScope(); //Exit "main" method
		currentScope = currentScope.exitScope(); //Exit class
	}
  
	// Identifier i;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclSimple n) 
	{
		currentScope = currentScope.enterScope(n.i.toString()); //Enter class
		
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
	}
 
	// Identifier i;
	// Identifier j;
	// VarDeclList vl;
	// MethodDeclList ml;
	public void visit(ClassDeclExtends n) 
	{
    	currentScope = currentScope.enterScope(n.i.toString()); //Enter class
		
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
		
    	currentScope = currentScope.exitScope(); //Exit class
	}

	// Type t;
	// Identifier i;
	public void visit(VarDecl n) 
	{
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
		currentScope = currentScope.enterScope(n.i.toString()); //Enter method
		
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
		String blockNum = nextBlockNumber();
		currentScope = currentScope.enterScope(blockNum); //Enter block
		
		for ( int i = 0; i < n.sl.size(); i++ ) 
		{
			n.sl.elementAt(i).accept(this);
		}
		
		 currentScope = currentScope.exitScope(); //Exit block
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
