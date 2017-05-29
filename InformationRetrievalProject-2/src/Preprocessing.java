import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Stemmer;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.util.CoreMap;

public class Preprocessing {

	static ArrayList<File> documents = new ArrayList<>();
	static int globalTermCounter = 0;
	static private boolean ownStemmer = false;
	static private boolean nlpLemma = false;
	static private boolean nlpStemmer = true;

	// TreeMap: term, document, document frequency
	static TreeMap<String, TreeMap<Integer, Integer>> invertedIndex = new TreeMap<String, TreeMap<Integer, Integer>>();
	// TreeMap: document, size
	static TreeMap<Integer, Integer> docSize = new TreeMap<Integer, Integer>();
	// HashSet: stop-word
	static HashSet<String> stopwords = new HashSet<>();
	// maps created doc-ids to the path in the collection
	static TreeMap<Integer, String> documentPathMapping = new TreeMap<Integer, String>();

	/**
	 * This method merges two common stopword lists, to cover as many stopwords as possbile.
	 * @throws IOException
	 */
	static void readInStopwordLists() throws IOException {
		readSingleStopwordList("stopwords.txt");
		readSingleStopwordList("stopwords_ranksnl.txt");
	}

	/**
	 * This method reads a single stopword-list and adds the words to the stopwords treemap.
	 * @param stopwordList
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void readSingleStopwordList(String stopwordList) throws FileNotFoundException, IOException {
		// FileReader reads text files in the default encoding.
		FileReader fileReader = new FileReader(stopwordList);
		// Always wrap FileReader in BufferedReader.
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			if (line.length() != 0) {
				if (!stopwords.contains(line)) {
					stopwords.add(line);
				}
			}
		}
		bufferedReader.close();
	}

	/**
	 * Reads in a document collection.
	 * 
	 * @param rootFolderPath
	 *            root-folder-path of the collection as String
	 */
	static void readDocumentCollection(String rootFolderPath) {
		File folder = new File(rootFolderPath);
		File[] subFolders = folder.listFiles();
		readFilesInSubFolders(subFolders);
	}

	/**
	 * Reads files in the given sub-folders recursively.
	 * 
	 * @param subFolders
	 *            Array of sub-folders
	 */
	private static void readFilesInSubFolders(File[] subFolders) {
		for (int i = 0; i < subFolders.length; i++) {
			File subFolder = subFolders[i];
			if (subFolder.isFile()) {
				documents.add(subFolder);
			} else {
				readFilesInSubFolders(subFolder.listFiles());
			}
		}
	}

	/**
	 * Adds a single term of a specific document to the inverted index
	 * 
	 * @param term
	 * @param docId
	 */
	private static void addTermToInvertedIndex(String term, Integer docId) {
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
	static void readInDocuments() throws FileNotFoundException {
		Scanner fileScanner;
		//Stanford NLP Stemmer
		Stemmer s = new Stemmer();
		//Group 11 Stemmer
		PortersStemmer ps = new PortersStemmer();
		for (int i = 0; i < documents.size(); i++) {
			int termCounter = 0;
			fileScanner = new Scanner(documents.get(i));
			int docId = i;
			String path = documents.get(i).getPath();
			documentPathMapping.put(i, path);
			while (fileScanner.hasNext()) {
				// next token
				String token = fileScanner.next();
				// first preprocessing steps
				token = useRegexOnToken(token);
				// 1st check for stop-word
				if (stopwords.contains(token)) {
					continue;
				}
				token = token.trim();
				// only consider token > 0 now
				if (token.length() > 0) {					
					String term = "";
					// check for 'normal' word?
					if (token.matches("[a-z]+[a-z']*")) {
						// stemming
						if (ownStemmer) {							
							term = ps.portersStemm(token);
						} else if(nlpStemmer){							
							Word w = new Word();
							w.setWord(token);
							w = s.stem(w);
							term = w.word();
						} else if(nlpLemma){
							Lemmatization lemma = new Lemmatization(token);
							term = lemma.word;
							// stanford lemmatize, language models too big...
							//term = standfordLemmatize(token);
						}else{
							term = token;
						}
						//2nd check for stop-word
						if (stopwords.contains(term)) {
							continue;
						}
					}
					// check for date?
					else if(token.matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-2][0-9]{3}")){
						term = token;
					} 
					// check for word,numbers or numbers,word to extract things like honda-x5 -> hondax
					// or 100mg, 125km/h -> mg, kmh etc...
					else if(token.matches("[0-9]{0,4}[a-zA-Z]*") || token.matches("[a-zA-Z]*[0-9]+")){
						String temptoken = token;
						term = temptoken.replaceAll("[0-9]", "");
					}
					// with/without emails (if code used, mails also will be in the vocabulary)
					/*
					else if(token.matches("[a-z0-9]+[a-z_0-9\\.]*[a-z0-9]+@[a-z_0-9]+\\.[a-z_0-9\\.]*[a-z0-9]+")){
						term = token;
					}*/
					// again check length just to be sure
					if(term.length() > 0){
						// delete subsequent '-s 
						if(term.charAt(term.length()-1) == '\''){
							term = term.substring(0, term.length() - 1);
						}
						// finally, add term to index
						addTermToInvertedIndex(term, docId);
						termCounter++;
						globalTermCounter++;
					}
				}
			}
			docSize.put(docId, termCounter);
		}
		System.out.println("Documents read & term-lists built.");
	}

