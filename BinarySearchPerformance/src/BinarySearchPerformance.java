import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class BinarySearchPerformance {
        static long MAXVALUE = 2000000000;
        static long MINVALUE = -2000000000;
        static int MINSIZE = 1000;
        static int SIZEINCREMENT = 10000;
        static long MAXSIZE = 10000000;
        static long numberOfTrials = 20;

        public static void main(String[] args) {
            /*
            binarySearch verification

            long[] demoList = {-12432398, -4243526, -123235, 0, 12, 234, 670, 789, 3563456, 13289745};
            System.out.println("To find = " + 12 + "," +  " Result = " + binarySearch(12, demoList));
            System.out.println("");
            System.out.println("To find = " + -12432398 + "," + " Result = " + binarySearch(-12432398, demoList));
            System.out.println("");
            System.out.println("To find = " + 789 + "," + " Result = " + binarySearch(789, demoList));
            System.out.println("");
            System.out.println("To find = " + 13289745 + "," + " Result = " + binarySearch(13289745, demoList));
            System.out.println("");
            System.out.println("To find = " + 777 + "," + " Result = " + binarySearch(777, demoList));

            */
            //long[] demoList2 = createAscendingList(50);
            //printList(demoList2);

            System.out.println("InputSize    AvgTime    TotalTimeForAllTrials");

            /*for each size of input we want to test..... */
            for(int inputSize=MINSIZE; inputSize<=MAXSIZE; inputSize += MINSIZE * 20000){
                /* repeat for desired number of trials.... (for a specific size of input)*/
                long totalTime = 0;
                for (long trial = 0; trial < numberOfTrials; trial++){
                    /*for one trial: */
                    /*generate ascending list of desired size (a list of N ascending numbers) */
                    long[] testList = createAscendingList(inputSize);
                    long testSearchNumber = (long)(Math.random() * 10);

                    /*run the trial */
                    long timeStampBefore = getCpuTime();

                    /* apply the test function to the test input*/
                    long found = binarySearch(testSearchNumber, testList);

                    /*get "after" timestamp, calculate trial time */
                    long timeStampAfter = getCpuTime();
                    long trialTime = timeStampAfter - timeStampBefore;

                    totalTime = totalTime + trialTime;
                }
                double averageTime = (double)totalTime / (double)numberOfTrials;
                System.out.println(inputSize + "     " + averageTime + "      " + totalTime);
            }
        }

        public static void printList(long[] numList) {
            for(int i=0; i<numList.length; i++){
                System.out.print(numList[i] + "  ");
            }
        }

        public static long[] createAscendingList(int size) {
            long[] newList = new long[size];
            newList[0] = 12;

            for(int i=1; i<size; i++) {
                newList[i] = newList[i-1] + ((long)(Math.random() * 10));
            }
            return newList;
        }

        public static long binarySearch(long toFind, long[] numList) {
            long low = 0;
            long high = numList.length-1;
            long mid = Integer.MAX_VALUE;

            /*
            System.out.print("List: [  ");
            printList(numList);
            System.out.print(" ]");
            System.out.println("");
            */

            while(low <= high){
                mid = low + (high-low)/2; //the reason this search is so efficient, the list is halved with each iteration

                if(numList[(int)mid] < toFind){
                    low = mid + 1; //cut array in half, focus on right side
                }else if(numList[(int)mid] > toFind){
                    high = mid - 1; //cut array in half, focus on the left side
                }else{
                    return mid; //value found
                }
            }
            return -1; //value not found
        }

        /** Get CPU time in nanoseconds since the program (thread) started. */
        /** from: http://nadeausoftware.com/articles/2008/03
         * /java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/
        public static long getCpuTime( ) {
            ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

            return bean.isCurrentThreadCpuTimeSupported( ) ?
                    bean.getCurrentThreadCpuTime( ) : 0L;

        }
}
