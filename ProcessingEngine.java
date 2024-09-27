package csc435.app;

import java.nio.charset.MalformedInputException;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class IndexResult {
    public double executionTime;
    public long totalBytesRead;

    public IndexResult(double executionTime, long totalBytesRead) {
        this.executionTime = executionTime;
        this.totalBytesRead = totalBytesRead;
    }
}

class DocPathFreqPair {
    public String documentPath;
    public long wordFrequency;

    public DocPathFreqPair(String documentPath, long wordFrequency) {
        this.documentPath = documentPath;
        this.wordFrequency = wordFrequency;
    }
}

class SearchResult {
    public double excutionTime;
    public ArrayList<DocPathFreqPair> documentFrequencies;

    public SearchResult(double executionTime, ArrayList<DocPathFreqPair> documentFrequencies) {
        this.excutionTime = executionTime;
        this.documentFrequencies = documentFrequencies;
    }
}

public class ProcessingEngine {
    // keep a reference to the index store
    private IndexStore store;

    public ProcessingEngine(IndexStore store) {
        this.store = store;
    }
   
    public IndexResult indexFolder(String folderPath) {
         // TO-DO get the start time
        // TO-DO crawl the folder path and extrac all file paths
	// TO-DO for each file put the document path in the index store and retrieve the document number
	// TO-DO for each file extract all alphanumeric terms that are larger than 2 characters
        //       and count their frequencies
        // TO-DO increment the total number of read bytes
 	// TO-DO update the main index with the word frequencies for each document
	File folder1 = new File(folderPath); 
	File[] filesInlist = folder1.listFiles(); //Storing all the files we get through the folderpath in a file array.
        long bytesIndexed = 0;
        long startTime = System.currentTimeMillis();

        if (filesInlist == null) {//When no files are found in list
            System.out.println("No files found or unable to access directory: " + folderPath);
            return new IndexResult(0, 0);
        }

        System.out.println("Starting indexing of " + filesInlist.length + " files in directory: " + folderPath);

        for (File file : filesInlist) { //Traversing all the files in list
            if (file.isFile()) { //Making sure the path being given is a file
                System.out.println("file being processed: " + file.getPath());
                long fileSize = file.length();
                bytesIndexed += fileSize;
                long docID = store.putDocument(file.getPath());
                HashMap<String, Long> wordFrequencies = getWordFrequencies(file.getPath());
                store.updateIndex(docID, wordFrequencies);
                System.out.println("Indexed file is: " + file.getPath() + " | Size: " + fileSize + " bytes");
            } else {
                System.out.println("Skipping directory: " + file.getPath());
            }
        }

        long endTime = System.currentTimeMillis();
        double executionTime = (endTime - startTime) / 1000.0;
        System.out.println("Indexing is finished. Total bytes indexed: " + bytesIndexed);
        System.out.println("Indexing took " + executionTime + " seconds.");

        return new IndexResult(executionTime, bytesIndexed); 
	 // TO-DO get the stop time and calculate the execution time
        // TO-DO return the execution time and the total number of bytes read
    }
    private HashMap<String, Long> getWordFrequencies(String filePath) {
       HashMap<String, Long> wordCounts = new HashMap<>();
	try {
        // Check if the file is a .DS_Store or other system files and skip it
        if (filePath.endsWith(".DS_Store")) {
            System.out.println("Skipping system file: " + filePath);
            return wordCounts;
        }  
	    try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            lines.forEach(line ->
                Arrays.stream(line.split("[^a-zA-Z0-9]+")) 
                    .map(String::toLowerCase)
		    .filter(word -> word.length() > 2)
                    .forEach(word -> wordCounts.merge(word, 1L, Long::sum))
		    );
        }
       } catch (MalformedInputException e) {
        System.err.println("Skipping file due to encoding issues: " + filePath);
        }
	
	catch (IOException e) {
            System.err.println("Error reading file: " + filePath);
        }
	return wordCounts;
    }

    public SearchResult searchFiles(ArrayList<String> terms) {
	SearchResult result = new SearchResult(0.0, new ArrayList<DocPathFreqPair>());
        	// TO-DO get the start time
	        // TO-DO for each term get the pairs of documents and frequencies from the index store
       	        // TO-DO combine the returned documents and frequencies from all of the specified terms
                // TO-DO sort the document and frequency pairs and keep only the top 10
		// TO-DO for each document number get from the index store the document path
	 // TO-DO get the stop time and calculate the execution time	
        // TO-DO return the execution time and the top 10 documents and frequencies
   	double startTime = System.currentTimeMillis();
	 if (terms.size() < 1) {
            throw new IllegalArgumentException("The search query must contain at least 1 term");
        }
	HashMap<Long, Long> documentFrequencyMap = new HashMap<>();
        for (String term : terms) {
        ArrayList<DocFreqPair> pairs = store.lookupIndex(term.toLowerCase()); // Ensure case insensitivity
	
        for (DocFreqPair pair : pairs) {
            documentFrequencyMap.merge(pair.documentNumber, pair.wordFrequency, Long::sum);
        }
    }
        ArrayList<DocPathFreqPair> results = new ArrayList<>();
    for (Map.Entry<Long, Long> entry : documentFrequencyMap.entrySet()) {
        String docPath = store.getDocument(entry.getKey());
        if (docPath != null) {
            results.add(new DocPathFreqPair(docPath, entry.getValue()));
        }
    }
    results = results.stream()
            .sorted((a, b) -> Long.compare(b.wordFrequency, a.wordFrequency))
            .limit(10)
            .collect(Collectors.toCollection(ArrayList::new));

        double endTime = System.currentTimeMillis();
        double executionTime2 = (endTime - startTime) / 1000.0;
	result.excutionTime = executionTime2;
	result.documentFrequencies = new ArrayList<>(results);
        return new SearchResult(executionTime2, new ArrayList<>(results));
    }

}
