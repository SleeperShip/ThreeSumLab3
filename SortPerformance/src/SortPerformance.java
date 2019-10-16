import java.lang.management.*;
import java.util.Arrays;

public class SortPerformance
{
    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int MINSIZE = 100;
    static int SIZEINCREMENT = MINSIZE * 100;
    static int MAXSIZE = MINSIZE*1000;
    static long numberOfTrials = 20;

    public static void main(String[] args) {
        long[] testList = createRandomIntegerList(10);
        System.out.println("Unsorted");
        System.out.println(Arrays.toString(testList));
        bubbleSortNumberList(testList);
        System.out.println("Sorted");
        System.out.println(Arrays.toString(testList));
        System.out.println("Input size   Average Time    Total Time");
        //for each size of input we want to test
        for(int inputSize = MINSIZE; inputSize <= MAXSIZE; inputSize+=SIZEINCREMENT) {
            long totalTime = 0;
            //repeat for desired number of trials
            for (long trial = 0; trial < numberOfTrials; trial++) {
                //FOR ONE TRIAL
                /* generate list of n elements */
                testList = createRandomIntegerList(inputSize);
                /* run the test */
                long timeStampBefore = getCpuTime(); /*get before time stamp*/
                //apply test function to test input
                bubbleSortNumberList(testList);
                long timeStampAfter = getCpuTime(); // get after time stamp
                long trialTime = timeStampAfter - timeStampBefore; // calculate time

                totalTime += trialTime;
            }
            double averageTime = (double)totalTime/(double)numberOfTrials;
            System.out.println(inputSize + "   " + averageTime + "    " + totalTime);
        }
    }

    public static long[] createRandomIntegerList(int size) {
        long[] newList = new long[size];
        for(int j = 0; j< size; j++) {
            newList[j] = (long)(MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }

    public static void bubbleSortNumberList(long[] list){
        int n = list.length;
        for(int i = 0; i < n-1; i++) {
            for (int j = 0; j < n-1 -i;j++) {
                if(list[j]> list[j+1]) {
                    long tmp = list[j];
                    list[j] = list[j+1];
                    list[j+1]= tmp;
                }
            }
        }

    }

/** Get CPU time in nanoseconds since the program(thread) started. */
/**
     * from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime
     **/

    public static long getCpuTime() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();

        return bean.isCurrentThreadCpuTimeSupported() ?
                bean.getCurrentThreadCpuTime() : 0L;
    }
}