package com.csc482;
import java.lang.management.*;
import java.util.*;

public class whatsupdoc {
    public static void main(String[] args) {
        long timeStampBefore = getCpuTime();
        System.out.println("What's up, Doc?");
        long timeStampAfter = getCpuTime();

        System.out.println("Total CPU time = " + (timeStampAfter-timeStampBefore) + " nanoseconds");
    }

    /** Get CPU time in nanoseconds since the program(thread) started. (You want only the time spent running THIS program, not everything else CPU is running in background*/
    /** from: http://nadeausoftware.com/articles/2008/03/java_tip_how_get_cpu_and_user_time_benchmarking#TimingasinglethreadedtaskusingCPUsystemandusertime **/

    public static long getCpuTime( ) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

        return bean.isCurrentThreadCpuTimeSupported( ) ?
                bean.getCurrentThreadCpuTime( ) : 0L;

    }
}
