//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//MiniJavaCompiler.java

import java_cup.runtime.*;
import java.io.*;
import syntaxtree.*;
import visitor.*;
import symboltable.*;
import IR.*;
import java.util.List;

public class MiniJavaCompiler
{
	public static void main(String[] args)
	{
		if(args.length != 1)
		{
			System.out.println("Usage: java MiniJavaCompiler file.java");
			System.exit(1);
		}

		Symbol parse_tree = null;
		
		try
		{
			Reader reader = new java.io.InputStreamReader(new java.io.FileInputStream(args[0]), "UTF-8");
			Parser parser = new Parser(new Lexer(reader));
			parse_tree = parser.parse();
			
			Program program = ((Program) parse_tree.value);

			if(program != null)
			{
				if(parser.errorDetected == false)
				{
					//Visit the tree starting at root (program) to build the symbol table
					BuildSymbolTableVisitor bstVisitor = new BuildSymbolTableVisitor();
					bstVisitor.visit(program);
					
					//Get a copy of the symboltable
					Scope symbolTable = bstVisitor.getFirstScope();
					
					if(symbolTable == null) //Check for error making symbol table
					{
						System.err.println("Oh no... the symbol table is null!");
						return;
					}

					//Visit the tree starting at the root to check for undefined variables
					UndefinedVariableVisitor undefinedVisitor = new UndefinedVariableVisitor(symbolTable); 
					undefinedVisitor.visit(program);

					//Visit the tree starting at the root to check for type errors
					TypeCheckingVisitor typeCheckVisitor = new TypeCheckingVisitor(symbolTable);
					typeCheckVisitor.visit(program);
					
					//If any errors occured, do not generate IR
					if(bstVisitor.errorDetected | undefinedVisitor.errorDetected | typeCheckVisitor.errorDetected) 
					{
						return;
					}
					
					//Generate IR
					IRVisitor intermediateVisitor = new IRVisitor(symbolTable);
					intermediateVisitor.visit(program);
					
					List<Quadruple> IRList = intermediateVisitor.getIR();
					
					//Print IR
					for(Quadruple q : IRList)
						System.out.println(q.toString());
				}
			}
		}
		catch (java.io.IOException e)
		{
			System.err.println("Unable to open file: " + args[0]);
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
		}
	}
}
