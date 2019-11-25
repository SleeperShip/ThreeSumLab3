import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

public class Lab7LCS {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    /* define constants */

    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 50;
    static int MAXINPUTSIZE = (int) Math.pow(2, 15);
    static int MININPUTSIZE = 2;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/jon/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        //checkSortedCorrectness();

        //run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("BooksFast-Exp1-ThrowAway.txt");

        runFullExperiment("BooksFast-Exp2.txt");

        runFullExperiment("BooksFast-Exp3.txt");

        //String s1 = new String("1234567Swenson");
        //String s2 = new String("Swenson");
        //char[] s1Array = s1.toCharArray();
        //char[] s2Array = s2.toCharArray();

        //System.out.println("s1.length() = " + s1.length());
        //System.out.println("s2.length() = " + s2.length());
        //System.out.println("LCS of s1/s2 strings(brute force): " + LcsBrute(s1Array, s2Array));
        //System.out.println("LCS of s1/s2 strings(fast): " + LcsFast(s1Array, s2Array));
    }

    static void runFullExperiment(String resultsFileName) {
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);

        } catch (Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file " + ResultsFolderPath + resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputSize    AverageTime"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        String Jekyll = "/home/jon//Documents/DrJekyllMrHyde.txt";
        String TimeMachine = "/home/jon//Documents/TheTimeMachine.txt";
        String jekyllBook = readAllBytesJava7(Jekyll);
        String timeMachineBook = readAllBytesJava7(TimeMachine);
        String megaBookString = jekyllBook + timeMachineBook;

        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize *= 2) {

            // progress message...
            System.out.println("Running test for input size " + inputSize + " ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            //double prevTimePerTrial = 0;
            /* force garbage collection before each batch of trials run so it is not included in the time */

            System.gc();

            // generate a list of randomly spaced integers in ascending sorted order to use as test input
            // In this case we're generating one list to use for the entire set of trials (of a given input size)
            // but we will randomly generate the search key for each trial

            System.out.print("    Generating test data...");
            //char[] testList = makeWorstCaseArray('*',inputSize);

            System.out.println("...done.");

            System.out.print("    Running trial batch...");

            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            // BatchStopwatchchStopwatch.start(); // comment this line if timing trials individually


            // run the trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                // generate a random key to search in the range of a the min/max numbers in the list
                //long testSearchKey = (long) (0 + Math.random() * (testList[testList.length - 1]));
                /* force garbage collection before each trial run so it is not included in the time */

                //String randomString1 = getRandomString(inputSize);
                //String randomString2 = getRandomString(inputSize);
                //char[] randomStringAsChar1 = randomString1.toCharArray();
                //char[] randomStringAsChar2 = randomString2.toCharArray();

                int megaBookLength = megaBookString.length();
                int bookIndex1 = getRandomIndex(megaBookLength-1-inputSize);
                int bookIndex2 = getRandomIndex(megaBookLength-1-inputSize);
                String randomSubstring1 = megaBookString.substring(bookIndex1, bookIndex1+inputSize-1);
                String randomSubstring2 = megaBookString.substring(bookIndex2, bookIndex2+inputSize-1);
                char[] randomCharArrayBook1 = randomSubstring1.toCharArray();
                char[] randomCharArrayBook2 = randomSubstring2.toCharArray();

                System.gc();
                TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                /* run the function we're testing on the trial input */

                int result = LcsFast(randomCharArrayBook1, randomCharArrayBook2);

                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
            //prevTimePerTrial = averageTimePerTrialInBatch;

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f\n", inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static String readAllBytesJava7(String filePath) {
        String content = "";
        try {
            content = new String ( Files.readAllBytes( Paths.get(filePath) ) );
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String getJekyll() throws IOException{
        String path = new String("C:\\Users\\jds48\\Documents\\DSU_CSC_482_Algs\\lab 7\\DrJekyllMrHyde.txt");
        String book = readAllBytesJava7(path);
        return book;
    }

    public static String getTimeMachine() throws IOException{
        String path = new String("C:\\Users\\jds48\\Documents\\DSU_CSC_482_Algs\\lab 7\\TheTimeMachine.txt");
        String book = readAllBytesJava7(path);
        return book;
    }

    public static String readFileAsString(String fileName) throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }

    public static int getRandomIndex(int megaBookLength) {
        int index = (int)(megaBookLength * Math.random());
        return index;
    }

    public static String getRandomString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz"
                + "<>:?/'{}[]!@#$%^&*()_-+=";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
    public static char[] makeWorstCaseArray(char charToUse, int strlen) {
        char[] charArray = new char[strlen];

        for(int i=0; i<strlen; i++){
            charArray[i] = charToUse;
        }
        return charArray;
    }

    public static int LcsBrute(char[] s1, char[] s2) {   //convert to char array for algorithm
        int len1 = s1.length;
        int len2 = s2.length;
        int lcsLen = 0;
        int i,j,k = 0;

        for(i=0; i<len1; i++){
            for(j=0; j<len2; j++){
                for(k=0; k < Math.min(len1-i-1, len2-j-1); k++){
                    if(s1[i+k] != s2[j+k]){
                        break;
                    }
                }
                if(k > lcsLen){
                    lcsLen = k;
                }
            }
        }
        return lcsLen;
    }

    public static int LcsFast(char X[], char Y[]) {
        // Create a table to store lengths of longest common suffixes of
        // substrings. Note that LCStuff[i][j] contains length of longest
        // common suffix of X[0..i-1] and Y[0..j-1]. The first row and
        // first column entries have no logical meaning, they are used only
        // for simplicity of program

        int len1 = X.length;
        int len2 = Y.length;
        int LCStuff[][] = new int[len1 + 1][len2 + 1];
        int result = 0;  // To store length of the longest common substring

        // Following steps build LCStuff[m+1][n+1] in bottom up fashion
        for (int i = 0; i <= len1; i++) {
            for (int j = 0; j <= len2; j++) {

                if (i == 0 || j == 0)
                    LCStuff[i][j] = 0;
                else if (X[i - 1] == Y[j - 1]) {
                    LCStuff[i][j] = LCStuff[i - 1][j - 1] + 1;
                    result = Integer.max(result, LCStuff[i][j]);
                } else {
                    LCStuff[i][j] = 0;
                }
            }
        }
        return result;
    }

}