	@SuppressWarnings("unused")
	private static String standfordLemmatize(String token) {
		String term;
		@SuppressWarnings("serial")
		StanfordCoreNLP pipeline = new StanfordCoreNLP(new Properties(){{
			  setProperty("annotators", "tokenize,ssplit,pos,lemma");
			}});
		Annotation tokenAnnotation = new Annotation(token);
		List<CoreMap> list = tokenAnnotation.get(SentencesAnnotation.class);
		try{
		term = list.get(0).get(TokensAnnotation.class).get(0).get(LemmaAnnotation.class);
		}catch(NullPointerException ne){
			System.out.println("Nullpointer...");
			term = token;
		}
		return term;
	}

	/**
	 * Does first regex-operations on the token.
	 * 
	 * @param token
	 * @return
	 */
	private static String useRegexOnToken(String token) {
		token = token.toLowerCase();
		token = token.trim();
		// replace everything except a-zA-Z_0-9'@ and .
		token = token.replaceAll("[^\\w'@\\.]", "");
		
		token = token.replaceAll("''+","").replaceAll("\\.\\.+", "").replaceAll(".*__+.*", "").replaceAll(".*@@+.*", "");
		// you could use the following commented-out code instead of the replace in the line before (would be more radical cleaning)
		// (tradeoff between losing information and get terms that actually make no sense)
		/*
		if(token.matches(".*@@+.*") || token.matches(".*__+.*") || token.matches(".*\\.\\.+.*") || token.matches(".*''+.*")){
			token = "";
		}*/
		
		// delete subsequent and trailing special characters (observed in the vocabulary)
		while(token.length() > 0 && (token.charAt(0) == '\'' || token.charAt(0) == '.' || token.charAt(0) == '_')){
			token = token.substring(1);
		}				
		while(token.endsWith(".")||token.endsWith("?")||token.endsWith("!")||token.endsWith(",")||token.endsWith(":")||token.endsWith("'")||token.endsWith("_")){
			token = token.substring(0, token.length() - 1);
		}
		return token;
	}
	
