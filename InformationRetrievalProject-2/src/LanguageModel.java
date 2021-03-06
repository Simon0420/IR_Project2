import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

public class LanguageModel {
	
	static TreeMap<String, TreeMap<Integer, Integer>> invertedIndex = Preprocessing.invertedIndex;
	static TreeMap<Integer, Integer> docSize = Preprocessing.docSize;
	
	
	static TreeMap<String, TreeMap<Integer, Double>> localLM = new TreeMap<String, TreeMap<Integer, Double>>();
	
	//term, frequency
	static TreeMap<String, Integer> termSize = new TreeMap<String, Integer>();
	static TreeMap<String, Double> globalLM = new TreeMap<String,Double>();
	
	//docId, rank
	static TreeMap<Integer, Double> computedRanks = new TreeMap<Integer, Double>();


	
	private static void getLocalLangModel() throws IOException{
		/*
		 * get LM from root directory
		 * 1. Fetch TreeMap<docId, count> from invertedIndex.
		 * 2. compute each rank of term in each document
		 * 
		 */
		System.out.println();
		Iterator<String> it = invertedIndex.keySet().iterator();
		
				
		//compute LM from each term's docId and document frequency.

		while(it.hasNext()){
			
			String term = it.next();
			int totalTermCount = 0;
			
			/*
			 * temp : <docId, count of term>
			 * temp2 : <docId, rank>
			 */
			TreeMap<Integer, Integer>temp = invertedIndex.get(term);
			TreeMap<Integer, Double> temp2 = new TreeMap<Integer, Double>();

			Iterator<Integer> it2 = temp.keySet().iterator();
			while(it2.hasNext()){
				int docId = it2.next();
				int termCount = temp.get(docId);
				double rank = (double) termCount / docSize.get(docId);
	
				temp2.put(docId, rank);
				if(localLM.containsKey(term)){
					localLM.get(term).put(docId, rank);
				}else{
					localLM.put(term, temp2);
				}		
				totalTermCount = totalTermCount + termCount;
			}
			
			termSize.put(term,totalTermCount);
		}
		
	}
	
	private static void getGlobalLangModel() throws IOException{
		/*
		 * 1. to compute global LM, we need some data structure
		 * that have a term and its global count. --> termSize
		 * 2. and in the termSize, all words are already stored.
		 * 3. so we just need to divide each row with total term count.
		 */
		int totalTermCount = invertedIndex.size();
		
		Iterator<String> it = termSize.keySet().iterator();
		
		while(it.hasNext()){
			String term = it.next();
			globalLM.put(term, (double) termSize.get(term) / totalTermCount);
			
		}
		
	}
	
	private static double JMSmoothing(double localRank, double globalRank){
		/*
		 * 1. we all have information on maps
		 * 2. so we just need to compute with lamda.
		 */
		double lamda=0.5;
	
		return lamda*localRank + (1-lamda)*globalRank;
	}
	
	private static void computeRank(ArrayList<String> terms){
		HashSet<Integer> relevantDocs = new HashSet<Integer>();
		for(String term : terms){
			//docId, rank
			TreeMap<Integer, Double> localRanks = new TreeMap<Integer, Double>();	
			//first, get a data from localLM with corresponding term.
			if(invertedIndex.containsKey(term)){
				localRanks = localLM.get(term);
				Iterator<Integer> it = localRanks.keySet().iterator();
				while(it.hasNext()){
					int docId = it.next();
					if(!relevantDocs.contains(docId)){
						relevantDocs.add(docId);
					}
				}
			}
		}
		
		for(String term : terms){
			if(!invertedIndex.containsKey(term)){
				continue;
			}
			
			/*
			 * to rank with LM,  we need to get the value in LM with string of query.
			 * so, find the data which have the term in local and global LM first,
			 * and, just calculate and rank the docs to some data
			 * then return the data.
			 */
		
			//docId, rank
			TreeMap<Integer, Double> localRanks = new TreeMap<Integer, Double>();	
			//first, get a data from localLM with corresponding term.
			localRanks = localLM.get(term);
			double globalRank = globalLM.get(term);
		
			for(Integer docId : relevantDocs){
				double localRank = 0;
				if(localRanks.containsKey(docId)){
					localRank = localRanks.get(docId);
				}
				if(computedRanks.containsKey(docId)){
					double currentRank = computedRanks.get(docId);
					double p = JMSmoothing(localRank, globalRank);
					if(p != 0){
						currentRank = currentRank*p;
					}else{
						currentRank = currentRank*1;
					}
					computedRanks.put(docId, currentRank);
					
				}
				else{
					if(JMSmoothing(localRank, globalRank) != 0){
						computedRanks.put(docId, JMSmoothing(localRank, globalRank));
					}				
				}
			}
		}
	}

	/*
	 * 1. get the Query form of string from the UI.
	 * 2. get TreeMap of ranks of each term by using computeRank() function
	 * 3. sort the TreeMap and print or return it.
	 */
	private static TreeMap<Integer,Double> getRank(ArrayList<String> query){
		computedRanks = new TreeMap<Integer, Double>();
		computeRank(query);
		
		//We do not need to sort here, so just return unsorted result.
		return computedRanks;
			
	}
	
	public static void rank(Query q){
		invertedIndex = Preprocessing.invertedIndex;
		//delete phase for get LMs after integrating.
		try {
			if(localLM.isEmpty()){
				getLocalLangModel();
			}
			if(globalLM.isEmpty()){
				getGlobalLangModel();
			}
			
			q.unsortedResults = getRank(q.terms);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
}
