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
        nGrams = new ArrayList[modulus];
        shortWords = new ArrayList<String>();
        this.threshold = threshold;
        fillNGramArray(words);
    }

    public static void main(String[] args) {
        Autocorrect a = new Autocorrect(loadDictionary("large"), 2);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the word:");
        String input = sc.nextLine();
        String[] corrections;
        while (!input.equals("")) {
            a.threshold = Math.max(input.length() / 3, 1);

            corrections = a.runTest(input);
            for (String word : corrections) {
                System.out.println(word + ", Edit distance: " + a.calculateEditDistance(word, input));
            }
            System.out.println("Enter the next word:");
            input = sc.nextLine();
        }
//        System.out.println(a.calculateEditDistance("act", "cat"));
//        System.out.println(a.calculateEditDistance("room", "roooom"));
//        System.out.println(a.calculateEditDistance("toward", "twrd"));
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

    public String[] suggestCorrections(String inputWord) {
        ArrayList<String> candidateWords = new ArrayList<String>();
        if (inputWord.length() <= 3) {
            candidateWords = shortWords;
        } else {
            for (int i = 0; i <= inputWord.length() - ngramSize; i++) {
                String ngram = inputWord.substring(i, i + ngramSize);
                int hash = calculateRabinKarpHash(ngram);
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

        ArrayList<String> suggestions = new ArrayList<String>();
        for (String candidate : candidateWords) {
            int editDist = calculateEditDistance(inputWord, candidate);
            if (editDist > 0 && editDist <= threshold) {
                suggestions.add(candidate);
            }
        }

        Collections.sort(suggestions);

        return suggestions.toArray(new String[0]);
    }

    private int calculateEditDistance(String a, String b) {
        int[][] levDist = new int[a.length() + 1][b.length() + 1];

        for (int i = 1; i <= a.length(); i++) {
            levDist[i][0] = i;
        }
        for (int j = 1; j <= b.length(); j++) {
            levDist[0][j] = j;
        }

        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    levDist[i][j] = levDist[i - 1][j - 1];
                } else {
                    int delete = levDist[i - 1][j];
                    int insert = levDist[i][j - 1];
                    int substitute = levDist[i - 1][j - 1];
                    levDist[i][j] = 1 + Math.min(Math.min(delete, insert), substitute);
                }
            }
        }

        return levDist[a.length()][b.length()];
    }

    private int calculateRabinKarpHash(String text) {
        int hash = 0;
        for (int i = 0; i < text.length(); i++) {
            hash = (hash * base + text.charAt(i)) % modulus;
        }
        return hash;
    }

    private void fillNGramArray(String[] dictionary) {
        for (String word : dictionary) {
            for (int i = 0; i <= word.length() - ngramSize; i++) {
                String ngram = word.substring(i, i + ngramSize);
                int hash = calculateRabinKarpHash(ngram);
                // Use previous ngrams to make calculation more efficent
                if (nGrams[hash] == null) {
                    nGrams[hash] = new ArrayList<>();
                }
                nGrams[hash].add(word);
            }
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