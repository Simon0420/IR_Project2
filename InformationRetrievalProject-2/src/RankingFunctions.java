import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeMap;

// @author Lars Hoffmann
public class RankingFunctions {

	public static void main(String[] args) {
		Preprocessing.readDocumentCollection("20news-bydate");
		System.out.println(Preprocessing.documents.size());
		try {
			Preprocessing.readInStopwordLists();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(Preprocessing.stopwords.size());
		try {
			Preprocessing.createInvertedIndex();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println(Preprocessing.invertedIndex.size());
		
		
	}
	
	public static TreeMap<Integer,Double> docRanks = new TreeMap<Integer,Double>();
	
	public static void rankBIM(){
		// TreeMap docId, weightSum
		TreeMap<Integer,Double> ranks = new TreeMap<Integer,Double>();
	}
	
	public static void rankTwoPoisson(){
		
	}
	
	public static void rankBM11(){
		
	}
	
	public static void rankBM25(){
		
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

}
