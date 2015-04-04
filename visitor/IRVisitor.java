//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//IRVisitor.java

package visitor;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import syntaxtree.*;
import symboltable.*;
import IR.*;
import helper.*;

public class IRVisitor implements Visitor
{
	private Scope currentScope;
	private int blockNumber;
	private List<Quadruple> IRList; 
	private Hashtable<Quadruple, List<Label>> labels;
	
	public IRVisitor(Scope symbolTable)
	{
		labels = new Hashtable<Quadruple, List<Label>>();
		IRList = new ArrayList<Quadruple>();
		currentScope = symbolTable;
		blockNumber = 0;
	}
	
	public Hashtable<Quadruple, List<Label>> getLabels()
	{
		return labels;
	}
	
	public List<Quadruple> getIR()
	{
		return IRList;
	}

	//Helper function to add a new Label to a certain IR
	public void addLabel(Quadruple q, boolean printBefore)
	{
		List<Label> temp = labels.get(q);
		
		if(temp == null)
		{
			temp = new ArrayList<Label>();
		}

		temp.add(new Label(printBefore));
		labels.put(q, temp);
	}
	
	public void addLabel(Quadruple q, Label l)
	{
		List<Label> temp = labels.get(q);
		
		if(temp == null)
		{
			temp = new ArrayList<Label>();
		}
		
		temp.add(l);
		labels.put(q, temp);
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
		
		addLabel(IRList.get(0), true);

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
		
		int size = IRList.size();
		
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
		IRList.add(new ReturnIR(n.e.generateTAC()));
		
		addLabel(IRList.get(size), true);
		
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
		Label L1 = new Label(true);
		Label L2 = new Label(true);
		Label L3 = new Label(false);
		
		n.e.accept(this);
		
		IRList.add(new ConditionalJumpIR(n.e.generateTAC(), L2)); 
		addLabel(IRList.get(IRList.size()-1), L1);
		
		n.s1.accept(this);
		IRList.add(new UnconditionalJumpIR(L3));
		
		int size = IRList.size();
		
		n.s2.accept(this);
		
		addLabel(IRList.get(size), L2);
		addLabel(IRList.get(IRList.size()-1), L3);
	}

	// Exp e;
	// Statement s;
	public void visit(While n) 
	{		
		Label L1 = new Label(true);
		Label L2 = new Label(false);
		
		n.e.accept(this);
		
		IRList.add(new ConditionalJumpIR(n.e.generateTAC(), L2)); 
		addLabel(IRList.get(IRList.size()-1), L1);
	
		n.s.accept(this);

		IRList.add(new UnconditionalJumpIR(L1));
		
		addLabel(IRList.get(IRList.size()-1), L2);
	}

	// Exp e;
	public void visit(Print n) 
	{
		n.e.accept(this);
		IRList.add(new ParameterIR(n.e.generateTAC()));
		IRList.add(new CallIR("System.out.println", "1", null));
	}
  
	// Identifier i;
	// Exp e;
	public void visit(Assign n) 
	{
		n.i.accept(this);
		n.e.accept(this);
		IRList.add(new CopyIR(n.e.generateTAC(), currentScope.lookupVariable(n.i.toString())));
	}

	// Identifier i;
	// Exp e1,e2;
	public void visit(ArrayAssign n) 
	{	
		n.i.accept(this);
		n.e1.accept(this);
		n.e2.accept(this);
		IRList.add(new IndexedAssignmentIR2(n.e2.generateTAC(), n.e1.generateTAC(), currentScope.lookupVariable(n.i.toString())));
	}

	// Exp e1,e2;
	public void visit(And n) 
	{
		n.e1.accept(this);	
		n.e2.accept(this);
		IRList.add(new AssignmentIR("&&", n.e1.generateTAC(), n.e2.generateTAC(), n.generateTAC()));
	}

	// Exp e1,e2;
	public void visit(LessThan n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
		IRList.add(new AssignmentIR("<", n.e1.generateTAC(), n.e2.generateTAC(), n.generateTAC()));
	}

	// Exp e1,e2;
	public void visit(Plus n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
		IRList.add(new AssignmentIR("+", n.e1.generateTAC(), n.e2.generateTAC(), n.generateTAC()));
	}

	// Exp e1,e2;
	public void visit(Minus n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
		IRList.add(new AssignmentIR("-", n.e1.generateTAC(), n.e2.generateTAC(), n.generateTAC()));
	}

	// Exp e1,e2;
	public void visit(Times n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
		IRList.add(new AssignmentIR("*", n.e1.generateTAC(), n.e2.generateTAC(), n.generateTAC()));
	}

	// Exp e1,e2;
	public void visit(ArrayLookup n) 
	{
		n.e1.accept(this);
		n.e2.accept(this);
		IRList.add(new IndexedAssignmentIR1(n.e1.generateTAC(), n.e2.generateTAC(), n.generateTAC()));
	}

	// Exp e;
	public void visit(ArrayLength n) 
	{
		n.e.accept(this);
		IRList.add(new LengthIR(n.e.generateTAC(), n.generateTAC()));
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
		
		//Add the caller as the "this" parameter
		IRList.add(new ParameterIR(n.e.generateTAC()));
		
		for(int i = 0; i < n.el.size(); i++)
		{
			IRList.add(new ParameterIR(n.el.elementAt(i).generateTAC()));
		}
		
		IRList.add(new CallIR(n.i.toString(), Integer.toString(n.el.size() + 1), n.generateTAC()));
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
	{
			n.t = currentScope.lookupVariable(n.s);
	}

	public void visit(This n) 
	{}

	// Exp e;
	public void visit(NewArray n) 
	{
		n.e.accept(this);
		IRList.add(new NewArrayIR("int", n.e.generateTAC(), n.generateTAC()));
	}

	// Identifier i;
	public void visit(NewObject n) 
	{
		IRList.add(new NewIR(n.i.toString(), n.generateTAC()));
	}

	// Exp e;
	public void visit(Not n) 
	{
		n.e.accept(this);
		IRList.add(new UnaryAssignmentIR("!", n.e.generateTAC(), n.generateTAC()));
	}

	// String s;
	public void visit(Identifier n) 
	{}
}
