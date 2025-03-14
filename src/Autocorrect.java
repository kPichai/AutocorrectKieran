import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author Kieran Pichai
 */
public class Autocorrect {

    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
//    public Autocorrect(String[] words, int threshold) {
//
//    }

    public static void main(String[] args) {
        Autocorrect a = new Autocorrect();
        System.out.println(a.calculateEditDistance("act", "cat"));
        System.out.println(a.calculateEditDistance("room", "rooom"));
        System.out.println(a.calculateEditDistance("toward", "twrd"));
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distance, then sorted alphabetically.
     */
    public String[] runTest(String typed) {

        return new String[0];
    }

    public int calculateEditDistance(String a, String b) {
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