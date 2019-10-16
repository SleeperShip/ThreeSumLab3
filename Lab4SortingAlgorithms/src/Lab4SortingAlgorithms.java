import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Random;

public class Lab4SortingAlgorithms {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    /* define constants */

    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 50;
    static int MAXINPUTSIZE = (int) Math.pow(2, 18);
    static int MININPUTSIZE = 2;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/jon/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        //checkSortedCorrectness();

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("BubbleSort-Exp1-ThrowAway.txt");

        runFullExperiment("BubbleSort-Exp2.txt");

        runFullExperiment("BubbleSort-Exp3.txt");

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

        resultsWriter.println("#InputSize    AverageTime    DoublingRatio"); // # marks a comment in gnuplot data
        resultsWriter.flush();

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

                //quickSortRand(testList, 0, testList.length-1);
                long[] sortedTestList = bubbleSort((testList));

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

     static int partition ( long A[],int start ,int end) {
         int i = start + 1;
         long piv = A[start] ;            //make the first element as pivot element.
        for(int j =start + 1; j <= end ; j++ )  {
         /*rearrange the array by putting elements which are less than pivot
            on one side and which are greater that on other. */

              if ( A[ j ] < piv) {
                      //swap (A[ i ],A [ j ]);
                      long temp = A[i];
                      A[i] = A[j];
                      A[j] = temp;
                 i += 1;
             }
        }
        //swap ( A[ start ] ,A[ i-1 ] ) ;  //put the pivot element in its proper place.
         long temp2 = A[start];
         A[start] = A[i-1];
         A[i-1] = temp2;
       return i-1;                      //return the position of the pivot
     }

    static void quickSort( long A[ ] ,int start , int end ) {
        if( start < end ) {
            //stores the position of pivot element
            int piv_pos = partition(A,start , end ) ;
            quickSort(A,start , piv_pos -1);    //sorts the left side of pivot.
            quickSort( A,piv_pos +1 , end) ; //sorts the right side of pivot.
        }
    }

    private static  void quickSortRand(long[] A, int low, int high) {
        if (low < high+1) {
            int p = partitionRand(A, low, high);
            quickSortRand(A, low, p-1);
            quickSortRand(A, p+1, high);
        }
    }

    private static void swap(long[] A, int index1, int index2) {
        long temp = A[index1];
        A[index1] = A[index2];
        A[index2] = temp;
    }

    // returns random pivot index between low and high inclusive.
    private static int getPivot(int low, int high) {
        Random rand = new Random();
        return rand.nextInt((high - low) + 1) + low;
    }

    // moves all n < pivot to left of pivot and all n > pivot
    // to right of pivot, then returns pivot index.
    private static int partitionRand(long[] A, int low, int high) {
        swap(A, low, getPivot(low, high));
        int border = low + 1;
        for (int i = border; i <= high; i++) {
            if (A[i] < A[low]) {
                swap(A, i, border++);
            }
        }
        swap(A, low, border-1);
        return border-1;
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
            long[] a = new long[N / 2];
            long[] b = new long[N - N / 2];
            for (int i = 0; i < a.length; i++)
                a[i] = input[i];
            for (int i = 0; i < b.length; i++)
                b[i] = input[i + N / 2];
            return merge(mergeSort(a), mergeSort(b));
        }

    public static void checkSortedCorrectness(){
        System.out.println("Quick Sort(naive) verification: ");
        System.out.println("");
        long[] testList = createRandomIntegerList(10);
        long[] largeList1 = createRandomIntegerList(100);
        //long [] largeList1Sorted = bubbleSort(largeList1);
        quickSort(largeList1, 0, largeList1.length-1);
        long[] largeList2 = createRandomIntegerList(250);
        //long [] largeList2Sorted = bubbleSort(largeList2);
        quickSort(largeList2, 0, largeList2.length-1);
        long[] largeList3 = createRandomIntegerList(500);
        //long [] largeList3Sorted = bubbleSort(largeList3);
        quickSort(largeList3, 0, largeList3.length-1);
        System.out.println("Unsorted");
        System.out.println(Arrays.toString(testList));
        quickSort(testList, 0, testList.length-1);
        //long [] testListSorted = bubbleSort(testList);
        System.out.println("Sorted");
        System.out.println(Arrays.toString(testList));
        System.out.println("");
        System.out.println("Verifying lists of size 100, 250, and 500 random integers are sorted: ");
        System.out.println("Result for list size 10: " + isSorted(testList));
        System.out.println("Result for list size 100: " + isSorted(largeList1));
        System.out.println("Result for list size 250: " + isSorted(largeList2));
        System.out.println("Result for list size 500: " + isSorted(largeList3));
    }

        public static long[] bubbleSort(long[] list) {
            int n = list.length;
            for (int i = 0; i < n - 1; i++) {
                for (int j = 0; j < n - 1 - i; j++) {
                    if (list[j] > list[j + 1]) {
                        long tmp = list[j];
                        list[j] = list[j + 1];
                        list[j + 1] = tmp;
                    }
                }
            }
            return list;
        }

        public static long[] insertionSort(long[] vals) {
            int currentIndex = -1;
            long temp = 0;

            for(int pos = 1; pos < vals.length; pos++){
                currentIndex = pos;

                while(currentIndex > 0 && (vals[currentIndex] < vals[currentIndex-1]) ){
                    temp = vals[currentIndex];
                    vals[currentIndex] = vals[currentIndex-1];
                    vals[currentIndex-1] = temp;
                    currentIndex--;
                }
            }
            return vals;
        }

        public static long[] createRandomIntegerList(int size) {
            long[] newList = new long[size];

            for (int i = 0; i < size; i++) {
                newList[i] = (long) (MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
            }
            return newList;
        }

        public static boolean isSorted(long[] list){
            int n = list.length;

            for(int i=0; i<n-1; i++){
                if(list[i] > list[i+1]) {
                    return false;
                }
            }
            return true;
        }

    }



