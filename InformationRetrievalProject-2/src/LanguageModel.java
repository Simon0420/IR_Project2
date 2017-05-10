import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

public class LanguageModel {
	
	static TreeMap<String, TreeMap<Integer, Integer>> invertedIndex = Preprocessing.invertedIndex;
	static TreeMap<Integer, Integer> docSize = Preprocessing.docSize;
	static TreeMap<String, TreeMap<Integer, Double>> localLM = new TreeMap<String, TreeMap<Integer, Double>>();
	static TreeMap<String, Integer> termSize = new TreeMap<String, Integer>();
	static TreeMap<String, Double> globalLM = new TreeMap<String,Double>();

	/*
	public static void exploreSubDir(String source) throws IOException{
		
		
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
*/
	
	public static void getLocalLangModel() throws IOException{
		/*
		 * get LM from root directory
		 * 1. Fetch TreeMap<docId, count> from invertedIndex.
		 * 2. 
		 * 
		 */
		System.out.println("Get Local LM");
		Iterator<String> it = invertedIndex.keySet().iterator();
				
		//여기가 String(term)별로 각 document와 frequency를 보고 LM을 구하는것.
		while(it.hasNext()){
			
			String term = it.next();
			int termCount = 0;
			TreeMap<Integer, Integer>temp = invertedIndex.get(term);
			Iterator<Integer> it2 = temp.keySet().iterator();
			while(it2.hasNext()){
				int docId = it2.next();
				
				double rank = (double) temp.get(docId) / docSize.get(docId);
				TreeMap<Integer, Double> temp2 = new TreeMap<Integer, Double>();
				temp2.put(docId, rank);
				localLM.put(term, temp2);
				
				termCount = termCount + temp.get(docId);
			}
			
			termSize.put(term,termCount);
		}
		
		System.out.println("Comlete get local LM");
		System.out.println(localLM);
		
		
		
		/*
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
		
		*/
		
	}
	
	public static void getGlobalLangModel(){
		/*
		 * 1. to compute global LM, we need some data structure
		 * tha have a term and its global count. --> termSize
		 * 2. and in the termSize, all words are already stored.
		 * 3. so we just need to divide each row with total term count.
		 */
		System.out.println("Get global LM");
		int totalTermCount = Preprocessing.invertedIndex.size();
		
		Iterator<String> it = termSize.keySet().iterator();
		
		while(it.hasNext()){
			String term = it.next();
			
			globalLM.put(term, (double) termSize.get(term) / totalTermCount);
			
		}
		System.out.println("End global LM");
		System.out.println(globalLM);
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
	
	
	
	public static void main(String args[]) throws IOException{
		System.out.println("----LangModel2-----");
		System.out.print("Read Document Collection...\t");
		Preprocessing.readDocumentCollection("20news-bydate");
		System.out.println("done");
		System.out.println("Documents found: "+Preprocessing.documents.size());
		try {
			System.out.print("Read Stopword Lists...\t\t");
			Preprocessing.readInStopwordLists();
			System.out.println("done");
			System.out.println("Stopwords found: "+Preprocessing.stopwords.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.print("Create Inverted Index...\t");
			Preprocessing.readInDocuments();
			System.out.println("done");
			System.out.println("Total considered terms: "+Preprocessing.invertedIndex.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		getLocalLangModel();
		getGlobalLangModel();
	}
}
