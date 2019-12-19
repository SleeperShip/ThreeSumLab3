import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.Random;

public class Lab8_TSP {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean();

    /* define constants */

    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 50;
    static int MAXINPUTSIZE = (int) Math.pow(2, 24);
    static int MININPUTSIZE = 2;

    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/jon/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;

    public static void main(String[] args) {

        //checkSortedCorrectness();

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        //runFullExperiment("BubbleSort-Exp1-ThrowAway.txt");

        //runFullExperiment("BubbleSort-Exp2.txt");

        //runFullExperiment("BubbleSort-Exp3.txt");

        double[][] someGraph = generateRandomCircularGraphCostMatrix(10, 100);
        print2DArray(someGraph);
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
            //long[] testList = createRandomIntegerList(inputSize);
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
                System.gc();
                TrialStopwatch.start(); // *** uncomment this line if timing trials individually

                /* run the function we're testing on the trial input */

                //quickSortRand(testList, 0, testList.length-1);
                //long[] sortedTestList = bubbleSort((testList));

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

    public static void print2DArray(int[][] someArray) {
        for(int i=0; i<someArray.length-1; i++){
            for(int j=0; j<someArray[j].length-1; j++){
                System.out.print(someArray[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public static void print2DArray(double[][] someArray) {
        for(int i=0; i<someArray.length-1; i++){
            for(int j=0; j<someArray[j].length-1; j++){
                System.out.print(someArray[i][j] + " ");
            }
            System.out.println("");
        }
    }

    public static int[][] generateRandomCostMatrix(int numNodes, int maxEdgeCost) {   //be sure to enter number of nodes you want plus one
        int[][] graph = new int[numNodes][numNodes];
        int randomNum = -1;

        for(int i=0; i<numNodes; i++){
            for(int j=0; j<numNodes; j++){
                randomNum = (int)(Math.random() * ((maxEdgeCost - 0) + 1));
                if(i == j) {
                    graph[i][j] = 0;
                }else{
                    graph[i][j] = randomNum;
                }
            }
        }

        for(int i=0; i<numNodes; i++){
            for(int j=0; j<numNodes; j++){
                if(graph[i][j] != graph[j][i]){
                    graph[j][i] = graph[i][j];
                }
            }
        }
        return graph;
    }

    public static double[][] generateRandomEuclideanCostMatrix(int numNodes, int maxXYCoords) {
        double[][] eucCostMatrix = new double[numNodes][numNodes];
        int randX1 = 0;
        int randY1 = 0;
        int randX2 = 0;
        int randY2 = 0;
        double distance = 0.0;


        for(int i=0; i<numNodes; i++){ //create nodes along first row and column of 2D array
            eucCostMatrix[i][0] = i;
        }

        for(int j=0; j<numNodes; j++){
            eucCostMatrix[0][j] = j;
        }

        for(int i=0; i<numNodes; i++){

            randX1 = (int)(Math.random() * ((maxXYCoords - 0) + 1));
            randY1 = (int)(Math.random() * ((maxXYCoords - 0) + 1));
            randX2 = (int)(Math.random() * ((maxXYCoords - 0) + 1));
            randY2 = (int)(Math.random() * ((maxXYCoords - 0) + 1));
            distance = (double)Math.sqrt(Math.pow((randX2-randX1) , 2) + Math.pow((randY2-randY1) ,2));

            for(int j=0; j<numNodes; j++){
                if(i == j) {
                    eucCostMatrix[i][j] = 0.0;
                }else{
                    eucCostMatrix[i][j] = distance;
                }
            }
        }

        for(int i=0; i<numNodes; i++){      //make the cost matrix symmetric
            for(int j=0; j<numNodes; j++){
                if(eucCostMatrix[i][j] != eucCostMatrix[j][i]){
                    eucCostMatrix[j][i] = eucCostMatrix[i][j];
                }
            }
        }
        return eucCostMatrix;
    }

    public static double[][] generateRandomCircularGraphCostMatrix(int numNodes, double radius) {
        double[][] circularCostMatrix = new double[numNodes][numNodes];
        double stepAngle = (2 * Math.PI) / numNodes;
        double xCoord = 0.0;
        double yCoord = 0.0;
        double distance = 0.0;

        for(int i=0; i<numNodes; i++){
            xCoord = radius * Math.sin(i * stepAngle);
            yCoord = radius * Math.cos(i * stepAngle);
            distance = (double)Math.sqrt(Math.pow(xCoord , 2) + Math.pow(yCoord ,2));

            for(int j=0; j<numNodes; j++){
                circularCostMatrix[i][j] = distance;
            }
        }
        return circularCostMatrix;
    }

}
