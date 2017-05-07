import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedSet;

public class Start {

	public static void main(String[] args) {
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
			Preprocessing.createInvertedIndex();
			System.out.println("done");
			System.out.println("Total considered terms: "+Preprocessing.invertedIndex.size());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("------------------------------------");
		System.out.println("Ranking Functions:");
		System.out.println("1 = BIM");
		System.out.println("2 = Two Poisson");
		System.out.println("3 = BM11");
		System.out.println("4 = BM25");
		System.out.println("------------------------------------");
		while(true){
			System.out.print("Select Ranking Function: (1-4) ");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	        String f = "";
	        try {
				f = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        int function = Integer.parseInt(f);
			
			
			System.out.print("Insert query: ");
	        String q = "";
	        try {
				q = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        readQuery(q);
	        System.out.println("Ranked Documents:");
	        callRankFunction(function);
			System.out.println("------------------------------------");
		}
		

	}
	
	public static ArrayList<String> queryTerms = new ArrayList<String>();
	
	public static void callRankFunction(int f){
		SortedSet<Map.Entry<Integer,Double>> ranks = null;
		switch(f){
			case 1: ranks = RankingFunctions.rankBIM(queryTerms);
			case 2: ranks = RankingFunctions.rankTwoPoisson(queryTerms, 1.5);
			case 3: ranks = RankingFunctions.rankBM11(queryTerms, 1.5);
			case 4: ranks = RankingFunctions.rankBM25(queryTerms, 1.5, 0.75);
		}
		System.out.println(ranks.toString());
	}
	
	public static void readQuery(String q){
		
		Scanner qScanner = new Scanner(q);
	    while (qScanner.hasNext( ))
	    {
	        String term = qScanner.next( );
	        // This makes the Word lower case.  
	        term = term.toLowerCase();
	        // Remove everything that isn't a letter or number
	        term = term.replaceAll("[^a-zA-Z0-9\\s]", "");
	        if(term.equals("") || !Preprocessing.invertedIndex.containsKey(term)){
	        	continue;
	        }else{
	        	//addDocIds(term);
	        	if(!queryTerms.contains(term)){
	        		queryTerms.add(term);
	        	}
	        	
	        }
	    }
	    qScanner.close();
	}

}
