import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 自动检测死锁
 */
public class DeadlockDetectorScheduler {
    public static void main(String[] args) {
        // 创建一个定时任务调度器
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 定义死锁检测任务
        Runnable deadlockDetectionTask = () -> {
            ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
            long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
            if (deadlockedThreads != null) {
                System.out.println("检测到死锁，涉及的线程信息如下：");
                // 获取所有线程的信息
                ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(deadlockedThreads, true, true);
                for (ThreadInfo threadInfo : threadInfos) {
                    if (threadInfo != null) {
                        System.out.println("进程ID: " + getProcessId());
                        System.out.println("线程ID: " + threadInfo.getThreadId());
                        System.out.println("线程名称: " + threadInfo.getThreadName());
                        System.out.println("栈信息: ");
                        List<StackTraceElement> stackTrace = Arrays.asList(threadInfo.getStackTrace());
                        stackTrace.forEach(stackElement -> System.out.println("\t" + stackElement.toString()));
                    }
                }
            } else {
                System.out.println("未检测到死锁。");
            }
        };

        // 定时执行死锁检测任务，每5秒执行一次
        scheduler.scheduleAtFixedRate(deadlockDetectionTask, 0, 5, TimeUnit.SECONDS);
    }

    /**
     * 从Java 9开始，可以通过 ProcessHandle 类来获取当前进程的ID。对于Java 8及以下版本，可以通过解析 ManagementFactory.getRuntimeMXBean().getName() 的返回值来间接获取进程ID。
     *
     */
    private static long getProcessId() {
        long pid = ProcessHandle.current().pid();
        System.out.println("pid:"+pid);
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(jvmName.split("@")[0]);
    }
}