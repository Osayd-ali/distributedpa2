package csc435.app;

import java.lang.System;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.util.Arrays.asList;

public class AppInterface {
    // keep a reference to the processing engine
    private ProcessingEngine engine;

    public AppInterface(ProcessingEngine engine) {
        this.engine = engine;
    }

    public void readCommands() {
	System.out.println("Enter commands ('index <path>', 'search <term1> <term2> ...', 'quit'):"); // TO-DO implement the read commands method
        Scanner sc = new Scanner(System.in);
        String command;
        
        while (true) {
            System.out.print("> ");
            
            // read from command line
            command = sc.nextLine();

            // if the command is quit, terminate the program       
                if ("quit".equals(command)) {
                System.out.println("Ending the program");
                break;
            } else if (command.startsWith("index ")) {
                String directory = command.substring(6); 
                System.out.println("Provided directory getting Indexed: " + directory);
                IndexResult resultOfIndex = engine.indexFolder(directory);
                System.out.println("Time taken to index: " + resultOfIndex.executionTime + " seconds");
                System.out.println("Total bytes read: " + resultOfIndex.totalBytesRead);
            } else if (command.startsWith("search ")) {
                String searchTerms = command.substring(7);
                ArrayList<String> termsOfSearch = new ArrayList<>(Arrays.asList(searchTerms.split("\\s+")));
		if (termsOfSearch.isEmpty()) {
                System.out.println("Please provide search terms.");
                continue;
                }
                SearchResult searchResult = engine.searchFiles(termsOfSearch);
                System.out.println("Search has been completed in " + searchResult.excutionTime + " seconds");
                System.out.println("Top results with highest Frequency:");
		if (searchResult.documentFrequencies.isEmpty()) {
                System.out.println("No documents found for the search query.");
            } else {
                for (DocPathFreqPair pair : searchResult.documentFrequencies) {
                    System.out.println(pair.documentPath + ": " + pair.wordFrequency);
                }
            }
	    }
	    else {

            System.out.println("unrecognized command!");
        }
	}

        sc.close();
    }
}
