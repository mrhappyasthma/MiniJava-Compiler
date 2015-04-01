//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//MiniJavaCompiler.java

import java_cup.runtime.*;
import java.io.*;
import syntaxtree.*;
import visitor.*;
import symboltable.*;

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
					//Visit the tree starting at root (program)
					BuildSymbolTableVisitor v = new BuildSymbolTableVisitor();
					v.visit(program);
					
					Scope s = v.getFirstScope();
					
					if(s == null)
						System.out.println("Oh no... the scope is null!");
					else
						s.print(0);
				}
			}
			else
			{
				System.err.println("Error: parse_tree.value == null");
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