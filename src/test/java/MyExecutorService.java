import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyExecutorService {

    private final BlockingQueue<Runnable> queue;

    private final Thread thread;

    public MyExecutorService() {
        queue = new LinkedBlockingQueue<Runnable>();
        thread = new Thread(() -> {
            while (true) {
                try {
                    Runnable runnable = (Runnable) queue.take();
                    runnable.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "myExecutorService");
        thread.start();
    }

    public void submit(Runnable runnable) {
        this.queue.offer(runnable);
    }


    public static void main(String[] args) {
        MyExecutorService executorService = new MyExecutorService();
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int finalI = i;
                executorService.submit(() -> System.out.println(Thread.currentThread().getName() + ":" + finalI));
            }
        }, "thread1");

        Thread thread2 = new Thread(() -> {
            for (int i = 10; i < 20; i++) {
                int finalI = i;
                executorService.submit(() -> System.out.println(Thread.currentThread().getName() + ":" + finalI));
            }
        }, "thread2");
        thread1.start();
        thread2.start();
    }
}
