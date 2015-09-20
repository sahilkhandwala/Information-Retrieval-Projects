package Assignment4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.Map.Entry;

public class ParseQueries {
	
	final static double k1 = 1.2;
	final static int k2 = 100;
	final static int qf = 1;
	final static double b = 0.75;
	final static int N = 3204;
	final static int ri =0;
	final static int R = 0;
	static int queryId = 0;
        static int limit = 0;
    static double avdl = 0;
    final static String system_name = "Sahil_System";
	
	static HashMap<String, HashMap<Integer, Integer>> index = new HashMap<String, HashMap<Integer, Integer>>();
	static HashMap<Integer, Integer> documentTokens = new HashMap<Integer, Integer>();
	
	public static void main(String[] args) throws Exception
	{
		File f = new File(args[0]);
		File f2 = new File(args[1]);
                limit = Integer.parseInt(args[2]);
		
		try {
			String path = "./h.ser";
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			documentTokens = (HashMap)ois.readObject();
			ois.close();
			fis.close();
			
			int sum = 0;
			
			for(Map.Entry<Integer, Integer> entry: documentTokens.entrySet())
			{
				sum+=entry.getValue();
			}
			
			avdl = (double)sum/3204;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		buildIndex(f);
		parseQuery(f2);
	}
	
	static void parseQuery(File f2)
	{
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(f2));
			while((line=br.readLine())!=null)
			{
			     HashMap<Integer, Double> score = new HashMap<Integer, Double>();
				calcBM25(score,line);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void buildIndex(File f)
	{
		String line;
		try {
			String word = "";
			BufferedReader br = new BufferedReader(new FileReader(f));
			while((line=br.readLine())!=null)
			{
				String[] input = line.split(" ");
				
				if(input[0].startsWith("#"))
				{
					word = input[1];
				}
				
				else
				{
					if(!index.containsKey(word))
					{
						HashMap<Integer, Integer> v = new HashMap<Integer, Integer>();
						v.put(Integer.parseInt(input[0]), Integer.parseInt(input[1]));
						index.put(word, v);
					}
					else
					{
						HashMap<Integer, Integer> v = index.get(word);
						v.put(Integer.parseInt(input[0]), Integer.parseInt(input[1]));
						index.put(word, v);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void calcBM25(HashMap<Integer,Double>score,String line)
	{
		String[] in = line.split(" ");
		queryId++;
		for(int i=0;i<in.length;i++)
		{
			stats(score,in[i]);
		}
		
		printOutput(score,queryId);
	}
	
	static void printOutput(HashMap<Integer, Double>finalResult, int qID)
	{
		int k=0;
		Set<Entry<Integer, Double>> set = finalResult.entrySet();
		List<Entry<Integer,Double>> list = new ArrayList<Entry<Integer, Double>>(set);
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>()
		{
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double>o2)
			{
				return (o2.getValue().compareTo(o1.getValue()));
			}
		});
		
		System.out.println("QueryId" + "     " +"Q0"+ "     "+"Doc_ID"+"     "+ "Rank"+"     "+"BM25_score"+"     "+"System_Name");
		for(Map.Entry<Integer, Double> entry:list)
		{
			if(k==limit)
				break;
			k++;
			System.out.println(qID+"           "+"Q0"+"        "+entry.getKey()+"     "+k+"     "+String.format("%.4f",entry.getValue())+"        "+system_name);
		}
		System.out.println();
		System.out.println("**************************************************************************");
	}
	
	static void stats(HashMap<Integer,Double>score, String word)
	{
		int ni = 0;
		int fi=0;
		double calculation = 0;
		double calculation2=0;
		double avg = 0;
		HashMap<Integer, Integer> temp = index.get(word);
		
		for(Map.Entry<Integer, Integer> entry: temp.entrySet())
		{
			ni = ni+1;
		}

		for(int i=1;i<=3204;i++)
		{
			if(temp.containsKey(i))
			{
				fi = temp.get(i);
				calculation = bm25(ni,fi, documentTokens.get(i));
				updateScore(score,i,calculation);
			}
			/*else
			{
				//calculation2 = bm25(ni,fi, documentTokens.get(i));
				updateScore(i,0);
			}*/
		}
	}
	
	static void updateScore(HashMap<Integer,Double>score,int i, double cal)
	{
		if(score.containsKey(i))
		{
			double t = score.get(i);
			t = t+cal;
			score.put(i, t);
		}
		else
		{
			score.put(i, cal);
		}
	}
	
	static double bm25(int ni, int fi, int val)
	{
		double result = Math.log(((ri+0.5)/(R-ri+0.5))/((ni-ri+0.5)/(N-ni-R+ri+0.5))) * (((k1+1)*fi)/(calcK(val,avdl)+fi)) * (((k2+1)*qf)/(k2+qf));
		return result;
	}
	
	static double calcK(int dl, double avdl)
	{
		double K = k1*((1-b)+(b*(dl/avdl)));
		return K;
	}
}
