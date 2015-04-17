package regalloc;

import java.util.Hashtable;

public class RegisterAllocator
{
        Hashtable<String, String> temporaries;
        int qtdTemp;

        public RegisterAllocator()
        {
            temporaries = new Hashtable<String,String>();
            qtdTemp = 0;
        }
		
		public String allocateReg() //Allocate register but don't map it (for local vars)
		{
			if(qtdTemp==18)
			{
                System.out.println("ERROR - Out of registers");
                System.exit(0);
            }
			
			String regTemp;
					
			if(qtdTemp < 10)
			{
				regTemp = "$t"+qtdTemp;
			}
			else
			{
				regTemp = "$s"+(qtdTemp - 10);
			}

            qtdTemp++;
            return regTemp;
		}

        public String allocateReg(String temporary)  //Allocate register and map it to a temporary
        {
            if(qtdTemp==18)
			{
                System.out.println("ERROR - Out of registers");
                System.exit(0);
            }
				
            //checks if temporary already exists
            if(temporaries.containsKey(temporary))
			{
                return temporaries.get(temporary);
            }
            else
			{
				String regTemp;
					
				if(qtdTemp < 10)
				{
					regTemp = "$t"+qtdTemp;
				}
				else
				{
					regTemp = "$s"+(qtdTemp - 10);
				}

                temporaries.put(temporary, regTemp);
                qtdTemp++;
                return regTemp;
            }
        }
		
		public String allocateTempReg(int offset)  //Allocate temporary register that won't be reserved for a temporary
		{
			offset += qtdTemp;
			
			if(offset == 18)
			{
                System.out.println("ERROR - Out of registers");
                System.exit(0);
            }
			
			String regTemp;
					
			if(offset < 10)
			{
				regTemp = "$t"+qtdTemp;
			}
			else
			{
				regTemp = "$s"+(offset - 10);
			}

            return regTemp;
		}
}

