import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
	static boolean ownStemmer = false;
	static boolean nlpLemma = false;
	static boolean nlpStemmer = true;

	// TreeMap: term, document, document frequency
	static TreeMap<String, TreeMap<Integer, Integer>> invertedIndex = new TreeMap<String, TreeMap<Integer, Integer>>();
	// TreeMap: document, size
	static TreeMap<Integer, Integer> docSize = new TreeMap<Integer, Integer>();
	// HashSet: stop-word
	static HashSet<String> stopwords = new HashSet<>();

	static void readInStopwordLists() throws IOException {
		String stopwordList = "stopwords.txt";
		readSingleStopwordList(stopwordList);
		stopwordList = "stopwords2.txt";
		readSingleStopwordList(stopwordList);
	}

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
		PortersStemmer ps = new PortersStemmer();
		Stemmer s = new Stemmer();
		for (int i = 0; i < documents.size(); i++) {
			int termCounter = 0;
			fileScanner = new Scanner(documents.get(i));
			int docId = Integer.parseInt(documents.get(i).getName());
			while (fileScanner.hasNext()) {
				// next token
				String token = fileScanner.next();
				// pre-processing here
				token = token.toLowerCase();
				token = token.replaceAll("[^\\w'@.]", "");
				if (token.endsWith(".")) {
					token = token.substring(0, token.length() - 1);
				}
				// 1st check for stop-word
				if (stopwords.contains(token)) {
					continue;
				}
				// only token > 1
				if (token.length() > 0) {
					Word w = new Word();
					w.setWord(token);
					String term = "";
					if (token.matches("[a-zA-z']*")) {
						if (ownStemmer) {
							term = ps.portersStemm(token);
						} else if(nlpStemmer){
							w = s.stem(w);
							term = w.word();
						} else if(nlpLemma){
							Lemmatization lemma = new Lemmatization("running");
							term = lemma.word;
							/*
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
							}*/
						}else{
							term = token;
						}
						//2nd check for stop-word
						if (stopwords.contains(term)) {
							continue;
						}
					} else {
						term = token;
					}
					addTermToInvertedIndex(term, docId);
					termCounter++;
					globalTermCounter++;
				}
			}
			docSize.put(docId, termCounter);
		}
		System.out.println("Documents read & term-lists built.");
	}
	
	public static void run(){
		Date start = new Date();
		readDocumentCollection("20news-bydate");
		System.out.println("No of Docments: "+documents.size());
		try {
			readInStopwordLists();
			System.out.println("No of Stopwords: "+stopwords.size());
			
			readInDocuments();			
			System.out.println("No of Terms: "+invertedIndex.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Date stop = new Date();
		System.out.println("Time for preprocessing: "+((stop.getTime()-start.getTime())/1000)+"s");
	}
	

	public static void main(String[] args) {
		Date start = new Date();
		readDocumentCollection("20news-bydate");
		System.out.println("No of Docments: "+documents.size());
		try {
			readInStopwordLists();
			System.out.println("No of Stopwords: "+stopwords.size());
			
			readInDocuments();
			
			System.out.println("No of Terms: "+invertedIndex.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Date stop = new Date();
		System.out.println("Time for preprocessing: "+((stop.getTime()-start.getTime())/1000)+"s");
	}
}