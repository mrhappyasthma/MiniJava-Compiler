//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//BackPatcher.java

package backpatching;

import java.util.List;
import java.util.HashMap;
import IR.*;

public class BackPatcher
{
	private List<Quadruple> IRList;
	private HashMap<String, String> workList;

	public BackPatcher(List<Quadruple> instructions, HashMap<String, String> work)
	{
		IRList = instructions;
		workList = work;
	}
	
	public void patch()
	{
		for(Quadruple q : IRList)
		{
			if(q instanceof CallIR)
			{
				String methodName = (String)q.getArg1();
			
				if(methodName.equals("System.out.println"))
				{
					q.setArg1("_system_out_println");
				}
				else if(methodName.equals("System.exit"))
				{
					q.setArg1("_system_exit");
				}
				else
				{
					q.setArg1(workList.get(methodName));
				}
			}
		}
	}
}