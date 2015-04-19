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
				else if(q instanceof UnconditionalJumpIR)
				{
					handleUnconditionalJump(q, bw);
				}
				else if(q instanceof ConditionalJumpIR)
				{
					handleConditionalJump(q, bw);
				}
				else if(q instanceof NewArrayIR)
				{
					handleNewArray(q, bw);
				}
				else if(q instanceof LengthIR)
				{
					handleArrayLength(q, bw);
				}
				else if(q instanceof IndexedAssignmentIR1)
				{
					handleArrayIndex(q, bw);
				}
				else if(q instanceof IndexedAssignmentIR2)
				{
					handleArrayAssignment(q, bw);
				}
				else if(q instanceof NewIR)
				{
					handleObjectCreation(q, bw);
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
	
	private void handleObjectCreation(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			//Notes:
			//		$a0 - Holds the number of bytes
			//		$v0 - On return, holds the memory address (with address 0 holding the 4 byte length)
			
			String className = (String)instruction.getArg1();
			Variable result = (Variable)instruction.getResult();
			
			ClassSymbolTable cst = symbolTable.getClass(className);
			int classSize = cst.getSize(); //Get size of class in bytes
			
			//Store $ra on stack
			String temp = "addi $sp, $sp, -20\n";  //Make enough space on stack to save all reg
			bw.write(temp, 0, temp.length());
			temp = "sw $ra, 16($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store $a0
			temp = "sw $a0, 12($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store $t0-$t1 on the stack
			for(int i = 0; i < 2; i++)
			{
				temp = "sw $t" + i + ", " + (8 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store $v0 on the stack
			temp = "sw $v0, 0($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store size into $a0
			temp = "li $a0, " + classSize + "\n";
			bw.write(temp, 0, temp.length());
			
			//Call the function of "_new_array"
			temp = "jal _new_object\n";
			bw.write(temp, 0, temp.length());
			
			//Restore $t0-$t1 from the stack
			for(int i = 1; i >= 0; i--)
			{
				temp = "lw $t" + i + ", " + (8 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $a0 from the stack
			temp = "lw $a0, 12($sp)\n";
			bw.write(temp, 0, temp.length());
			
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
					temp = "lw $v0, " + result.getOffset() + "($a0)\n";
				}
			}
				
			bw.write(temp, 0, temp.length());
			
			//Restore $v0
			temp = "lw $v0, 0($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Restore $ra from the stack
			temp = "lw $ra, 16($sp)\n";
			bw.write(temp, 0, temp.length());
			temp = "addi $sp, $sp, 20\n";    //Cleanup space on stack from all saved reg
			bw.write(temp, 0, temp.length());
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleArrayIndex(Quadruple instruction, BufferedWriter bw)
	{
		//x := y[i]
		try
		{
			Variable arg1 = (Variable)instruction.getArg1();
			Variable arg2 = (Variable)instruction.getArg2();
			Variable result = (Variable)instruction.getResult();
			String temp = "";
			String arg1Reg = "";
			String arg2Reg = "";
			
			//Handle arg2 -- Get the index and multiply by 4 (shift by 2) -- then add 4 (for length offset)
			if(arg2.getType().equals("temporary"))
			{
				arg2Reg = allocator.allocateReg(arg2.getName());
				
				if(arg1.getType().equals("int[]"))
				{
					temp = "sll " + arg2Reg + ", " + arg2Reg + ", 2\n";
					bw.write(temp, 0, temp.length());
				}
				
				temp = "addi " + arg2Reg + ", " + arg2Reg + ", 4\n";
				bw.write(temp, 0, temp.length());
			}
			else if(arg2.getType().equals("constant"))
			{
				arg2Reg = "$zero"; //Handle this later as part of the ofset
			}
			else //Variable arg2
			{
				if(arg2.getOffset() == -1)
				{
					arg2Reg = arg2.getRegister();
					
					if(arg1.getType().equals("int[]"))
					{
						temp = "sll " + arg2Reg + ", " + arg2Reg + ", 2\n";
						bw.write(temp, 0, temp.length());
					}
					
					temp = "addi " + arg2Reg + ", " + arg2Reg + ", 4\n";
					bw.write(temp, 0, temp.length());
				}
				else //Class variable
				{
					arg2Reg = allocator.allocateTempReg(0);
					
					temp = "lw " + arg2Reg + ", " + arg2.getOffset() + "($a0)\n";
					
					if(arg1.getType().equals("int[]"))
					{
						temp = "sll " + arg2Reg + ", " + arg2Reg + ", 2\n";
						bw.write(temp, 0, temp.length());
					}
					
					temp = "addi " + arg2Reg + ", " + arg2Reg + ", 4\n";
					bw.write(temp, 0, temp.length());
				}
			}
			
			//Handle arg1 -- Address of array
			if(arg1.getType().equals("temporary"))
			{
				temp = "add " + allocator.allocateReg(arg1.getName()) + ", " + allocator.allocateReg(arg1.getName()) + ", " + arg2Reg + "\n";
				bw.write(temp, 0, temp.length());
				
				arg1Reg = allocator.allocateReg(arg1.getName());
			}
			else //Variable arg1
			{
				if(arg1.getOffset() == -1)
				{
					temp = "add " + arg1.getRegister() + ", " + arg1.getRegister() + ", " + arg2Reg + "\n";
					bw.write(temp, 0, temp.length());
					
					arg1Reg = arg1.getRegister();
				}
				else //Class Variable
				{
					arg1Reg = allocator.allocateTempReg(1);
					
					temp = "lw " + arg1Reg + ", " + arg1.getOffset() + "($a0)\n";
					bw.write(temp, 0, temp.length());
					
					temp = "add " + arg1Reg + ", " + arg1Reg + ", " + arg2Reg + "\n";
					bw.write(temp, 0, temp.length());
				}
			}
		
			//Load value at address from arg1 into result
			if(result.getType().equals("temporary"))
			{
				if(arg2.getType().equals("constant"))
				{
					//Calculate offset
					int offset = Integer.parseInt(arg2.getName());
					
					if(arg1.getType().equals("int[]"))
					{
						offset = offset * 4; //Multiply by 4 because it is an int[]
					}
					offset += 4; //Add 4 to offset the length
					temp =  "lw " + allocator.allocateReg(result.getName()) + ", " + offset + "(" + arg1Reg + ")\n";
				}
				else
				{
					temp =  "lw " + allocator.allocateReg(result.getName()) + ", 0(" + arg1Reg + ")\n";
				}
			}
			else //Variable result
			{
				if(result.getOffset() == -1)
				{
					if(arg2.getType().equals("constant"))
					{
						//Calculate offset
						int offset = Integer.parseInt(arg2.getName());
						
						if(arg1.getType().equals("int[]"))
						{
							offset = offset * 4; //Multiply by 4 because it is an int[]
						}
						offset += 4; //Add 4 to offset the length
						temp =  "lw " + result.getRegister() + ", " + offset + "(" + arg1Reg + ")\n";
					}
					else
					{
						temp =  "lw " + result.getRegister() + ", 0(" + arg1Reg + ")\n";
					}
				}
				else
				{
					if(arg2.getType().equals("constant"))
					{
						//Calculate offset
						int offset = Integer.parseInt(arg2.getName());
						
						if(arg1.getType().equals("int[]"))
						{
							offset = offset * 4; //Multiply by 4 because it is an int[]
						}
						offset += 4; //Add 4 to offset the length
						
						String tempReg = allocator.allocateTempReg(0);
						
						temp =  "lw " + tempReg + ", " + offset + "(" + arg1Reg + ")\n";
						bw.write(temp, 0, temp.length());
						
						temp = "sw " + tempReg + ", " + result.getOffset() + "($a0)\n";
					}
					else
					{
						String tempReg = allocator.allocateTempReg(0);
						
						temp =  "lw " + tempReg + ", 0(" + arg1Reg + ")\n";
						bw.write(temp, 0, temp.length());
						
						temp = "sw " + tempReg + ", " + result.getOffset() + "($a0)\n";
					}
				}
			}
			
			bw.write(temp, 0, temp.length());
			
			//Subtract arg2 from arg1 to return it to the original value
			if(arg1.getType().equals("temporary"))
			{
				temp = "sub " + arg1Reg + ", " + arg1Reg + ", " + arg2Reg + "\n";
				bw.write(temp, 0, temp.length());
			}
			else //Variable arg1
			{
				if(arg1.getOffset() == -1)
				{
					temp = "sub " + arg1Reg + ", " + arg1Reg + ", " + arg2Reg + "\n";
					bw.write(temp, 0, temp.length());
				}
			}
			
			//Return arg2 to normal - Subtract 4 (for length) and then right shift by 2 (divide by 4)
			if(arg2.getType().equals("temporary"))
			{	
				temp = "addi " + arg2Reg + ", " + arg2Reg + ", -4\n";
				bw.write(temp, 0, temp.length());
				
				if(arg1.getType().equals("int[]"))
				{
					temp = "srl " + arg2Reg + ", " + arg2Reg + ", 2\n";
					bw.write(temp, 0, temp.length());
				}
			}
			else if(!arg2.getType().equals("constant")) //Variable arg2
			{
				if(arg2.getOffset() == -1)
				{
					temp = "addi " + arg2Reg + ", " + arg2Reg + ", -4\n";
					bw.write(temp, 0, temp.length());
					
					if(arg1.getType().equals("int[]"))
					{
						temp = "srl " + arg2Reg + ", " + arg2Reg + ", 2\n";
						bw.write(temp, 0, temp.length());
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleArrayAssignment(Quadruple instruction, BufferedWriter bw)
	{
		//y[i] := x
		try
		{
			Variable arg1 = (Variable)instruction.getArg1();
			Variable arg2 = (Variable)instruction.getArg2();
			Variable result = (Variable)instruction.getResult();
			String temp = "";
			String resultReg = "";
			String arg2Reg = "";
			
			//Handle arg2 -- Get the index and multiply by 4 (shift by 2) -- then add 4 (for length offset)
			if(arg2.getType().equals("temporary"))
			{
				arg2Reg = allocator.allocateReg(arg2.getName());
				
				if(result.getType().equals("int[]"))
				{
					temp = "sll " + arg2Reg + ", " + arg2Reg + ", 2\n";
					bw.write(temp, 0, temp.length());
				}
				
				temp = "addi " + arg2Reg + ", " + arg2Reg + ", 4\n";
				bw.write(temp, 0, temp.length());
			}
			else if(arg2.getType().equals("constant"))
			{
				arg2Reg = "$zero"; //Handle this later as part of the ofset
			}
			else //Variable arg2
			{
				if(arg2.getOffset() == -1)
				{
					arg2Reg = arg2.getRegister();
					
					if(result.getType().equals("int[]"))
					{
						temp = "sll " + arg2Reg + ", " + arg2Reg + ", 2\n";
						bw.write(temp, 0, temp.length());
					}
					
					temp = "addi " + arg2Reg + ", " + arg2Reg + ", 4\n";
					bw.write(temp, 0, temp.length());
				}
				else //Class variable
				{
					arg2Reg = allocator.allocateTempReg(0);
					
					temp = "lw " + arg2Reg + ", " + arg2.getOffset() + "($a0)\n";
					bw.write(temp, 0, temp.length());
					
					if(result.getType().equals("int[]"))
					{
						temp = "sll " + arg2Reg + ", " + arg2Reg + ", 2\n";
						bw.write(temp, 0, temp.length());
					}
					
					temp = "addi " + arg2Reg + ", " + arg2Reg + ", 4\n";
					bw.write(temp, 0, temp.length());
				}
			}
			
			//Handle result -- Address of array
			if(result.getType().equals("temporary"))
			{
				temp = "add " + allocator.allocateReg(result.getName()) + ", " + allocator.allocateReg(result.getName()) + ", " + arg2Reg + "\n";
				bw.write(temp, 0, temp.length());
				
				resultReg = allocator.allocateReg(result.getName());
			}
			else //Variable result
			{
				if(result.getOffset() == -1)
				{
					temp = "add " + result.getRegister() + ", " + result.getRegister() + ", " + arg2Reg + "\n";
					bw.write(temp, 0, temp.length());
					
					resultReg = result.getRegister();
				}
				else //Class Variable
				{
					resultReg = allocator.allocateTempReg(1);
					
					temp = "lw " + resultReg + ", " + result.getOffset() + "($a0)\n";
					bw.write(temp, 0, temp.length());
					
					temp = "add " + resultReg + ", " + resultReg + ", " + arg2Reg + "\n";
					bw.write(temp, 0, temp.length());
				}
			}
		
			//Store value from arg1 result address
			if(arg1.getType().equals("temporary"))
			{
				if(arg2.getType().equals("constant"))
				{
					//Calculate offset
					int offset = Integer.parseInt(arg2.getName());
					
					if(result.getType().equals("int[]"))
					{
						offset = offset * 4; //Multiply by 4 because it is an int[]
					}
					offset += 4; //Add 4 to offset the length
					temp =  "sw " + allocator.allocateReg(arg1.getName()) + ", " + offset + "(" + resultReg + ")\n";
				}
				else
				{
					temp =  "sw " + allocator.allocateReg(arg1.getName()) + ", 0(" + resultReg + ")\n";
				}
			}
			else if(arg1.getType().equals("constant"))
			{
				String tempReg = allocator.allocateTempReg(0);
				
				if(arg2.getType().equals("constant"))
				{	
					//Calculate offset
					int offset = Integer.parseInt(arg2.getName());
					
					if(result.getType().equals("int[]"))
					{
						offset = offset * 4; //Multiply by 4 because it is an int[]
					}
					offset += 4; //Add 4 to offset the length
					
					temp = "li " + tempReg + ", " + arg1.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp =  "sw " + tempReg + ", " + offset + "(" + resultReg + ")\n";
				}
				else
				{
					temp = "li " + tempReg + ", " + arg1.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp =  "sw " + tempReg + ", 0(" + resultReg + ")\n";
				}
			}
			else //Variable arg1
			{
				if(arg1.getOffset() == -1)
				{
					if(arg2.getType().equals("constant"))
					{
						//Calculate offset
						int offset = Integer.parseInt(arg2.getName());
						
						if(result.getType().equals("int[]"))
						{
							offset = offset * 4; //Multiply by 4 because it is an int[]
						}
						offset += 4; //Add 4 to offset the length
						temp =  "sw " + arg1.getRegister() + ", " + offset + "(" + resultReg + ")\n";
					}
					else
					{
						temp =  "sw " + arg1.getRegister() + ", 0(" + resultReg + ")\n";
					}
				}
				else
				{
					String tempReg = allocator.allocateTempReg(0);
					
					if(arg2.getType().equals("constant"))
					{
						//Calculate offset
						int offset = Integer.parseInt(arg2.getName());
						
						if(result.getType().equals("int[]"))
						{
							offset = offset * 4; //Multiply by 4 because it is an int[]
						}
						offset += 4; //Add 4 to offset the length
						
						temp =  "lw " + tempReg + ", " + arg1.getOffset() + "($a0)\n";
						bw.write(temp, 0, temp.length());
						
						temp =  "sw " + tempReg + ", " + offset + "(" + resultReg + ")\n";
					}
					else
					{
						temp =  "lw " + tempReg + ", " + arg1.getOffset() + "($a0)\n";
						bw.write(temp, 0, temp.length());
						
						temp =  "sw " + tempReg + ", 0(" + resultReg + ")\n";
					}
				}
			}
			
			bw.write(temp, 0, temp.length());
			
			//Subtract arg2 from result to return it to the original value
			if(result.getType().equals("temporary"))
			{
				temp = "sub " + resultReg + ", " + resultReg + ", " + arg2Reg + "\n";
				bw.write(temp, 0, temp.length());
			}
			else //Variable arg1
			{
				if(result.getOffset() == -1)
				{
					temp = "sub " + resultReg + ", " + resultReg + ", " + arg2Reg + "\n";
					bw.write(temp, 0, temp.length());
				}
			}
			
			//Return arg2 to normal - Subtract 4 (for length) and then right shift by 2 (divide by 4)
			if(arg2.getType().equals("temporary"))
			{	
				temp = "addi " + arg2Reg + ", " + arg2Reg + ", -4\n";
				bw.write(temp, 0, temp.length());
				
				if(result.getType().equals("int[]"))
				{
					temp = "srl " + arg2Reg + ", " + arg2Reg + ", 2\n";
					bw.write(temp, 0, temp.length());
				}
			}
			else if(!arg2.getType().equals("constant")) //Variable arg2
			{
				if(arg2.getOffset() == -1)
				{
					temp = "addi " + arg2Reg + ", " + arg2Reg + ", -4\n";
					bw.write(temp, 0, temp.length());
					
					if(result.getType().equals("int[]"))
					{
						temp = "srl " + arg2Reg + ", " + arg2Reg + ", 2\n";
						bw.write(temp, 0, temp.length());
					}
				}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleArrayLength(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			Variable arg1 = (Variable)instruction.getArg1(); //Holds the address
			Variable result = (Variable)instruction.getResult();
			String temp;
			
			if(result.getType().equals("temporary"))
			{
				if(arg1.getType().equals("temporary"))
				{
					temp = "lw " + allocator.allocateReg(result.getName()) + ", 0(" + allocator.allocateReg(arg1.getName()) + ")\n";
				}
				else //Variable
				{
					if(arg1.getOffset() == -1)
					{
						temp = "lw " + allocator.allocateReg(result.getName()) + ", 0(" + arg1.getRegister() + ")\n";
					}
					else //Class variable
					{
						temp = "lw " + allocator.allocateReg(result.getName()) + ", " + arg1.getOffset() + "($a0)\n";
						bw.write(temp, 0, temp.length());
						
						temp = "lw " + allocator.allocateReg(result.getName()) + ", 0(" + allocator.allocateReg(result.getName()) + ")\n";
					}
				}
			}
			else //Variable result
			{
				if(result.getOffset() == -1)
				{
					if(arg1.getType().equals("temporary"))
					{
						temp = "lw " + result.getRegister() + ", 0(" + allocator.allocateReg(arg1.getName()) + ")\n";
					}
					else //Variable
					{
						if(arg1.getOffset() == -1)
						{
							temp = "lw " + result.getRegister() + ", 0(" + arg1.getRegister() + ")\n";
						}
						else //Class variable
						{
							temp = "lw " + result.getRegister() + ", " + arg1.getOffset() + "($a0)\n";
							bw.write(temp, 0, temp.length());
						
							temp = "lw " + result.getRegister() + ", 0(" + result.getRegister() + ")\n";
						}
					}
				}
				else //Class variable
				{
					String resultReg = allocator.allocateTempReg(0);
					String tempReg = allocator.allocateTempReg(1);
					
					if(arg1.getType().equals("temporary"))
					{
						temp = "lw " + resultReg + ", 0(" + allocator.allocateReg(arg1.getName()) + ")\n";
						bw.write(temp, 0, temp.length());
						
						temp = "sw " + resultReg + ", " + result.getOffset() + "($a0)\n";
					}
					else //Variable
					{
						if(arg1.getOffset() == -1)
						{
							temp = "lw " + resultReg + ", 0(" + arg1.getRegister() + ")\n";
							bw.write(temp, 0, temp.length());
						
							temp = "sw " + resultReg + ", " + result.getOffset() + "($a0)\n";
						}
						else //Class variable
						{
							temp = "lw " + resultReg + ", " + arg1.getOffset() + "($a0)\n";
							bw.write(temp, 0, temp.length());
							
							temp = "lw " + resultReg + ", 0(" + resultReg + ")\n";
							bw.write(temp, 0, temp.length());
						
							temp = "sw " + resultReg + ", " + result.getOffset() + "($a0)\n";
						}
					}
				}
			}
			
			bw.write(temp, 0, temp.length());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleNewArray(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			//Notes:
			//		$a0 - Holds the number of bytes
			//		$v0 - On return, holds the memory address (with address 0 holding the 4 byte length)
			
			String type = (String)instruction.getArg1();
			Variable arg2 = (Variable)instruction.getArg2();
			Variable result = (Variable)instruction.getResult();
			String temp;
			
			//Store $ra on stack
			temp = "addi $sp, $sp, -20\n";  //Make enough space on stack to save all reg
			bw.write(temp, 0, temp.length());
			temp = "sw $ra, 16($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store $a0
			temp = "sw $a0, 12($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store $t0-$t1 on the stack
			for(int i = 0; i < 2; i++)
			{
				temp = "sw $t" + i + ", " + (8 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Store $v0 on the stack
			temp = "sw $v0, 0($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Handle arg2 -- The number of elements/size
			if(arg2.getType().equals("constant"))
			{
				temp = "li $a0, " + arg2.getName() + "\n";
			}
			else if(arg2.getType().equals("temporary"))
			{
				temp = "move $a0, " + allocator.allocateReg(arg2.getName()) + "\n";
			}
			else //Variable arg1
			{
				if(arg2.getOffset() == -1)
				{
					temp = "move $a0, " + arg2.getRegister() + "\n";
				}
				else //Class variable
				{
					temp = "lw $a0, " + arg2.getOffset() + "($a0)\n";
				}
			}
				
			bw.write(temp, 0, temp.length());
			
			if(type.equals("int")) //Only "int" for MiniJava
			{
				//Shift left by 2 (multiply by 4 bytes) for each "int" in size
				temp = "sll $a0, $a0, 2\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Call the function of "_new_array"
			temp = "jal _new_array\n";
			bw.write(temp, 0, temp.length());
			
			//Restore $t0-$t1 from the stack
			for(int i = 1; i >= 0; i--)
			{
				temp = "lw $t" + i + ", " + (8 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
			//Restore $a0 from the stack
			temp = "lw $a0, 12($sp)\n";
			bw.write(temp, 0, temp.length());
			
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
					temp = "sw $v0, " + result.getOffset() + "($a0)\n";
				}
			}
				
			bw.write(temp, 0, temp.length());
			
			//Restore $v0
			temp = "lw $v0, 0($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Restore $ra from the stack
			temp = "lw $ra, 16($sp)\n";
			bw.write(temp, 0, temp.length());
			temp = "addi $sp, $sp, 20\n";    //Cleanup space on stack from all saved reg
			bw.write(temp, 0, temp.length());
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleConditionalJump(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			String label = ((Label)instruction.getResult()).getName();
			Variable arg1 = (Variable)instruction.getArg1();
			String temp;
			
			//Handle arg1 -- Store the first parameter in the result register
			if(arg1.getType().equals("constant"))
			{
				String tempReg = allocator.allocateTempReg(0);
				temp = "beq " + tempReg + ", $zero, " + label + "\n";
			}
			else if(arg1.getType().equals("temporary"))
			{
				temp = "beq " + allocator.allocateReg(arg1.getName()) + ", $zero, " + label + "\n";
			}
			else //Variable arg1
			{
				if(arg1.getOffset() == -1)
				{
					temp = "beq " + arg1.getRegister() + ", $zero, " + label + "\n";
				}
				else //Class variable
				{
					//Todo
					temp = "";
				}
			}
			
			bw.write(temp, 0, temp.length());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private void handleUnconditionalJump(Quadruple instruction, BufferedWriter bw)
	{
		try
		{
			String label = ((Label)instruction.getResult()).getName();
			
			String temp = "j " + label + "\n";
			
			bw.write(temp, 0, temp.length());
		}
		catch(IOException e)
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
					temp = "lw " + resultReg + ", " + arg1.getOffset() + "($a0)\n";
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
					temp = "sw " + resultReg + ", " + result.getOffset() + "($a0)\n";
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
					temp = "lw $v0, " + arg1.getOffset() + "($a0)\n";
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
			int numTempRegs = 0;
				
			if(result.getType().equals("temporary"))
			{
				resultReg = allocator.allocateReg(result.getName());
			}
			else //Variable result
			{
				resultReg = allocator.allocateTempReg(numTempRegs);
				numTempRegs++;
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
					String tempReg = allocator.allocateTempReg(numTempRegs);
					numTempRegs++;

					temp = "li " + tempReg + ", " + arg2.getName() + "\n";
					bw.write(temp, 0, temp.length());
					
					temp = "mult " + resultReg + ", " + tempReg + "\n";
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
				
				if(arg2.getOffset() == -1)
				{
					varReg = arg2.getRegister();
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
			String temp = "addi $sp, $sp, -100\n";  //Make enough space on stack to save all reg
			bw.write(temp, 0, temp.length());
			temp = "sw $ra, 96($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store $s0-$s7 on stack
			for(int i = 0; i < 8; i++)
			{
				temp = "sw $s" + i + ", " + (92 - (4*i)) + "($sp)\n";
				bw.write(temp, 0, temp.length());
			}
			
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
						//Since are overwriting $aX registers, we need to get their old value off the stack if needed
						if(arg1.getName().equals("this"))
						{
							temp = "lw " + reg + ", 60($sp)\n";
						}
						else if(arg1.getRegister().equals("$a0"))
						{
							temp = "lw " + reg + ", 60($sp)\n";
						}
						else if(arg1.getRegister().equals("$a1"))
						{
							temp = "lw " + reg + ", 56($sp)\n";
						}
						else if(arg1.getRegister().equals("$a2"))
						{
							temp = "lw " + reg + ", 52($sp)\n";
						}
						else if(arg1.getRegister().equals("$a3"))
						{
							temp = "lw " + reg + ", 48($sp)\n";
						}
						else
						{
							temp = "move " + reg + ", " + arg1.getRegister() + "\n";
						}
					}
					else //Class variable
					{
						temp = "lw " + reg + ", 60($sp)\n";
						bw.write(temp, 0, temp.length());
						
						temp = "lw " + reg + ", " + arg1.getOffset() + "(" + reg + ")\n";
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
			
			//Restore $s0-$s7 on the stack
			for(int i = 7; i >= 0; i--)
			{
				temp = "lw $s" + i + ", " + (92 - (4*i)) + "($sp)\n";
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
						temp = "lw $v0, " + result.getOffset() + "0($a0)\n";
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
			temp = "lw $ra, 96($sp)\n";
			bw.write(temp, 0, temp.length());
			temp = "addi $sp, $sp, 100\n";    //Cleanup space on stack from all saved reg
			bw.write(temp, 0, temp.length());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
