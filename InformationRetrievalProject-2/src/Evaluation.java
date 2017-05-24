import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class Evaluation {

	public static void main(String[] args) {

		Preprocessing.enableNlpStemmer();
		Preprocessing.run();
		//Preprocessing.printVocabulary();
		//printHugeDocuments();
		
		// This method prints out all terms ascending sorted by number of
		// documents in which they appear.
		// sortByValues(Preprocessing.invertedIndex);
		
		File dir = new File("resultPool");
		dir.mkdir();

		// Query 1
		processQuery("Yes, one can sometimes get away with running a newer ROM (of the correct size, obviously) in an older machine, but one should be prepared for problems if running software that checks for machine type rather than ROM trap availability and then draws incorrect conclusions when special-casing - especially timing-dependentand driver-related stuff. In this particular case I see no reason to go to the trouble of ROM-swapping. The Apple 32-bit enabler has problems, but MODE32");
		// Query 2
		// processQuery("asian auto quality rating");
		// Query 3
		// processQuery("asian auto quality rating");
		// Query 4
		// processQuery("asian auto quality rating");
		// Query 5
		// processQuery("asian auto quality rating");

		// Refresh Eclipse-Project after execution. All documents will be in a
		// new directory "resultPool"!
	}

	private static SortedSet<Map.Entry<String, TreeMap<Integer, Integer>>> sortByValues(Map<String, TreeMap<Integer, Integer>> map) {
		SortedSet<Map.Entry<String, TreeMap<Integer, Integer>>> sortedEntries = new TreeSet<Map.Entry<String, TreeMap<Integer, Integer>>>(
				new Comparator<Map.Entry<String, TreeMap<Integer, Integer>>>() {
					@Override
					public int compare(Map.Entry<String, TreeMap<Integer, Integer>> e1,
							Map.Entry<String, TreeMap<Integer, Integer>> e2) {
						int s1 = e1.getValue().size();
						int s2 = e2.getValue().size();
						return s1 > s2 ? 1 : -1;
					}
				});
		sortedEntries.addAll(map.entrySet());
		Iterator<Entry<String, TreeMap<Integer, Integer>>> it = sortedEntries.iterator();
		while (it.hasNext()) {
			Entry<String, TreeMap<Integer, Integer>> ob = it.next();
			System.out.println(ob.getKey() + ": " + ob.getValue().size());
		}
		return sortedEntries;
	}

	static void readDocumentCollection(String rootFolderPath, int docID) {
		File folder = new File(rootFolderPath);
		File[] subFolders = folder.listFiles();
		try {
			readFilesInSubFolders(subFolders, docID);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void printDocumentintoResultPool(int docID) throws IOException {
		String text = "";
		String docPathInCollectionFolder = Preprocessing.documentPathMapping.get(docID);
		FileReader fileReader = new FileReader(docPathInCollectionFolder);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			text += line + "\n";
		}
		bufferedReader.close();
		try (PrintWriter out = new PrintWriter("resultPool/" + docID + ".txt")) {
			out.println("Original Document at Path:" + docPathInCollectionFolder + "\n\n" + text);
		}
	}

	private static void readFilesInSubFolders(File[] subFolders, int docID) throws IOException {
		String text = "";
		for (int i = 0; i < subFolders.length; i++) {
			File subFolder = subFolders[i];
			if (subFolder.isFile()) {
				if (subFolder.getName().equals(docID + "")) {
					FileReader fileReader = new FileReader(subFolder);
					// Always wrap FileReader in BufferedReader.
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						text += line + "\n";
					}
					bufferedReader.close();
					try (PrintWriter out = new PrintWriter("resultPool/" + docID + ".txt")) {
						out.println(text);
					}
				}
			} else {
				readFilesInSubFolders(subFolder.listFiles(), docID);
			}
		}
	}

	public static void processQuery(String query) {

		Query q1 = new Query(query, "bim", 10);
		Query q2 = new Query(query, "twoP", 10);
		Query q3 = new Query(query, "bm11", 10);
		Query q4 = new Query(query, "bm25", 10);
		Query q5 = new Query(query, "lm", 10);

		q1.search();
		q2.search();
		q3.search();
		q4.search();
		q5.search();

		System.out.println("Query: " + query + " Terms: " + q1.terms);
		System.out.println("--------------------------------------------------");

		printResults(q1);
		System.out.println("--------------------------------------------------");
		printResults(q2);
		System.out.println("--------------------------------------------------");
		printResults(q3);
		System.out.println("--------------------------------------------------");
		printResults(q4);
		System.out.println("--------------------------------------------------");
		printResults(q5);

		int[] union = unionArrays(q1.getTopDocs(), q2.getTopDocs(), q3.getTopDocs(), q4.getTopDocs(), q5.getTopDocs());
		System.out.println("--------------------------------------------------");
		System.out.println("UNION has " + union.length + " documents.");
		System.out.println("--------------------------------------------------");
		Arrays.sort(union);
		for (int i = 0; i < union.length; i++) {
			System.out.println(union[i]);
			try {
				printDocumentintoResultPool(union[i]);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String printResults(Query q) {
		int[] docs = q.getTopDocs();
		String res = "Algorithm: " + q.function + "\n";
		res = res + "Rank |\tDocument ID\n";
		for (int i = 0; i < docs.length; i++) {
			res = res + (i + 1) + "\t" + docs[i] + "\n";
		}
		System.out.println(res);
		return res;
	}

	public static int[] unionArrays(int[]... arrays) {
		int maxSize = 0;
		int counter = 0;

		for (int[] array : arrays)
			maxSize += array.length;
		int[] accumulator = new int[maxSize];

		for (int[] array : arrays)
			for (int i : array)
				if (!isDuplicated(accumulator, counter, i))
					accumulator[counter++] = i;

		int[] result = new int[counter];
		for (int i = 0; i < counter; i++)
			result[i] = accumulator[i];

		return result;
	}

	public static void printHugeDocuments(){
		List<Entry<Integer, Integer>> l = entriesSortedByValues(Preprocessing.docSize);
		for(int i = 0; i < 20; i++){
			System.out.println(l.get(i) + " at "+ Preprocessing.documentPathMapping.get(l.get(i).getKey()));
		}
	}

	static <K, V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		List<Entry<K, V>> sortedEntries = new ArrayList<Entry<K, V>>(map.entrySet());
		Collections.sort(sortedEntries, new Comparator<Entry<K, V>>() {
			@Override
			public int compare(Entry<K, V> e1, Entry<K, V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

	public static boolean isDuplicated(int[] array, int counter, int value) {
		for (int i = 0; i < counter; i++)
			if (array[i] == value)
				return true;
		return false;
	}

}
