
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class IRAssignment4 {

	public static void main(String[] args) throws IOException
	{
		File f = new File(args[0]);
		invertedIndex(f);
	}
	
	static void invertedIndex(File f) throws IOException
	{
		HashMap<String, HashMap<Integer, Integer>> index = new HashMap<String, HashMap<Integer, Integer>>();
		HashMap<Integer, Integer> documentTokens = new HashMap<Integer, Integer>();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(f));
		
		int docId = 0;
		int count = 0;
		while((line=br.readLine())!=null)
		{
			String[] input = line.split(" ");
			
			if(input[0].startsWith("#"))
			{
				docId = Integer.parseInt(input[1]);
			}
			
			else
			{
				if(!documentTokens.containsKey(docId))
				{
					documentTokens.put(docId, input.length);
				}
				else
				{
					int countLength = documentTokens.get(docId);
					countLength += input.length;
					documentTokens.put(docId, countLength);
				}
				
				for(int i=0;i<input.length;i++)
				{	
					if(!index.containsKey(input[i]))
					{
						HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
						map.put(docId, 1);
						index.put(input[i].toLowerCase(),map);
					}
					
					else
					{
						HashMap<Integer, Integer> temp = index.get(input[i]);
						
						if(!temp.containsKey(docId))
						{
							temp.put(docId, 1);
							index.put(input[i].toLowerCase(), temp);
						}
						
						else
						{
							count = temp.get(docId);
							count = count+1;
							temp.put(docId, count);
							index.put(input[i].toLowerCase(), temp);
						}
					}
				}
			}
		}
		
		File newFile2 = new File("h.ser");
		
		FileOutputStream fos = new FileOutputStream(newFile2);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(documentTokens);
		oos.close();
		fos.close();
		
		File newFile = new File("index.out");
		if(!newFile.exists())
		{
			newFile.createNewFile();
		}
		FileWriter fw = new FileWriter(newFile.getAbsolutePath());
		BufferedWriter bw = new BufferedWriter(fw);
		for(Map.Entry<String, HashMap<Integer, Integer>> entry: index.entrySet())
		{
			bw.write("# "+ entry.getKey());
			bw.newLine();
			HashMap<Integer, Integer> toFile = new HashMap<Integer, Integer>();
			toFile = entry.getValue();
			
			for(Map.Entry<Integer, Integer> entry2: toFile.entrySet())
			{
				bw.write(entry2.getKey()+ " " +entry2.getValue());
				bw.newLine();
			}
		}
		bw.close();
	}
}
