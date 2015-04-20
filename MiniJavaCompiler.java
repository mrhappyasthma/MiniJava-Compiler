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
import helper.*;
import linker.*;
import codegen.*;
import backpatching.*;
import regalloc.*;
import java.util.Set;
import java.util.List;
import java.util.Hashtable;
import java.util.HashMap;
import regalloc.flowgraph.*;
import regalloc.graph.*;
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
					if(bstVisitor.errorDetected || undefinedVisitor.errorDetected || typeCheckVisitor.errorDetected) 
					{
						return;
					}
					
					//Generate IR
					IRVisitor intermediateVisitor = new IRVisitor(symbolTable);
					intermediateVisitor.visit(program);
					
					List<Quadruple> IRList = intermediateVisitor.getIR();
					Hashtable<Quadruple, List<Label>> labels = intermediateVisitor.getLabels();
					HashMap<String, String> workList = intermediateVisitor.getWorkList();
					
					
						
					 //The above register allocator is not finished, so this is the temporary versio$
                                        RegisterAllocator allocator = new RegisterAllocator(); 										

					
					
					SymbolTable symTable = (SymbolTable)symbolTable;
					Hashtable <String, ClassSymbolTable> classes = symTable.getClasses();
					List<String> keys = Helper.keysToSortedList(classes.keySet());;
					
					for(int i = 0; i < keys.size(); i++) 	//Iterate over each class
					{
						ClassSymbolTable classSymTable = classes.get(keys.get(i));
						classSymTable.calculateVarOffsets(); //Store variable offsets in the symbol table
						
						Hashtable<String, MethodSymbolTable> methods = classSymTable.getMethods();
						List<String> methodKeys = Helper.keysToSortedList(methods.keySet());
						
						for(int j = 0; j < methodKeys.size(); j++)
						{
							MethodSymbolTable methSymTable = methods.get(methodKeys.get(j));
							methSymTable.assignRegisters(allocator); //Temporary allocation to all method locals
						}
					}
					
					//Backpatch the IR to resolve labels in jumps to methods
					BackPatcher backPatch = new BackPatcher(IRList, workList);
					backPatch.patch();
					
					
					 //Allocate Registers
                                        AssemFlowGraph asmFG = new AssemFlowGraph(IRList,labels);
                                        List<List<Node>> func = asmFG.buildCFG();
					for (int i = 0; i < func.size(); i++) {
                                            Liveness liv = new Liveness(func.get(i));
                                           // liv.calculateLive();
            
                                        }		
			


					//Create output file
					String fileName = args[0].substring(0, args[0].lastIndexOf(".")) + ".asm";
						
					//Write MIPS
					CodeGenerator gen = new CodeGenerator(IRList, labels, allocator, symTable, fileName);
					gen.generateMIPS();


					//Link runtime.asm file
					Linker linker = new Linker("linker/runtime.asm", fileName);
					linker.link();
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
