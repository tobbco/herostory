import java.util.concurrent.atomic.AtomicInteger;

public class TestHero {

    private Integer  hp = 100;

    public synchronized void subHp(Integer hp) {
        if (this.hp <= 0) {
            return;
        }
        this.hp -= hp;
    }

    public synchronized void attack(TestHero hero) {
        int damage = 10;
        hero.subHp(damage);
    }
}
