//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//CodeGenerator.java

package codegen;

import java.io.*;
import IR.*;
import symboltable.Variable;
import java.util.List;

public class CodeGenerator
{
	private String output;
	private List<Quadruple> IRList;
	
	public CodeGenerator(List<Quadruple> list, String fileName)
	{
		IRList = list;
		output = fileName;
	}
	
	public void generateMIPS()
	{
		try
		{
			FileWriter fw = new FileWriter(output);
			BufferedWriter bw = new BufferedWriter(fw);
			
			//Iterate through the IR instructions
			for(int i = 0; i < IRList.size(); i++)
			{
				Quadruple q = IRList.get(i);
				
				if(q instanceof CallIR)
				{
					functionCall(IRList, i, bw);
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
	
	private void functionCall(List<Quadruple>IRList, int index, BufferedWriter bw)
	{
		try
		{
			CallIR instruction = (CallIR)IRList.get(index);
			int paramCount = Integer.parseInt((String)instruction.getArg2());
			
			//Store $ra on stack
			String temp = "addi $sp, $sp, -4\n";
			bw.write(temp, 0, temp.length());
			temp = "sw $ra, ($sp)\n";
			bw.write(temp, 0, temp.length());
			
			//Store the (up to 4) params in register
			if(paramCount > 4)
			{
				System.err.println("Invalid number of parameters.  A max of 4 params per function in our MIPS output.");
				return;
			}
			
			int paramIndex = index - paramCount;
			
			for(int i = 0; i < paramCount; i++)
			{
				ParameterIR param = (ParameterIR)IRList.get(paramIndex);
				String reg = "$a" + i;
				
				//What if it isn't just a constant?
				temp = "addi " + reg + ", $zero, " + ((Variable)param.getArg1()).getName() + "\n"; 
				bw.write(temp, 0, temp.length());
			}
			
			//Jump to the function
			String function = (String)instruction.getArg1();
			
			if(function.equals("System.out.println"));
			{
				function = "_system_out_println";
			}
			
			temp = "jal " + function + "\n";
			bw.write(temp, 0, temp.length());
			
			//Restore $ra from stack
			temp = "lw $ra, ($sp)\n";
			bw.write(temp, 0, temp.length());
			temp = "addi $sp, $sp, 4\n";
			bw.write(temp, 0, temp.length());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}