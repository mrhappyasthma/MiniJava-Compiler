//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//CodeGenerator.java

package codegen;

import java.io.*;
import IR.*;
import helper.Label;
import symboltable.Variable;
import java.util.List;
import java.util.Hashtable;
import regalloc.RegisterAllocator;

public class CodeGenerator
{
	private String output;
	private List<Quadruple> IRList;
	private Hashtable<Quadruple, List<Label>> labels;
	private List<Variable> varList;
	private RegisterAllocator allocator;
	
	public CodeGenerator(List<Quadruple> list, Hashtable<Quadruple, List<Label>> label, List<Variable> vars, String fileName)
	{
		IRList = list;
		output = fileName;
		labels = label;
		varList = vars;
		allocator = new RegisterAllocator();
	}
	
	public void generateMIPS()
	{
		boolean printedExit = false; //Used to track the closing of main
		
		try
		{
			FileWriter fw = new FileWriter(output);
			BufferedWriter bw = new BufferedWriter(fw);
			
			//Allocated Variables
			bw.write(".data\n");
			generateDataSeg(varList, bw);
			
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
							//Print system exit at the end of main
							if(l.toString().equals("L1:"))
							{
								String temp = "jal _system_exit\n";
								bw.write(temp, 0, temp.length());
								printedExit = true;
							}
							else //Print jr return jump before the label
							{
								if(!l.toString().equals("L0:"))
								{
									String temp = "jr $ra\n";
									bw.write(temp, 0, temp.length());
								}
							}
					
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
				
				//Print any labels after
				if(labelList != null)
				{
					for(Label l : labelList)
					{
						if(l.printBefore == false)
						{	
							//Print system exit at the end of main
							if(l.toString().equals("L1:"))
							{
								String temp = "jal _system_exit\n";
								bw.write(temp, 0, temp.length());
								printedExit = true;
							}
							else //Print jr return jump before the label
							{
								if(!l.toString().equals("L0:"))
								{
									String temp = "jr $ra\n";
									bw.write(temp, 0, temp.length());
								}
							}
					
							//Print label
							String temp = l.toString() + "\n";
							bw.write(temp, 0, temp.length());
						}
					}
				}
			}
			
			//Print the closing exit/return
			if(printedExit == true)
			{
				String temp = "jr $ra\n";
				bw.write(temp, 0, temp.length());
			}
			else
			{
				String temp = "jal _system_exit\n";
				bw.write(temp, 0, temp.length());
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
	
	private void generateDataSeg(List<Variable> varList, BufferedWriter bw)
	{
		for(int i = 0; i < varList.size(); i++)
		{
			try
			{
				String name = varList.get(i).getName();
				String type = varList.get(i).getType();
				String value = "";
				
				if(type.equals("int") || type.equals("boolean"))
				{
					type = ".word";
					value = "0"; //Default value of 0d or false
				}
				else //Don't handle objects or int[] yet
				{
					return;
				}
				
				String data = name + ": " + type + " " + value + "\n";
				bw.write(data, 0, data.length());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
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
				temp = "lw $v0, " + arg1.getName() + "\n";
			}
			
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
			
			if(op.equals("+"))
			{
				if(result.getType().equals("temporary"))
				{
					String resultReg = allocator.allocateReg(result.getName());
					
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
						temp = "lw " + resultReg + ", " + arg1.getName() + "\n";
					}
					
					bw.write(temp, 0, temp.length());
					
					//Handle arg2 -- Add it to the second parameter in the result register
					if(arg2.getType().equals("constant"))
					{
						temp = "addi " + resultReg + ", " + resultReg + ", " + arg2.getName() + "\n";
					}
					else if(arg2.getType().equals("temporary"))
					{
						temp = "add " + resultReg + ", " + resultReg + ", " + allocator.allocateReg(arg2.getName()) + "\n";
					}
					else //Variable arg2
					{
						String tempReg = allocator.allocateTempReg(0);
						temp = "lw " + tempReg + ", " + arg2.getName() + "\n";
						bw.write(temp, 0, temp.length());
						
						temp = "add " + resultReg + ", " + resultReg + ", " + tempReg + "\n";
					}
					
					bw.write(temp, 0, temp.length());
				}
				else //Variable result
				{
					String resultReg = allocator.allocateTempReg(0);
					String tempReg = allocator.allocateTempReg(1);
					String resultName = (String)result.getName();
					
					//Handle arg1 -- Store the first parameter in the temporary register
					if(arg1.getType().equals("constant"))
					{
						temp = "li " + tempReg + ", " + arg1.getName() + "\n";
					}
					else if(arg1.getType().equals("temporary"))
					{
						temp = "move " + tempReg + ", " + allocator.allocateReg(arg1.getName()) + "\n";
					}
					else //Variable arg1
					{
						temp = "lw " + tempReg + ", " + arg1.getName() + "\n";
					}
					
					bw.write(temp, 0, temp.length());
					
					//Handle arg2 -- Add it to the second parameter in the temp register
					if(arg2.getType().equals("constant"))
					{
						temp = "addi " + tempReg + ", " + tempReg + ", " + arg2.getName() + "\n";
					}
					else if(arg2.getType().equals("temporary"))
					{
						temp = "add " + tempReg + ", " + tempReg + ", " + allocator.allocateReg(arg2.getName()) + "\n";
					}
					else //Variable arg2
					{
						temp = "lw " + resultReg + ", " + arg2.getName() + "\n"; //Use "resultReg" as a temporary register here
						bw.write(temp, 0, temp.length());
						
						temp = "add " + tempReg + ", " + tempReg + ", " + resultReg;
					}
					
					bw.write(temp, 0, temp.length());
					
					//Load address of variable
					temp = "la " + resultReg + ", " + resultName + "\n";
					bw.write(temp, 0, temp.length());
					
					//Store result from tempReg into the variable address
					temp = "sw " + tempReg + ", 0(" + resultReg + ")\n";
					bw.write(temp, 0, temp.length());
				}
			}
			else if(op.equals("-")) //Subtraction
			{
				//To be completed...
			}
			else if(op.equals("*")) //Multiplication
			{
				//To be completed...
			}
			else if(op.equals("<")) //Less Than
			{
				//To be completed...
			}
			else //And
			{
				//To be completed...
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
				
				String argName = ((Variable)param.getArg1()).getName();
				String argType = ((Variable)param.getArg1()).getType();
				
				if(argType.equals("constant")) 
                {              
                    temp = "li " + reg + ", " + argName + "\n"; 
				}
                else if(argType.equals("temporary")) 
                {              
                    temp = "move " + reg + ", " + allocator.allocateReg(argName) + "\n"; 
                }
				else //Variable
                {              
                    temp = "lw " + reg + ", " + argName + "\n"; 
                }

				bw.write(temp, 0, temp.length());
			}
			
			//Jump to the function
			String function = (String)instruction.getArg1();

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
					temp = "sw $v0, " + result.getName() + "\n";
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
