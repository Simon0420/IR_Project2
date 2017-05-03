import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeMap;

public class UnigramLM {

	static File[] files = new File("MyCollection").listFiles();
	static int globalCounter = 0;
	
	// term, document, document frequency
	static TreeMap<String, TreeMap<Integer, Integer>> invertedIndex = new TreeMap<String, TreeMap<Integer, Integer>>();
	
	// document, size
	static TreeMap<Integer, Integer> docSize = new TreeMap<Integer, Integer>();
	
	// document, term, value
	static TreeMap<Integer, TreeMap<String, Double>> localUnigramModels = new TreeMap<Integer, TreeMap<String, Double>>();
	
	// term, value
	static TreeMap<String, Double> globalUnigramModels = new TreeMap<String, Double>();
	
	// rating, document
	static TreeMap<Double, Integer> docRanking = new TreeMap<Double, Integer>();

	public static void main(String[] args) {
		docSize = new TreeMap<Integer, Integer>();
		try {
			createInvertedIndex();
			createUnigramModels();
			querySubmissionAndRanking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a single term of a specific document to the inverted index
	 * 
	 * @param term
	 * @param docId
	 */
	public static void addTermToIndex(String term, Integer docId) {
		if (invertedIndex.containsKey(term)) {
			TreeMap<Integer, Integer> temp = invertedIndex.get(term);
			if (temp.containsKey(docId)) {
				int occ = temp.get(docId).intValue();
				temp.put(docId, occ + 1);
			} else {
				temp.put(docId, 1);
			}
		} else {
			TreeMap<Integer, Integer> temp = new TreeMap<Integer, Integer>();
			temp.put(docId, 1);
			invertedIndex.put(term, temp);
		}
	}

	/**
	 * Goes through the files and creates the inverted index
	 * 
	 * @throws FileNotFoundException
	 */
	public static void createInvertedIndex() throws FileNotFoundException {
		Scanner fileScanner;
		for (int i = 0; i < files.length; i++) {
			String token;
			int termCounter = 0;
			fileScanner = new Scanner(files[i]);
			int docId = Integer.parseInt(files[i].getName());
			while (fileScanner.hasNext()) {
				token = fileScanner.next();
				token = token.toLowerCase().replaceAll("[^a-z]", "");
				if (token.length() > 2) {
					addTermToIndex(token, docId);
					termCounter++;
					globalCounter++;
				}
			}
			docSize.put(docId, termCounter);
		}
		System.out.println("Created inverted index.");
	}
	
	/**
	 * adds a term to the local unigram language model of document:docId
	 * 
	 * @param term
	 * @param docId
	 */
	public static void addToLocalUnigramModel(String term, int docId) {
		if (localUnigramModels.containsKey(docId)) {
			TreeMap<String, Double> localModel = localUnigramModels.get(docId);
			if (!localModel.containsKey(term)) {
				double value = ((double)invertedIndex.get(term).get(docId))/((double)docSize.get(docId));
				localUnigramModels.get(docId).put(term, value);
			}
		} else {
			TreeMap<String, Double> localModel = new TreeMap<String, Double>();
			double value = ((double)invertedIndex.get(term).get(docId)) / ((double)docSize.get(docId));
			localModel.put(term, value);
			localUnigramModels.put(docId, localModel);
		}
	}

	/**
	 * adds a term to the global unigram language model
	 * 
	 * @param term
	 */
	public static void addToGlobalUnigramModel(String term) {
		if (!globalUnigramModels.containsKey(term)) {
			int globalOccs = 0;
			TreeMap<Integer, Integer> index = invertedIndex.get(term);
			for (Integer docId : index.keySet()) {
				globalOccs = globalOccs + index.get(docId);
			}
			double value = ((double)globalOccs)/((double)globalCounter);
			globalUnigramModels.put(term, value);
		}
	}

	/**
	 * Goes through the files and creates unigram language models
	 * 
	 * @throws FileNotFoundException
	 */
	public static void createUnigramModels() throws FileNotFoundException {
		Scanner fileScanner;
		for (int i = 0; i < files.length; i++) {
			String token;
			fileScanner = new Scanner(files[i]);
			int docId = Integer.parseInt(files[i].getName());
			while (fileScanner.hasNext()) {
				token = fileScanner.next();
				token = token.toLowerCase().replaceAll("[^a-z]", "");
				if (token.length() > 2) {
					addToLocalUnigramModel(token, docId);
					addToGlobalUnigramModel(token);
				}
			}
		}
		System.out.println("Created unigram models.");
	}

	/**
	 * Gets the query terms and the relevant documents, computes the ranking as
	 * TreeMap(rating,docId)
	 * 
	 * @param queryTerms
	 * @param docs
	 * @return
	 */
	public static TreeMap<Double, Integer> jelinek_mercer_function(ArrayList<String> queryTerms,
			ArrayList<Integer> docs) {
		boolean queryTermContained = false;
		for (int j = 0; j < queryTerms.size(); j++) {
			if (globalUnigramModels.containsKey(queryTerms.get(j))) {
				queryTermContained = true;
			}
		}
		TreeMap<Double, Integer> docRanking = new TreeMap<Double, Integer>();
		if (queryTermContained) {
			for (int i = 0; i < docs.size(); i++) {
				double rating = 1;
				TreeMap<String, Double> localTM = localUnigramModels.get(docs.get(i));
				for (int j = 0; j < queryTerms.size(); j++) {
					String term = queryTerms.get(j);
					Double localValue, globalValue, p;
					if (globalUnigramModels.containsKey(term)) {
						globalValue = globalUnigramModels.get(term);
						if (localTM.containsKey(term)) {
							localValue = localTM.get(term);
						} else {
							localValue = 0.0;
						}
						p = (0.5 * localValue) + (0.5 * globalValue);
					} else {
						p = 1.0;
					}
					rating = rating * p;
				}
				docRanking.put(rating, docs.get(i));
			}
			UnigramLM.docRanking = docRanking;
			return docRanking;
		} else {
			System.out.println("Non of the query terms are contained in the global collection of documents.");
			return null;
		}
	}
	
	/**
	 * Reads the query & gets the relevant documents (and terms of the query)
	 */
	public static void querySubmissionAndRanking() {
		String query = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			System.out.print("Please enter the query: ");
			query = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner queryScanner = new Scanner(query);
		ArrayList<Integer> relevantDocs = new ArrayList<Integer>();
		ArrayList<String> queryTerms = new ArrayList<String>();
		while (queryScanner.hasNext()) {
			String token = queryScanner.next();
			token = token.toLowerCase().replaceAll("[^a-z]", "");
			if (token.length() > 2) {
				if (invertedIndex.containsKey(token)) {
					queryTerms.add(token);
					TreeMap<Integer, Integer> temp = invertedIndex.get(token);
					for (Integer docID : temp.keySet()) {
						relevantDocs.add(docID);
					}
				}
			}
		}
		TreeMap<Double, Integer> ranking = jelinek_mercer_function(queryTerms, relevantDocs);
		System.out.println("Ranking (from lowest to highest score)");
		if (ranking != null) {
			int i = 0;
			for (Double d : ranking.keySet()) {
				if(ranking.keySet().size()-i < 10){
					System.out.println("Rank: 0"+ (ranking.keySet().size()-i) +"\t Document: " + ranking.get(d) + "\t Rating: " + d);
				}else{
					System.out.println("Rank: "+ (ranking.keySet().size()-i) +"\t Document: " + ranking.get(d) + "\t Rating: " + d);
				}
				
				i++;
			}
		}
		queryScanner.close();
	}

	/**
	 * Prints the inverted index
	 */
	public static void printIndex() {
		for (String token : invertedIndex.keySet()) {
			System.out.println("Term: " + token);
			for (Integer docId : invertedIndex.get(token).keySet()) {
				System.out.println("\t Doc: " + docId + " , #: " + invertedIndex.get(token).get(docId));
			}
		}
	}

	/**
	 * Prints the local unigram models
	 */
	public static void printLocalUnigramModels() {
		for(Integer doc : localUnigramModels.keySet()){
			System.out.println("Doc: "+doc);
			for(String term : localUnigramModels.get(doc).keySet()){
				System.out.println("\t Term: "+term+"; W: "+localUnigramModels.get(doc).get(term));
			}
		}
	}
	
	/**
	 * Prints the gloabal unigram model
	 */
	public static void printGlobalUnigramModels() {
		for(String term : globalUnigramModels.keySet()){
			System.out.println("\t Term: "+term+"; W: "+globalUnigramModels.get(term));
		}
	}
}
