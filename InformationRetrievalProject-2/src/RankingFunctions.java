import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

// @author Lars Hoffmann
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
	
	// return unsorted DocIds with their total weight (sum of all weights per term).
	private static TreeMap<Integer,Double> rankBIM(ArrayList<String> query){
		// TreeMap docId, weightSum
		System.out.println("rankBIM");
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
	
	// return unsorted DocIds with their total weight (sum of all weights per term) with Poisson distribution.
	// k is a real constant, usually 1 <= k < 2
	private static TreeMap<Integer,Double> rankTwoPoisson(ArrayList<String> query, double k){
		// TreeMap docId, weightSum
		System.out.println("rank2-P");
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
	
	// return unsorted DocIds with their total weight (sum of all weights per term) with Poisson distribution.
	// k is a real constant, usually 1 <= k < 2
	// includes document lengths
	private static TreeMap<Integer,Double> rankBM11(ArrayList<String> query, double k){
		// TreeMap docId, weightSum
		System.out.println("rankBM11");
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
					
					// Poisson distribution with doc length included 
					int termFreq = docs.get(docId); 	// f t,D
					double lAvg = getAvgDocLength();
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
	
	// return unsorted DocIds with their total weight (sum of all weights per term) with Poisson distribution.
	// k is a real constant, usually 1 <= k < 2
	// includes document lengths	
	// most common value for parameter b is b = 0.75 (for correction of doc length)
	private static TreeMap<Integer,Double> rankBM25(ArrayList<String> query, double k, double b){
		// TreeMap docId, weightSum
		System.out.println("rankBM25");
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
					
					// Poisson distribution with corrected doc length included 
					int termFreq = docs.get(docId); 	// f t,D
					double lAvg = getAvgDocLength();
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
		double weight = Math.log(0.5 * ((totalDocs + 1)/(docsHaveTerm + 0.5))); //with smoothing
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
