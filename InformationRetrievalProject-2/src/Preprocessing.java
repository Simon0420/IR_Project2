import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.TreeMap;

public class Preprocessing {

	static ArrayList<File> documents = new ArrayList<>();
	static int globalTermCounter = 0;

	// TreeMap: term, document, document frequency
	static TreeMap<String, TreeMap<Integer, Integer>> invertedIndex = new TreeMap<String, TreeMap<Integer, Integer>>();
	// TreeMap: document, size
	static TreeMap<Integer, Integer> docSize = new TreeMap<Integer, Integer>();
	// HashSet: stopword
	static HashSet<String> stopwords = new HashSet<>();

	static public void readInStopwordLists() throws IOException {
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
			if(line.length() != 0){
				if(!stopwords.contains(line)){
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
	static public void readDocumentCollection(String rootFolderPath) {
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
	public static void addTermToInvertedIndex(String term, Integer docId) {
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
	public static void readInDocuments() throws FileNotFoundException {
		Scanner fileScanner;
		PortersStemmer ps = new PortersStemmer();
		for (int i = 0; i < documents.size(); i++) {
			int termCounter = 0;

			fileScanner = new Scanner(documents.get(i));
			int docId = Integer.parseInt(documents.get(i).getName());
			while (fileScanner.hasNext()) {
				// next token
				String token = fileScanner.next();
				// pre-processing here
				token = token.toLowerCase();
				token = token.replaceAll("[^\\w']", "");
				//stop words?
				if(stopwords.contains(token)){
					continue;
				}
				// only token > 1
				if (token.length() > 1) {
					String term;
					if(token.matches("[a-zA-z]*")){
						 term = ps.portersStemm(token);
					}else{
						term = token;
					}					
					addTermToInvertedIndex(term, docId);
					termCounter++;
					globalTermCounter++;
				}
			}
			docSize.put(docId, termCounter);
		}
		//System.out.println("Created inverted index.");
	}

	public static void main(String[] args) {
		readDocumentCollection("20news-bydate");
		System.out.println(documents.size());
		try {
			readInStopwordLists();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(stopwords.size());
	}
}
