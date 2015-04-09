package regalloc;

public class RegisterAllocator
{
	int[] temporaries;
	int qtdTemp;

	public RegisterAllocator()
	{
		temporaries = new int[10];
		qtdTemp = 0;
	}
	
	public String allocateTemporaries(int num)
	{
		int i;
		if(qtdTemp==10){
			//System.out.println("");
			//System.exit(0);
			return null;
		}
		//verify if the value already exists
		for(i=0; i<10; i++)
		{
			if(temporaries[i]==num)
			{
					return("$t"+i);
			}
		}
		temporaries[qtdTemp]=num;
		int reg = qtdTemp;
		qtdTemp++;
		return("$t"+reg);
		
	}
}
