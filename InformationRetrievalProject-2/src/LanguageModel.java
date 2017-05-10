import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class LanguageModel {

	static int globalTermCount=0;
	static ArrayList<File> documents = Preprocessing.documents;
	static HashSet<String> stopwords = Preprocessing.stopwords;
	
	static TreeMap<Integer, Integer> docSize = Preprocessing.docSize;
	
	static TreeMap<Integer, TreeMap<String, Double>> localLM = new TreeMap<Integer, TreeMap<String, Double>>();
	static TreeMap<String ,Integer> localTermCounter = new TreeMap<String, Integer>();
	static TreeMap<String, Double> localTermProb = new TreeMap<String,Double>();
	
	static TreeMap<String,Double> globalLM = new TreeMap<String, Double>();
	static TreeMap<String,Integer> globalTermCounter = new TreeMap<String,Integer>();
	
	
	public static void main(String args[]) throws IOException{
		
		Preprocessing.readDocumentCollection("MyCollection");
		System.out.println("Document size : "+documents.size());
		System.out.println("Document read end");
		
		Preprocessing.readInStopwordLists();
		System.out.println("Stopwords size : "+stopwords.size());
		System.out.println("Stopword read end");
		
		//Preprocessing.createInvertedIndex();
		//System.out.println("docSize : " +docSize.keySet().size());
		
		exploreSubDir("MyCollection");
		
		/*
		System.out.println(localLM.size());
		System.out.println(localLM.firstKey());
		System.out.println(localLM.lastKey());
		System.out.println(localLM.get(10003));
		 */
		
		getGlobalLM();
		
		//System.out.println(globalLM.get("com"));
		//JMSmoothing("com", 10003);
	}
	
	public static void exploreSubDir(String source) throws IOException{
		/*
		 * explore dir and file recursively to explore all subfolders.
		 */
		
		File dir = new File(source);
		File[] fileList = dir.listFiles();
		
		for(int i=0; i<fileList.length; i++){
			if(fileList[i].isFile()){
				getLangModel(fileList[i]);
			}
			
			else if(fileList[i].isDirectory()){
				exploreSubDir(fileList[i].getCanonicalPath().toString());
			}
			
		}
	}
	
	public static void getLangModel(File file) throws IOException{
		/*
		 * get LM from root directory
		 * 
		 * 1. explore documents of all path in exploreSubDir()
		 * 2. do preprocessing 
		 * 3. compute each probability and compute LM 
		 * -two ways to compute LM-
		 * 1)count total number of term in document, and then compute again to get LM, but maybe too slow
		 * 2)use the number of terms in document saved in docSize. 
		 * but second way is only used after inverted index is computed.
		 * so just do with first way in here. (modify later with integrating)
		 */
		
		//only stopword and simple lemmatization. can be complete after Preprocessing.
		BufferedReader br = new BufferedReader(new FileReader(file));
		String temp;
		StringTokenizer token;
		int localTermCount=0;
		
		while((temp = br.readLine())!=null){
			token = new StringTokenizer(temp, " :.-_';/,!?<>)(,@");
				
			while(token.hasMoreTokens()){
				temp = token.nextToken().toLowerCase();
				
				if(stopwords.contains(temp)){
					continue;
				}
				if(temp == " "){
				}
				localTermCount++;
				globalTermCount++;
				
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(file));

		//System.out.println("TermCounter : "+localTermCount);
		//pause();
		
		System.out.println("Start get LM");
		
		while((temp = br.readLine())!=null){
			token = new StringTokenizer(temp, " :.-_';/,!?<>)(,@");
			
			while(token.hasMoreTokens()){
				temp = token.nextToken().toLowerCase();
				if(stopwords.contains(temp)){
					continue;
				}
				if(temp == " "){
					
				}
				getLocalLM(temp,file,localTermCount);
				
				if(globalTermCounter.containsKey(temp)){
					int globalCount = globalTermCounter.get(temp);
					globalCount = globalCount + 1;
					globalTermCounter.put(temp, globalCount);
				}
				else{
					globalTermCounter.put(temp, 1);
				}
			}
		}
		br.close();
		localLM.put(Integer.parseInt(file.getName()), localTermProb);
		System.out.println("Finish get LocaclLM");
		
	}
	
	
	//just method for debugging line by line with enter
	private static void pause() {
		// TODO Auto-generated method stub
		try{
			System.in.read();
		}catch(IOException e){}
		
	}
	
	public static void getLocalLM(String term, File file,int termCount){

		/*
		 * get LocalLM with given term and file.
		 * 1. File name is given, so just use the treemap of that file.
		 * 2. if the term exist in the map, add 1 to the count.
		 * 3. else, just put 1 with the term.
		 * 
		 * this way, we can get the count of each term, 
		 * and if we divide it by termCounter, we can easily get local LM
		 */
		
		//System.out.println("Term : "+term);
		
		if(localTermCounter.containsKey(term)){
			int count = localTermCounter.get(term);
			count = count + 1;
			localTermCounter.put(term, count);
			localTermProb.put(term, (double)count / termCount);
			//pause();
		}
		else{
			localTermCounter.put(term, 1);
			localTermProb.put(term, (double)1 / termCount);
		}
	}
	
	public static void getGlobalLM(){
		/*
		 * 1. all list of words and their counts are already stored in the step of getLM
		 * 2. so we just need to divide them with globalTermCount. 
		 */
		System.out.println("Start get globalLM");
		
		String term;
		Iterator<String> it = globalTermCounter.keySet().iterator();
		
		while(it.hasNext()){
			term = 	it.next();
			globalLM.put(term, (double)globalTermCounter.get(term) / globalTermCount);
		}
		
		System.out.println("Finish get globalLM");
		
	}
	
	public static double JMSmoothing(String term, int docId){
		/*
		 * 1. we all have information on maps
		 * 2. so we just need to compute with lamda.
		 */
		double lamda=0.5;
		//System.out.println(localLM.get(docId).get(term));
		//System.out.println(globalLM.get(term));
		
		return lamda*localLM.get(docId).get(term)+(1-lamda)*globalLM.get(term);
	}

}
