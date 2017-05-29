import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

//@author Lars
public class Query {
	
	public String fullQuery;
	public String function;
	public ArrayList<String> terms;
	public TreeMap<Integer,Double> unsortedResults;
	public SortedSet<Map.Entry<Integer,Double>> sortedResults;
	public int topCount;
	
	Query(String full, String fnctn, int count){
		this.fullQuery = full;
		this.function = fnctn;
		this.topCount = count;
		this.terms = Preprocessing.getQuery(full);
	}
	
	private static SortedSet<Map.Entry<Integer,Double>> sortByValues(Map<Integer,Double> map) {
	    SortedSet<Map.Entry<Integer,Double>> sortedEntries = new TreeSet<Map.Entry<Integer,Double>>(
	        new Comparator<Map.Entry<Integer,Double>>() {
	            @Override public int compare(Map.Entry<Integer,Double> e1, Map.Entry<Integer,Double> e2) {
	                int res = e1.getValue().compareTo(e2.getValue());
	                return res != 0 ? res*(-1) : 1;
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
	public void search(){
		if(function.equals("lm")){
			LanguageModel.rank(this);
		}else{
			RankingFunctions.rank(this);
		}
		this.sortedResults = sortByValues(this.unsortedResults);
	}
	
	public int[] getTopDocs(){
		int docs[] = new int[topCount];
		int returnedDocsCount = this.sortedResults.size();
		Iterator<Entry<Integer,Double>> it = this.sortedResults.iterator();
		int i = 0;
		while(i<topCount){
			if(i >= returnedDocsCount){
				docs[i] = -1;
			}else{
				docs[i] = it.next().getKey();
			}

			i++;
		}
		return docs;
	}
	
}
