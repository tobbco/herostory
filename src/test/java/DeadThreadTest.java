import org.herostory.model.Hero;
import org.junit.jupiter.api.Test;

public class DeadThreadTest {


    @Test
    public void test1() {
        DeadlockDetectorScheduler.main(null);
        for (int i = 0; i < 10000; i++) {
            TestHero hero1 = new TestHero();
            TestHero hero2 = new TestHero();

            Thread thread1 = new Thread(() -> {
                hero1.attack(hero2);

            });
            thread1.start();
            Thread thread2 = new Thread(() -> {
                hero2.attack(hero1);
            });
            thread2.start();
            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("第 " + i + "次互砍结束");
        }

    }
}
