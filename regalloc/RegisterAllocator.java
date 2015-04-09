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

        public String allocateRegister(String temporary)
        {
                int i;
                if(qtdTemp==10){
                        System.out.println("More than 9 values necessary!");
                        System.exit(0);
                }
                //checks if temporary already exists
                if(temporaries.containsKey(temporary))
		{
                    return temporaries.get(temporary);
                }
                else
		{
                    String regTemp = "$t"+qtdTemp;
                    temporaries.put(temporary, regTemp);
                    qtdTemp++;
                    return regTemp;
                }
        }
}

