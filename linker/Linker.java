//Mark Klara
//mak241@pitt.edu
//CS 1622 - Project 3
//Linker.java

package linker;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Linker
{
	private String inFile;
	private String outFile;
	
	public Linker()
	{
		inFile = null;
		outFile = null;
	}
	
	public Linker(String input, String output)
	{
		inFile = input;
		outFile = output;
	}
	
	public void link()
	{
		try
		{
			if(inFile == null)
			{
				System.err.println("Input file for the linker is invalid.");
				return;
			}
			
			if(outFile == null)
			{
				System.err.println("Output file for the linker is invalid.");
				return;
			}
			
			FileReader fr = new FileReader(inFile);
			BufferedReader br = new BufferedReader(fr);
			FileWriter fw = new FileWriter(outFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			
			String temp;
			while((temp = br.readLine()) != null)
			{
				bw.write(temp, 0, temp.length());
				bw.write("\n", 0, 1);
			}
			
			//Close resources
			if(br != null)
				br.close();
			if(bw != null)
				bw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}