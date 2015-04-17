//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//CodeGenerator.java

package codegen;

import java.io.*;
import IR.*;
import helper.Label;
import symboltable.*;
import java.util.List;
import java.util.Hashtable;
import regalloc.RegisterAllocator;

public class CodeGenerator
{
	private String output;
	private List<Quadruple> IRList;
	private Hashtable<Quadruple, List<Label>> labels;
	private RegisterAllocator allocator;
	private SymbolTable symbolTable;
	
	public CodeGenerator(List<Quadruple> list, Hashtable<Quadruple, List<Label>> label, RegisterAllocator alloc, Scope symTable, String fileName)
	{
		IRList = list;
		output = fileName;
		labels = label;
		allocator = alloc;
		symbolTable = (SymbolTable)symTable;
	}
	
	//Helper function to get size of a class (in bytes)
	public int getClassSize(String name)
	{
		ClassSymbolTable cst = symbolTable.getClass(name);
		
		if(cst == null)
		{
			System.err.println("Error - Invalid Class Lookup in CodeGenerator!");
			return -1;
		}
		
		return cst.getSize();
	}
	
	public void generateMIPS()
	{
		boolean printedExit = false; //Used to track the closing of main
		
		try
		{
			FileWriter fw = new FileWriter(output);
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write(".text\n", 0, 6);
			bw.write("main:\n", 0, 6);
			
			//Iterate through the IR instructions
			for(int i = 0; i < IRList.size(); i++)
			{
				Quadruple q = IRList.get(i);
				
				List<Label> labelList = labels.get(q);
				
				//Print any labels before this instruction
				if(labelList != null)
				{
					for(Label l : labelList)
					{
						if(l.printBefore == true)
						{
							//Print label
							String temp = l.toString() + "\n";
							bw.write(temp, 0, temp.length());
						}
					}
				}
				
				if(q instanceof AssignmentIR)
				{
					handleAssignment(q, bw);
				}
				else if(q instanceof CallIR)
				{
					functionCall(IRList, i, bw);
				}
				else if(q instanceof ReturnIR)
				{
					handleReturn(q, bw);
				}
				else if(q instanceof CopyIR)
				{
					handleCopy(q, bw);
				}
				else if(q instanceof UnaryAssignmentIR)
				{
					handleUnaryAssignment(q, bw);
				}
				
				//Print any labels after
				if(labelList != null)
				{
					for(Label l : labelList)
					{
						if(l.printBefore == false)
						{	
							//Print label
							String temp = l.toString() + "\n";
							bw.write(temp, 0, temp.length());
						}
					}
				}
			}
			
			//Close output file resources
			if(bw != null)
				bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleCopy(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			Variable result = (Variable)instruction.getResult();
			Variable arg1 = (Variable)instruction.getArg1();
			String temp = "";
			String resultReg;
				
			if(result.getType().equals("temporary"))
			{
				resultReg = allocator.allocateReg(result.getName());
			}
			else //Variable result
			{
				resultReg = allocator.allocateTempReg(0);
			}
					
			//Handle arg1 -- Store the first parameter in the result register
			if(arg1.getType().equals("constant"))
			{
				temp = "li " + resultReg + ", " + arg1.getName() + "\n";
			}
			else if(arg1.getType().equals("temporary"))
			{
				temp = "move " + resultReg + ", " + allocator.allocateReg(arg1.getName()) + "\n";
			}
			else //Variable arg1
			{
				if(arg1.getOffset() == -1)
				{
					temp = "move " + resultReg + ", " + arg1.getRegister() + "\n";
				}
				else //Class variable
				{
					//Todo
				}
			}
					
			bw.write(temp, 0, temp.length());
			
			if(!result.getType().equals("temporary")) //Variable result
			{
				if(result.getOffset() == -1)
				{
					temp = "move " + result.getRegister() + ", " + resultReg + "\n";
				}
				else //Class variable
				{
					//Todo
				}
				bw.write(temp, 0, temp.length());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleUnaryAssignment(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			String op = (String)instruction.getOp();
			Variable result = (Variable)instruction.getResult();
			Variable arg1 = (Variable)instruction.getArg1();
			String resultReg;
			String temp;
			
			if(result.getType().equals("temporary"))
			{
				resultReg = allocator.allocateReg(result.getName());
			}
			else //Variable
			{
				resultReg = allocator.allocateTempReg(0);
			}
			
			//Handle arg1 -- Store the first parameter in the result register
			if(arg1.getType().equals("constant"))
			{
				temp = "li " + resultReg + ", " + arg1.getName() + "\n";
			}
			else if(arg1.getType().equals("temporary"))
			{
				temp = "move " + resultReg + ", " + allocator.allocateReg(arg1.getName()) + "\n";
			}
			else //Variable arg1
			{
				if(arg1.getOffset() == -1)
				{
					temp = "move " + resultReg + ", " + arg1.getRegister() + "\n";
				}
				else //Class variable
				{
					//Todo
					temp = "";
				}
			}
					
			bw.write(temp, 0, temp.length());
			
			if(op.equals("!"))
			{
				Label L1 = new Label(false);
				Label L2 = new Label(false);
				
				//Check if we have 0 (false), if so, jump to new label L1 and store "1" in the resultReg
				temp = "beq " + resultReg + ", $zero, " + L1.getName() + "\n";
				bw.write(temp, 0, temp.length());
				
				//Otherwise, fallthrough and store "-" in result reg
				temp = "add " + resultReg + ", $zero, $zero\n";
				bw.write(temp, 0, temp.length());
				
				temp = "j " + L2.getName() + "\n";
				bw.write(temp, 0, temp.length());
				
				temp = L1.toString() + "\n";
				bw.write(temp, 0, temp.length());
				
				temp = "addi " + resultReg + ", $zero, 1\n";
				bw.write(temp, 0, temp.length());
				
				temp = L2.toString() + "\n";
				bw.write(temp, 0, temp.length());
			}
			
			bw.write(temp, 0, temp.length());
			
			if(!result.getType().equals("temporary")) //Variable result
			{
				if(result.getOffset() == -1)
				{
					temp = "sw " + result.getRegister() + ", " + resultReg + "\n";
				}
				else //Class variable
				{
					//Todo
				}
				bw.write(temp, 0, temp.length());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleReturn(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			Variable arg1 = (Variable)instruction.getArg1();
			String temp = "";
			
			if(arg1.getType().equals("constant"))
			{
				temp = "li $v0, " + arg1.getName() + "\n";
			}
			else if(arg1.getType().equals("temporary"))
			{
				temp = "move $v0, " + allocator.allocateReg(arg1.getName()) + "\n";
			}
			else //Variable
			{
				if(arg1.getOffset() == -1)
				{
					temp = "move $v0, " + arg1.getRegister() + "\n";
				}
				else //Class variable
				{
					//Todo
				}
			}
			
			bw.write(temp, 0, temp.length());
			
			temp = "jr $ra\n";
			bw.write(temp, 0, temp.length());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleAssignment(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			String op = (String)instruction.getOp();
			Variable result = (Variable)instruction.getResult();
			Variable arg1 = (Variable)instruction.getArg1();
			Variable arg2 = (Variable)instruction.getArg2();
			String temp = "";
			String resultReg;
				
			if(result.getType().equals("temporary"))
			{
				resultReg = allocator.allocateReg(result.getName());
			}
			else //Variable result
			{
				resultReg = allocator.allocateTempReg(0);
			}
					
			//Handle arg1 -- Store the first parameter in the result register
			if(arg1.getType().equals("constant"))
			{
				temp = "li " + resultReg + ", " + arg1.getName() + "\n";
			}
			else if(arg1.getType().equals("temporary"))
			{
				temp = "move " + resultReg + ", " + allocator.allocateReg(arg1.getName()) + "\n";
			}
			else //Variable arg1
			{
				if(arg1.getOffset() == -1)
				{
					temp = "move " + resultReg + ", " + arg1.getRegister() + "\n";
				}
				else //Class variable
				{
					//Todo
				}
			}
					
			bw.write(temp, 0, temp.length());
			
			//Handle arg2 -- Add it to the second parameter in the result register
			if(arg2.getType().equals("constant"))
			{
				if(op.equals("+"))
				{
					temp = "addi " + resultReg + ", " + resultReg + ", " + arg2.getName() + "\n";
				}
				else if(op.equals("-"))
				{
					temp = "addi " + resultReg + ", " + resultReg + (Integer.parseInt(arg2.getName())*-1) + "\n";
				}
				else if(op.equals("*"))
				{
					temp = "li " + resultReg + ", " + arg2.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "mult " + resultReg + ", " + resultReg + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "mflo " + resultReg + "\n";
				}
				else if(op.equals("<"))
				{
					Label L1 = new Label(false);
					Label L2 = new Label(false);
					
					//If arg 1 < arg2, branch to L1 and store "1" inside resultReg
					temp = "blt " + resultReg + ", " + arg2.getName() + ", " + L1.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					//Else fallthrough and store "0" inside resultReg
					temp = "add " + resultReg + ", $zero, $zero\n";
					bw.write(temp, 0, temp.length());
					
					temp = "j " + L2.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = L1.toString() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "addi " + resultReg + ", $zero, 1\n";
					bw.write(temp, 0, temp.length());
					
					temp = L2.toString() + "\n";
				}
				else if(op.equals("&&"))
				{
					temp = "andi " + resultReg + ", " + resultReg + ", " + arg2.getName() + "\n";
				}
			}
			else if(arg2.getType().equals("temporary"))
			{
				if(op.equals("+"))
				{	
					temp = "add " + resultReg + ", " + resultReg + ", " + allocator.allocateReg(arg2.getName()) + "\n";
				}
				else if(op.equals("-"))
				{
					temp = "sub " + resultReg + ", " + resultReg + ", " + allocator.allocateReg(arg2.getName()) + "\n";
				}
				else if(op.equals("*"))
				{
					temp = "mult " + resultReg + ", " + allocator.allocateReg(arg2.getName()) + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "mflo " + resultReg + "\n";
				}
				else if(op.equals("<"))
				{
					Label L1 = new Label(false);
					Label L2 = new Label(false);
					
					//If arg 1 < arg2, branch to L1 and store "1" inside resultReg
					temp = "blt " + resultReg + ", " + allocator.allocateReg(arg2.getName()) + ", " + L1.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					//Else fallthrough and store "0" inside resultReg
					temp = "add " + resultReg + ", $zero, $zero\n";
					bw.write(temp, 0, temp.length());
					
					temp = "j " + L2.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = L1.toString() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "addi " + resultReg + ", $zero, 1\n";
					bw.write(temp, 0, temp.length());
					
					temp = L2.toString() + "\n";
				}
				else if(op.equals("&&"))
				{
					temp = "and " + resultReg + ", " + resultReg + ", " + allocator.allocateReg(arg2.getName()) + "\n";
				}
			}
			else //Variable arg2
			{
				String varReg = "";
				
				if(arg1.getOffset() == -1)
				{
					varReg = arg1.getRegister();
				}
				else //Class variable
				{
					//Todo
				}
				
				if(op.equals("+"))
				{
					temp = "add " + resultReg + ", " + resultReg + ", " + varReg + "\n";
				}
				else if(op.equals("-"))
				{
					temp = "sub " + resultReg + ", " + resultReg + ", " + varReg + "\n";
				}
				else if(op.equals("*"))
				{
					temp = "mult " + resultReg + ", " + varReg + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "mflo " + resultReg + "\n";
				}
				else if(op.equals("<"))
				{
					Label L1 = new Label(false);
					Label L2 = new Label(false);
					
					//If arg 1 < arg2, branch to L1 and store "1" inside resultReg
					temp = "blt " + resultReg + ", " + varReg + ", " + L1.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					//Else fallthrough and store "0" inside resultReg
					temp = "add " + resultReg + ", $zero, $zero\n";
					bw.write(temp, 0, temp.length());
					
					temp = "j " + L2.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = L1.toString() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "addi " + resultReg + ", $zero, 1\n";
					bw.write(temp, 0, temp.length());
					
					temp = L2.toString() + "\n";
				}
				else if(op.equals("&&"))
				{
					temp = "and " + resultReg + ", " + resultReg + ", " + varReg + "\n";
				}
			}
					
			bw.write(temp, 0, temp.length());
				
			if(!result.getType().equals("temporary")) //Variable result
			{
				if(result.getOffset() == -1)
				{
					temp = "move " + result.getRegister() + ", " + resultReg + "\n";
				}
				else //Class variable
				{
					//Todo
				}
				
				bw.write(temp, 0, temp.length());
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void functionCall(List<Quadruple>IRList, int index, BufferedWriter bw)
	{
		try
		{
			CallIR instruction = (CallIR)IRList.get(index);
			int paramCount = Integer.parseInt((String)instruction.getArg2());
			String function = (String)instruction.getArg1();
			
			//Handle System.exit (no need to worry about params nor registers)
			if(function.equals("_system_exit"))
			{
				String temp = "jal " + function + "\n";
				bw.write(temp, 0, temp.length());
				return;
			}
			
			//Store $ra on stack
			String temp = "addi $sp, $sp, -68\n";  //Make enough space on stack to save all reg
			bw.write(temp, 0, temp.length());
			temp = "sw $ra, 64($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store $a0 - $a3 on stack
			for(int i = 0; i < 4; i++)
			{
				temp = "sw $a" + i + ", " + (60 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store $t0-$t9 on the stack
			for(int i = 0; i < 10; i++)
			{
				temp = "sw $t" + i + ", " + (44 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store $v0-$v1 on the stack
			for(int i = 0; i < 2; i++)
			{
				temp = "sw $v" + i + ", " + (4 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store the (up to 4) params in register
			if(paramCount > 4)
			{
				System.err.println("Invalid number of parameters.  A max of 4 params per function in our MIPS output.");
				return;
			}
			
			int paramIndex = index - paramCount;
			
			for(int i = 0; i < paramCount; i++)
			{	
				ParameterIR param = (ParameterIR)IRList.get(paramIndex+i);
				String reg = "$a" + i;
				
				Variable arg1 = (Variable)param.getArg1();
				
				if(arg1.getType().equals("constant")) 
                {              
                    temp = "li " + reg + ", " + arg1.getName() + "\n"; 
				}
                else if(arg1.getType().equals("temporary")) 
                {              
                    temp = "move " + reg + ", " + allocator.allocateReg(arg1.getName()) + "\n"; 
                }
				else //Variable
                {              
                    if(arg1.getOffset() == -1)
					{
						temp = "move " + reg + ", " + arg1.getRegister() + "\n";
					}
					else //Class variable
					{
						//Todo
					}
                }

				bw.write(temp, 0, temp.length());
			}
			
			//Jump to the function
			temp = "jal " + function + "\n";
			bw.write(temp, 0, temp.length());
			
			//Restore $t0-$t9 from the stack
			for(int i = 9; i >= 0; i--)
			{
				temp = "lw $t" + i + ", " + (44 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $a0-$a3 on the stack
			for(int i = 3; i >= 0; i--)
			{
				temp = "lw $a" + i + ", " + (60 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Move return value into the result register
			if(!function.equals("_system_out_println"))  //println is the only 'void' function in minijava
			{
				Variable result = (Variable)instruction.getResult();
				
				if(result.getType().equals("temporary"))
				{
					temp = "move " + allocator.allocateReg(result.getName()) + ", $v0\n";
				}
				else //Variable
				{
					if(result.getOffset() == -1)
					{
						temp = "move " + result.getRegister() + ", $v0\n";
					}
					else //Class variable
					{
						//Todo
					}
				}
				
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $v0-$v1 from the stack
			for(int i = 1; i >= 0; i--)
			{
				temp = "lw $v" + i + ", " + (4 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $ra from the stack
			temp = "lw $ra, 64($sp)\n";
			bw.write(temp, 0, temp.length());
			temp = "addi $sp, $sp, 68\n";    //Cleanup space on stack from all saved reg
			bw.write(temp, 0, temp.length());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
