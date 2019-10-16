import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;

public class ThreeSumLab3{

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    /* define constants */

    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 20;
    static int MAXINPUTSIZE = (int) Math.pow(2, 15);
    static int MININPUTSIZE = 2;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/jon/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        //verifyThreeSum();

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("ThreeSumFastest-Exp1-ThrowAway.txt");

        runFullExperiment("ThreeSumFastest-Exp2.txt");

        runFullExperiment("ThreeSumFastest-Exp3.txt");

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

        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize *= 2) {
            // progress message...
            System.out.println("Running test for input size " + inputSize + " ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            /* force garbage collection before each batch of trials run so it is not included in the time */

            System.gc();

            // generate a list of randomly spaced integers in ascending sorted order to use as test input
            // In this case we're generating one list to use for the entire set of trials (of a given input size)
            // but we will randomly generate the search key for each trial

            System.out.print("    Generating test data...");
            long[] testList = createRandomIntegerList(inputSize);
            System.out.println("...done.");

            System.out.print("    Running trial batch...");

            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            // BatchStopwatchchStopwatch.start(); // comment this line if timing trials individually

            // run the trials

            for (long trial = 0; trial < numberOfTrials; trial++) {
                // generate a random key to search in the range of a the min/max numbers in the list
                long testSearchKey = (long) (0 + Math.random() * (testList[testList.length - 1]));
                /* force garbage collection before each trial run so it is not included in the time */
                System.gc();
                TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                /* run the function we're testing on the trial input */

                threeSumFastest(testList);

                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }

            //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
             double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d  %15.2f \n", inputSize, averageTimePerTrialInBatch); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static void verifyThreeSum() {
        long[] demoList = {-12432398, -4243526, -123235, 0, 12, 234, 670, 789, 3563456, 13289745, -12, -13289745, 123235, -234};
        System.out.println("Test array for Three Sum (4 expected zero sums) : ");
        for(int i=0; i<demoList.length; i++){
            System.out.print(demoList[i] + "  ");
        }
        System.out.println("");
        System.out.println("Three Sum result = " + threeSumFastest(demoList) );
    }

    public static void printThreeSum(int[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                for (int k = j+1; k < n; k++) {
                    if (a[i] + a[j] + a[k] == 0) {
                        System.out.println(a[i] + " " + a[j] + " " + a[k]);
                    }
                }
            }
        }
    }

    private static long[] merge(long[] a, long[] b) {
        long[] c = new long[a.length + b.length];
        int i = 0, j = 0;
        for (int k = 0; k < c.length; k++) {
            if      (i >= a.length) c[k] = b[j++];
            else if (j >= b.length) c[k] = a[i++];
            else if (a[i] <= b[j])  c[k] = a[i++];
            else                    c[k] = b[j++];
        }
        return c;
    }

    public static long[] mergeSort(long[] input) {
        int N = input.length;
        if (N <= 1) return input;
        long[] a = new long[N/2];
        long[] b = new long[N - N/2];
        for (int i = 0; i < a.length; i++)
            a[i] = input[i];
        for (int i = 0; i < b.length; i++)
            b[i] = input[i + N/2];
        return merge(mergeSort(a), mergeSort(b));
    }

    public static int threeSum(long[] a) {
        int n = a.length;
        int count = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                for (int k = j+1; k < n; k++) {
                    if (a[i] + a[j] + a[k] == 0) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    public static int threeSumFast(long[] a) {
        long[] arr = mergeSort(a);
        //Arrays.sort(a);
        int n = arr.length;
        int cnt = 0;
        for(int i=0; i<n; i++){
            for(int j = i+1; j<n; j++){
                if(binarySearch(-(arr[i] + arr[j]), arr) > j){
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public static int threeSumFastest(long[] a) {
        long[] newList = mergeSort(a);
        //long[] arr = mergeSort(a);
        int cnt = 0;
        int N = a.length;

        for (int i = 0; i < N; ++i) {
            //this if statement checks to make sure we don't look at a duplicate index  for i
            if (i != 0 && newList[i] == newList[i - 1]) continue;
            //j = i + 1, so points to element after i
            int j = i + 1;
            //k = length of list - 1, so point to last element.
            int k = newList.length - 1;
            while (j < k) {
                if (newList[i] + newList[j] + newList[k] == 0) {
                    cnt++;
                    ++j;
                    //avoiding duplicate j indexes
                    while (j < k && newList[j] == newList[j - 1]) ++j;
                } else if (newList[i] + newList[j] + newList[k] < 0)//if sum less than 0, move j to the right
                {
                    ++j;
                } else//otherwise, move k to the left
                {
                    --k;
                }
            }

        }

        return cnt;
    }

    public static int binarySearch(long key, long[] list) {
        int lo = 0;
        int hi = list.length - 1;
        while (lo <= hi) {
            // Key is in a[lo..hi] or not present.
            int mid = lo + (hi - lo) / 2;
            if      (key < list[mid]) hi = mid - 1;
            else if (key > list[mid]) lo = mid + 1;
            else return mid;
        }
        return -1;
    }

    public static long[] createRandomIntegerList(int size) {
        long[] newList = new long[size];

        for(int i=0; i<size; i++) {
            newList[i] = (long)(MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }

}