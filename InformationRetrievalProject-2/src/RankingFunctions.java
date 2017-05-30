import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
* This class contains the implementations of classic probabilistic retrieval models for ranking.
*/
public class RankingFunctions {
	

	public static void rank(Query q){
		switch(q.function){
		case "bim":
			q.unsortedResults = rankBIM(q.terms);
			break;
		case "twoP":
			q.unsortedResults = rankTwoPoisson(q.terms, 1.5);
			break;
		case "bm11":
			q.unsortedResults = rankBM11(q.terms, 1.5);
			break;
		case "bm25":
			q.unsortedResults = rankBM25(q.terms, 1.5, 0.75);
			break;
		}
	}
	
	/**
	 * This method implements the Binary Independence Model (without relevance judgements, with smoothing)
	 * @param query: list of preprocessed query terms
	 * @return TreeMap containing DocIds with their total weight (sum of all weights per term).
	 */
	private static TreeMap<Integer,Double> rankBIM(ArrayList<String> query){
		
		// TreeMap docId, weightSum
		TreeMap<Integer,Double> ranks = new TreeMap<Integer,Double>();
		Iterator<String> it = query.iterator();
		while(it.hasNext()){
			String term = it.next();
			double weight = getWt(term);
			if(Preprocessing.invertedIndex.containsKey(term)){
				Iterator<Integer> it2 = Preprocessing.invertedIndex.get(term).navigableKeySet().iterator();
				while(it2.hasNext()){
					int docId = it2.next();
					if(ranks.containsKey(docId)){
						double old = ranks.get(docId);
						ranks.replace(docId, old+weight);
					}else {
						ranks.put(docId, weight);
					}
				}
			}
			
		}
		
		return ranks;
	}
	
	/**
	 * This method implements the Two Poisson extension of the BIM
	 * @param query: list of preprocessed query terms
	 * @param k: calibrates the document term frequency scaling (standard value 1.5)
	 * @return TreeMap containing DocIds with their total weight (sum of all weights per term with Poisson distribution)
	 */
	private static TreeMap<Integer,Double> rankTwoPoisson(ArrayList<String> query, double k){
		
		// TreeMap docId, weightSum
		TreeMap<Integer,Double> ranks = new TreeMap<Integer,Double>();
		Iterator<String> it = query.iterator();
		while(it.hasNext()){
			String term = it.next();
			double weight = getWt(term);
			if(Preprocessing.invertedIndex.containsKey(term)){
				TreeMap<Integer,Integer> docs = Preprocessing.invertedIndex.get(term);
				Iterator<Integer> it2 = docs.navigableKeySet().iterator();
				while(it2.hasNext()){
					int docId = it2.next();
					
					// Poisson distribution included
					int termFreq = docs.get(docId); 	// f t,D
					double weightNew = ( (termFreq*(k+1))/(termFreq+k) ) * weight;
					
					if(ranks.containsKey(docId)){
						double old = ranks.get(docId);
						ranks.replace(docId, old+weightNew);
					}else {
						ranks.put(docId, weightNew);
					}
				}
			}
					
		}
				
		return ranks;
	}
	
	/**
	 * This method implements the BM11 extension of the BIM
	 * @param query: list of preprocessed query terms
	 * @param k: calibrates the document term frequency scaling (standard value 1.5)
	 * @return TreeMap containing DocIds with their total weight (sum of all weights per term 
	 * 		   including Poisson distribution and document lengths)
	 */
	private static TreeMap<Integer,Double> rankBM11(ArrayList<String> query, double k){
		
		// TreeMap docId, weightSum
		TreeMap<Integer,Double> ranks = new TreeMap<Integer,Double>();
		Iterator<String> it = query.iterator();
		double lAvg = getAvgDocLength();
		while(it.hasNext()){
			String term = it.next();
			double weight = getWt(term);
			if(Preprocessing.invertedIndex.containsKey(term)){
				TreeMap<Integer,Integer> docs = Preprocessing.invertedIndex.get(term);
				Iterator<Integer> it2 = docs.navigableKeySet().iterator();
				while(it2.hasNext()){
					int docId = it2.next();
					
					// Poisson distribution with doc length included 
					int termFreq = docs.get(docId); 	// f t,D
					double lDoc = Preprocessing.docSize.get(docId);
					double weightNew = ( (termFreq*(k+1))/( termFreq+(k*(lDoc/lAvg)) ) ) * weight;
					
					if(ranks.containsKey(docId)){
						double old = ranks.get(docId);
						ranks.replace(docId, old+weightNew);
					}else {
						ranks.put(docId, weightNew);
					}
				}
			}
					
		}
				
		return ranks;	
	}
	
	/**
	 * This method implements the BM25 extension of the BIM
	 * @param query: list of preprocessed query terms
	 * @param k: calibrates the document term frequency scaling (standard value 1.5)
	 * @param b: determines the scaling by document length: (standard value 0.75)
	 * @return TreeMap containing DocIds with their total weight (sum of all weights per term 
	 * 		   including Poisson distribution and corrected document length effect)
	 */
	private static TreeMap<Integer,Double> rankBM25(ArrayList<String> query, double k, double b){
		
		// TreeMap docId, weightSum
		TreeMap<Integer,Double> ranks = new TreeMap<Integer,Double>();
		Iterator<String> it = query.iterator();
		double lAvg = getAvgDocLength();
		while(it.hasNext()){
			String term = it.next();
			double weight = getWt(term);
			if(Preprocessing.invertedIndex.containsKey(term)){
				TreeMap<Integer,Integer> docs = Preprocessing.invertedIndex.get(term);
				Iterator<Integer> it2 = docs.navigableKeySet().iterator();
				while(it2.hasNext()){
					int docId = it2.next();
					
					// Poisson distribution with corrected doc length included 
					int termFreq = docs.get(docId); 	// f t,D
					double lDoc = Preprocessing.docSize.get(docId);
					double weightNew = ( (termFreq*(k+1))/( termFreq+(k*(lDoc/lAvg)*b)+(k*(1-b)) ) ) * weight;
					
					if(ranks.containsKey(docId)){
						double old = ranks.get(docId);
						ranks.replace(docId, old+weightNew);
					}else {
						ranks.put(docId, weightNew);
					}
				}
			}
					
		}
				
		return ranks;
	}
	
	private static double getWt(String term){
		if(!Preprocessing.invertedIndex.containsKey(term)){
			return 0.0;
		}
		int docsHaveTerm = Preprocessing.invertedIndex.get(term).size();
		int totalDocs = Preprocessing.docSize.size();
		// weight calculation includes smoothing
		double weight = Math.log(0.5 * ((totalDocs + 1)/(docsHaveTerm + 0.5)));
		return weight;
	}
	
	private static double getAvgDocLength(){
		double totalLength = 0.0;
		Iterator<Integer> it = Preprocessing.docSize.values().iterator();
		while(it.hasNext()){
			totalLength += it.next();
		}	
		return totalLength / (double)Preprocessing.docSize.size();
	}

}
