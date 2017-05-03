import java.io.File;
import java.util.ArrayList;

public class Preprocessing {
	
	static ArrayList<File> documents = new ArrayList<>();
	
	static public void readDocumentCollection(String rootFolderPath){
		File folder = new File(rootFolderPath);
		File[] subFolders = folder.listFiles();
		readFilesInSubFolders(subFolders);
	}

	private static void readFilesInSubFolders(File[] subFolders) {
		for(int i = 0; i < subFolders.length; i++){
			File subFolder = subFolders[i];
			if(subFolder.isFile()){
				documents.add(subFolder);
			}else{
				readFilesInSubFolders(subFolder.listFiles());
			}
		}
	}
	
	public static void main(String[] args) {
		readDocumentCollection("20news-bydate");
		System.out.println(documents.size());
	}
}