	/**
	 * Method to read in & process a query. Outputs remaining query-terms as list.
	 * 
	 * @param txt
	 * @return
	 */
	public static ArrayList<String> getQuery(String txt){
		Scanner fileScanner;
		PortersStemmer ps = new PortersStemmer();
		Stemmer s = new Stemmer();
		fileScanner = new Scanner(txt);
		ArrayList<String> queryTerms = new ArrayList<String>();
		while (fileScanner.hasNext()) {
			// next token
			String token = fileScanner.next();
			// first preprocessing steps
			token = useRegexOnToken(token);
			// 1st check for stop-word
			if (stopwords.contains(token)) {
				continue;
			}
			token = token.trim();
			// only consider token > 0 now
			if (token.length() > 0) {					
				String term = "";
				// check for 'normal' word?
				if (token.matches("[a-z]+[a-z']*")) {
					// stemming
					if (ownStemmer) {							
						term = ps.portersStemm(token);
					} else if(nlpStemmer){							
						Word w = new Word();
						w.setWord(token);
						w = s.stem(w);
						term = w.word();
					} else if(nlpLemma){
						Lemmatization lemma = new Lemmatization(token);
						term = lemma.word;
						// stanford lemmatize, language models too big...
						//term = standfordLemmatize(token);
					}else{
						term = token;
					}
					//2nd check for stop-word
					if (stopwords.contains(term)) {
						continue;
					}
				}
				// check for date?
				else if(token.matches("[0-3][0-9]\\.[0-1][0-9]\\.[0-2][0-9]{3}")){
					term = token;
				} 
				// check for word,numbers or numbers,word to extract things like honda-x5 -> hondax
				// or 100mg, 125km/h -> mg, kmh etc...
				else if(token.matches("[0-9]{0,4}[a-zA-Z]*") || token.matches("[a-zA-Z]*[0-9]+")){
					String temptoken = token;
					term = temptoken.replaceAll("[0-9]", "");
				}
				// with/without emails (if code used, mails also will be in the vocabulary)
				/*
				else if(token.matches("[a-z0-9]+[a-z_0-9\\.]*[a-z0-9]+@[a-z_0-9]+\\.[a-z_0-9\\.]*[a-z0-9]+")){
					term = token;
				}*/
				// again check length just to be sure
				if(term.length() > 0){
					// delete subsequent '-s 
					if(term.charAt(term.length()-1) == '\''){
						term = term.substring(0, term.length() - 1);
					}
					// finally, add term to index
					queryTerms.add(term);
				}
			}
		}
		fileScanner.close();
		return queryTerms;
	}
	
	public static void run(){
		Date start = new Date();
		invertedIndex = new TreeMap<String, TreeMap<Integer, Integer>>();
		docSize = new TreeMap<Integer, Integer>();
		documents = new ArrayList<>();
		readDocumentCollection("20news-bydate");
		System.out.println("No of Docments: "+documents.size());
		try {
			readInStopwordLists();
			System.out.println("No of Stopwords: "+stopwords.size());
			
			if(Preprocessing.isNlpLemma()){
				System.out.println("NLP Lemmatization");
			}else if(Preprocessing.isNlpStemmer()){
				System.out.println("NLP Stemming");
			}else{
				System.out.println("Custom Stemming");
			}
			
			readInDocuments();			
			System.out.println("No of Terms: "+invertedIndex.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Date stop = new Date();
		System.out.println("Time for preprocessing: "+((stop.getTime()-start.getTime())/1000)+"s");
	}
	
	/*
	public static void main(String[] args) {
		Date start = new Date();
		readDocumentCollection("20news-bydate");
		System.out.println("No of Docments: "+documents.size());
		try {
			readInStopwordLists();
			System.out.println("No of Stopwords: "+stopwords.size());
			
			if(Preprocessing.isNlpLemma()){
				System.out.println("NLP Lemmatization");
			}else if(Preprocessing.isNlpStemmer()){
				System.out.println("NLP Stemming");
			}else{
				System.out.println("Custom Stemming");
			}
			
			readInDocuments();
			System.out.println("No of Terms: "+invertedIndex.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Date stop = new Date();
		System.out.println("Time for preprocessing: "+((stop.getTime()-start.getTime())/1000)+"s");
	}*/

	public static boolean isOwnStemmer() {
		return ownStemmer;
	}

	public static void enableOwnStemmer() {
		Preprocessing.ownStemmer = true;
		Preprocessing.nlpStemmer = false;
		Preprocessing.nlpLemma = false;
	}

	public static boolean isNlpLemma() {
		return nlpLemma;
	}

	public static void enableNlpLemma() {
		Preprocessing.ownStemmer = false;
		Preprocessing.nlpStemmer = false;
		Preprocessing.nlpLemma = true;
	}

	public static boolean isNlpStemmer() {
		return nlpStemmer;
	}

	public static void enableNlpStemmer() {
		Preprocessing.ownStemmer = false;
		Preprocessing.nlpStemmer = true;
		Preprocessing.nlpLemma = false;
	}
	
	public static void printVocabulary(){
		File dir = new File("resultPool");
		dir.mkdir();
		FileWriter fw;
		try {
			fw = new FileWriter("resultPool/voc.txt",false);
			BufferedWriter bw = new BufferedWriter(fw);
			for(String term: invertedIndex.keySet()){
				bw.write(term);
				bw.newLine();
			}
		    bw.close();
		} catch (IOException e) {
		}  	
	}
	
	public static void main(String[] args) {
		Preprocessing.run();
		Preprocessing.printVocabulary();
	}
}