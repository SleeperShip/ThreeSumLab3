import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Random;

public class Lab5Fibonacci {

        static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        /* define constants */
        static int numberOfTrials = 50;
        static int MAXINPUTSIZE = 128;
        static int MININPUTSIZE = 2;

        static String ResultsFolderPath = "/home/jon/Results/Lab5/"; // pathname to results folder
        static FileWriter resultsFile;
        static PrintWriter resultsWriter;

        public static void main(String[] args) {

            // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
            runFullExperiment("FibLoop-Exp1-ThrowAway.txt");

            runFullExperiment("FibLoop-Exp2.txt");

            runFullExperiment("FibLoop-Exp3.txt");

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

            resultsWriter.println("#InputSize    AverageTime    NumberOfBits"); // # marks a comment in gnuplot data
            resultsWriter.flush();

            /* for each size of input we want to test: in this case starting small and doubling the size each time */
            for (int inputSize = MININPUTSIZE; inputSize <= MAXINPUTSIZE; inputSize += 1) {

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

                //System.out.print("    Generating test data...");
                //long[] testList = createRandomIntegerList(inputSize);
                //System.out.println("...done.");

                System.out.print("    Running trial batch...");

                // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
                // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
                // stopwatch methods themselves
                // BatchStopwatchchStopwatch.start(); // comment this line if timing trials individually


                // run the trials
                for (long trial = 0; trial < numberOfTrials; trial++) {
                    /* force garbage collection before each trial run so it is not included in the time */
                    System.gc();
                    TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                    /* run the function we're testing on the trial input */
                    //long[] fibSequence = new long[inputSize+2];
                    FibLoop(inputSize);

                    batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
                }

                //batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
                double averageTimePerTrialInBatch = (double) batchElapsedTime / (double) numberOfTrials; // calculate the average time per trial in this batch
                long numBits = FibLoop(countBits(inputSize));

                /* print data for this size of input */
                resultsWriter.printf("%12d  %15.2f  %12d\n", inputSize, averageTimePerTrialInBatch, numBits); // might as well make the columns look nice
                resultsWriter.flush();
                System.out.println(" ....done.");
            }
        }

        public static long FibRecur(long x) { //basic recursive version based on mathematical definition
            if(x<=1){
                return x;
            }

            return FibRecur(x-1) + FibRecur(x-2); //Very slow as the number of stack frames grows quickly
        }

        public static long FibRecurDP(long x, long[] fib) { //linear in run-time and order of growth in memory
            if(x<=1){
                return x;
            }

            if(fib[(int)x] != 0){  //tracks all previous Fibonacci numbers calculated so far
                return fib[(int)x];  //checks to see if previous results is already in table
            }else{
                fib[(int)x] = FibRecurDP(x-1, fib) + FibRecurDP(x-2, fib);  //otherwise recur
                return fib[(int)x];
            }
        }

        public static long FibLoop(long x) { //further optimized version of FibRecurDP(), reducing memory order of growth from linear to constant
            long a = 0, b = 1, c;
            if (x <= x)
                return x;

            for (int i = 2; i <= x; i++) {
                c = a + b; //Only the "current" Fibonacci value and the previous two in the sequence are stored (as opposed to the entire sequence in FibRecurDP)
                a = b;
                b = c;
            }
            System.out.println("Fib number =  " + b);
            return b;
        }

        public static long FibMatrix(long x) { //recursively calls MatrixPower since this technique uses matrix multiplication
            long F[][] = new long[][]{{1,1},{1,0}};
            if (x == 0)
                return 0;
            MatrixPower(F, x-1); //multiply the matrix F = {{1,1},{1,0}} by itself, get the (n+1)th Fibonacci number as the element at (0, 0) in the resultant matrix

            return F[0][0];
        }

        static void MatrixPower(long F[][], long x) {
            if(x == 0 || x == 1)
                return;
            long M[][] = new long[][]{{1,1},{1,0}};

            MatrixPower(F, x/2); //breaks problem into simpler chunks, calculates [matrix]^y/2 power and uses it to later calculate x^2, x^4, x^8, ... quickly
            matrixMultiply(F, F); //Raise a matrix to a power by multiplying it by itself

            if (x%2 != 0) //to handle odd-numbered cases
                matrixMultiply(F, M);
        }

        static void matrixMultiply(long F[][], long M[][]) { //multiplies two matrices using a bit of algebra (2x2 in dimension)
                long x =  F[0][0]*M[0][0] + F[0][1]*M[1][0];
                long y =  F[0][0]*M[0][1] + F[0][1]*M[1][1];
                long z =  F[1][0]*M[0][0] + F[1][1]*M[1][0];
                long w =  F[1][0]*M[0][1] + F[1][1]*M[1][1];

                F[0][0] = x;
                F[0][1] = y;
                F[1][0] = z;
                F[1][1] = w;
        }

        static int countBits(long number) {
            // log function in base 2
            // take only integer part
            return (int)(Math.log(number) /
                    Math.log(2) + 1);
        }
}

