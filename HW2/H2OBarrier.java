import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class H2OBarrier {
    ReentrantLock lock;
    Condition condition;
    int hydrogen_needed, oxygen_needed;

    public static void main(String[] args) {
        H2OBarrier h = new H2OBarrier();
    }

    public H2OBarrier() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void HReady() {
        lock.lock();

        try {
            while (0 == hydrogen_needed) {
                condition.await();
            } 

            hydrogen_needed -= 1;
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void OReady() {
        lock.lock();

        try {
            while (0 == oxygen_needed) {
                condition.await();
            }

            oxygen_needed -= 1;            
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public void makeWater() {
        lock.lock();

        try {
            hydrogen_needed += 2;
            oxygen_needed += 1;

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
