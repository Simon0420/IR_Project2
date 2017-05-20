import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class Evaluation {

	public static void main(String[] args) {
		
		Preprocessing.enableNlpStemmer();
		Preprocessing.run();
		
		File dir = new File("resultPool");
		dir.mkdir();
		
		//Query 1
		processQuery("asian auto quality rating");
		//Query 2
		//processQuery("asian auto quality rating");
		//Query 3
		//processQuery("asian auto quality rating");
		//Query 4
		//processQuery("asian auto quality rating");
		//Query 5
		//processQuery("asian auto quality rating");

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


	private static void readFilesInSubFolders(File[] subFolders, int docID) throws IOException {
		String text= "";
		for (int i = 0; i < subFolders.length; i++) {
			File subFolder = subFolders[i];
			if (subFolder.isFile()) {
				if(subFolder.getName().equals(docID+"")){
					FileReader fileReader = new FileReader(subFolder);
					// Always wrap FileReader in BufferedReader.
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						text += line+"\n";
					}
					bufferedReader.close();
					try(  
							PrintWriter out = new PrintWriter("resultPool/"+docID+".txt")  ){
						    out.println( text );
						}
				}
				
			} else {
				readFilesInSubFolders(subFolder.listFiles(), docID);
			}
		}
		
	}
	
	public static void processQuery(String query){
		
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
		
		System.out.println("Query: "+query+" Terms: "+q1.terms);
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
		
		int[] union = unionArrays(q1.getTopDocs(),q2.getTopDocs(),q3.getTopDocs(),q4.getTopDocs(),q5.getTopDocs());
		System.out.println("--------------------------------------------------");
		System.out.println("UNION has "+union.length+" documents.");
		System.out.println("--------------------------------------------------");
		Arrays.sort(union);
		for(int i=0; i<union.length; i++){
			System.out.println(union[i]);
			readDocumentCollection("20news-bydate",union[i]);
		}
		
		
	}
		
	
	public static String printResults(Query q){
		int[] docs = q.getTopDocs();
		String res = "Algorithm: " + q.function+"\n";
		res = res + "Rank |\tDocument ID\n";
		for(int i=0; i<docs.length; i++){
			res = res + (i+1) + "\t" + docs[i] + "\n";
		}
		System.out.println(res);
		return res;
	}
	
	public static int[] unionArrays(int[]... arrays)
    {
        int maxSize = 0;
        int counter = 0;

        for(int[] array : arrays) maxSize += array.length;
        int[] accumulator = new int[maxSize];

        for(int[] array : arrays)
            for(int i : array)
                if(!isDuplicated(accumulator, counter, i))
                    accumulator[counter++] = i;

        int[] result = new int[counter];
        for(int i = 0; i < counter; i++) result[i] = accumulator[i];

        return result;
    }

    public static boolean isDuplicated(int[] array, int counter, int value)
    {
        for(int i = 0; i < counter; i++) if(array[i] == value) return true;
        return false;
    }

}
