package csc435.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Data structure that stores a document number and the number of time a word/term appears in the document
class DocFreqPair {
    public long documentNumber;
    public long wordFrequency;

    public DocFreqPair(long documentNumber, long wordFrequency) {
        this.documentNumber = documentNumber;
        this.wordFrequency = wordFrequency;
    }
}

public class IndexStore {
    private HashMap<String, Long> documentMap = new HashMap<>(); // TO-DO declare data structure that keeps track of the DocumentMap
    private HashMap<String, Map<Long, Long>> termInvertedIndex = new HashMap<>(); // TO-DO declare data structures that keeps track of the TermInvertedIndex

        // TO-DO initialize the DocumentMap and TermInvertedIndex members.

    public long putDocument(String documentPath) {
        long documentNumber = 0;
        // TO-DO assign a unique number to the document path and return the number
		documentMap.put(documentPath, documentNumber);
		documentNumber++;//Incrementing document number after assigning to each doc.
        return documentNumber; //Returning document number after receiving documentPath and assigning the same number to it
    }

    public String getDocument(long documentNumber) {
        // TO-DO retrieve the document path that has the given document number
        for (Map.Entry<String, Long> entry: documentMap.entrySet()) {
            if (entry.getValue() == documentNumber){ //if number provided as arg is equal to entry set value then we return entry set key that is docPath
                return entry.getKey();
            }
        }
        return null;
    }

    public void updateIndex(long documentNumber, HashMap<String, Long> wordFrequencies) {
        // TO-DO update the TermInvertedIndex with the word frequencies of the specified document
    	 for(Map.Entry<String, Long> entry: wordFrequencies.entrySet()){ //Traversing all the entry sets i.e. key values from wordFrequencies
		 String word = entry.getKey(); //Retrieving only the key i.e word here.
		 Long frequency = entry.getValue(); //Retrieving the value i.e. frequency here.
		 termInvertedIndex.computeIfAbsent(word, k -> new HashMap<>()).put(documentNumber, frequency);//updating termInvertedIndex hashMap here.
    	}
	 }

    public ArrayList<DocFreqPair> lookupIndex(String term) {
	ArrayList<DocFreqPair> results = new ArrayList<>();
        // TO-DO return the document and frequency pairs for the specified term
	 Map<Long, Long> docs = termInvertedIndex.get(term.toLowerCase());
        if (docs != null) {
            for (Map.Entry<Long, Long> entry : docs.entrySet()) {
                results.add(new DocFreqPair(entry.getKey(), entry.getValue()));
            }
        }
        return results;
    }
}
