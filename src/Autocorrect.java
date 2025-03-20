import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Kieran Pichai
 */
public class Autocorrect {
    // Create instance variables and final variables
    private final int ngramSize = 3;
    private final int base = 256;
    private final int modulus = 50021;
    private int threshold;
    private ArrayList<String>[] nGrams;
    private ArrayList<String> shortWords;

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold) {
        // Initializes arraylists
        nGrams = new ArrayList[modulus];
        shortWords = new ArrayList<String>();
        this.threshold = threshold;
        // Calls helper to fill ngram array
        fillNGramArray(words);
    }

    // Main function repeatedly prompts user on what word they would like to autocorrect
    public static void main(String[] args) {
        // Creates autocorrect object and prompts first word
        Autocorrect a = new Autocorrect(loadDictionary("large"), 2);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the word:");
        String input = sc.nextLine();
        String[] corrections;
        // Repeatedly runs correction finder on each inputted word
        while (!input.equals("")) {
            // Calculates threshold based off of length of work
            a.threshold = Math.max(input.length() / 4, 1);
            // Makes and prints each correction suggestion
            corrections = a.runTest(input);
            for (String word : corrections) {
                System.out.println(word);
            }
            System.out.println("Enter the next word:");
            input = sc.nextLine();
        }
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distance, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        return suggestCorrections(typed);
    }

    // suggestCorrections generates all possible correction words within a threshold from an input
    public String[] suggestCorrections(String inputWord) {
        // Generates candidates to check
        ArrayList<String> candidateWords = new ArrayList<String>();
        // If the word <= 3 we don't use the ngram technique and instead use the shortWords array
        if (inputWord.length() <= 3) {
            candidateWords = shortWords;
        } else {
            // Otherwise we use the ngrams and calculate hashes of our input word
            ArrayList<Integer> inputHashes = generateHashes(inputWord);
            // Use those hashes to find all possible candidate words in the ngrams array
            for (int hash : inputHashes) {
                if (nGrams[hash] == null) {
                    continue;
                }
                for (String word : nGrams[hash]) {
                    if (!candidateWords.contains(word)) {
                        candidateWords.add(word);
                    }
                }
            }
        }
        // Calculates edit distance of all candidate words and our word
        ArrayList<String> suggestions = new ArrayList<String>();
        for (String candidate : candidateWords) {
            int editDist = calculateEditDistance(inputWord, candidate);
            // Only adds if its the different word or is less than the threshold
            if (editDist > 0 && editDist <= threshold) {
                suggestions.add(candidate);
            }
        }
        Collections.sort(suggestions);
        // Returns suggested words as a string array
        return suggestions.toArray(new String[0]);
    }

    // Generates ngrams of a string then returns the hashes of each ngram
    public ArrayList<Integer> generateHashes(String word) {
        ArrayList<Integer> hashes = new ArrayList<>();
        int currentHash = 0;
        // Checks if the word is <= 3 because then we use smallWords
        if (word.length() <= 3) {
            return hashes;
        }
        // Calculates the first hash
        for (int i = 0; i < ngramSize; i++) {
            currentHash = (currentHash * base + word.charAt(i)) % modulus;
        }
        hashes.add(currentHash);
        // Uses rabin karp fingerprinting to calculate each following hash in O(1) time
        int power = 1;
        for (int i = 0; i < ngramSize - 1; i++) {
            power *= base;
        }
        for (int i = 1; i <= word.length() - ngramSize; i++) {
            // shifts, adds, mods, considers
            currentHash *= base;
            currentHash += word.charAt(i);
            currentHash %= modulus;
            hashes.add(currentHash);
        }
        return hashes;
    }

    // Tabulation based calculating edit distance
    private int calculateEditDistance(String a, String b) {
        // Creates tabulation array
        int[][] levDist = new int[a.length() + 1][b.length() + 1];
        // Initializes left and right to 0, 1, 2, 3...
        for (int i = 1; i <= a.length(); i++) {
            levDist[i][0] = i;
        }
        for (int j = 1; j <= b.length(); j++) {
            levDist[0][j] = j;
        }
        // Loops through tabulation array
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                // Checks if their last characters match
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    levDist[i][j] = levDist[i - 1][j - 1];
                } else {
                    // Otherwise run through delete insert and substitute cases
                    int delete = levDist[i - 1][j];
                    int insert = levDist[i][j - 1];
                    int substitute = levDist[i - 1][j - 1];
                    // Gets the min of all 3 and updates levDist array
                    levDist[i][j] = 1 + Math.min(Math.min(delete, insert), substitute);
                }
            }
        }
        // Returns bottom right cell, which contains the edit distance
        return levDist[a.length()][b.length()];
    }

    // Helper that fills our ngram array right at the beginning
    private void fillNGramArray(String[] dictionary) {
        // Loops through each word in dict, calculates its ngram hashes, updates ngram array
        for (String word : dictionary) {
            ArrayList<Integer> hashes = generateHashes(word);
            for (int hash : hashes) {
                // Initializes arraylist at ngrams[hash] to avoid null access
                if (nGrams[hash] == null) {
                    nGrams[hash] = new ArrayList<String>();
                }
                // Adds word at its hashed location
                if (!nGrams[hash].contains(word)) {
                    nGrams[hash].add(word);
                }
            }
            // Updates short words
            if (word.length() <= 4) {
                shortWords.add(word);
            }
        }
    }

    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}