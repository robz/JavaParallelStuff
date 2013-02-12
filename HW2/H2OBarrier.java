import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class H2OBarrier {
    static int NUM_ATOMS = 10;

    ReentrantLock lock;
    Condition condition;
    int hydrogen_ready, oxygen_ready;

    public static void main(String[] args) {
        testBarrier();
    }

    public H2OBarrier() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void HReady() {
        lock.lock();

        try {
            hydrogen_ready += 1;

            while (!((2 <=  hydrogen_ready && 1 <= oxygen_ready)
                  || (0 == hydrogen_needed))) {
                condition.await();
            } 

            hydrogen_ready -= 1;
            hydrogen_needed -= 1;
            makeWater();
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

    // no lock needed because this can only be called from OReady or HReady, right?
    // (so why is this a public function?)
    public void makeWater() {
        hydrogen_needed += 2;
        oxygen_needed += 1;
        condition.signalAll();
    }

    static void testBarrier() {
        H2OBarrier barrier = new H2OBarrier();
        
        for (int i = 0; i < NUM_ATOMS; i++) {
            new Thread(new Atom(Atom.randomAtomType(), barrier)).start();
        }

        for (int i = 0; i < NUM_ATOMS; i += 3) {
            try { Thread.sleep(1000); } catch (Exception ex) { ex.printStackTrace(); }
            System.out.println();
            barrier.makeWater();
        }
    }

    static class Atom implements Runnable {
        static int global_id = 0;
        
        H2OBarrier barrier;
        int type, id;        

        public Atom(int type, H2OBarrier barrier) {
            this.type = type;
            this.barrier = barrier;
            this.id = global_id++;
        }

        public void run() {
            if (HYDROGEN == type) {
                System.out.printf("Hydrogen %d ready...\n", id);
                barrier.HReady();
                System.out.printf("Hydrogen %d bonding!\n", id);
            } else if (OXYGEN == type) {
                System.out.printf("Oxygen %d ready...\n", id);
                barrier.OReady();
                System.out.printf("Oxygen %d bonding!\n", id);
            }
        }

        static final int HYDROGEN = 0,
                         OXYGEN = 1;

        public static int randomAtomType() {
            double random = Math.random();
            if (random < .66) {
                return HYDROGEN;
            } else {
                return OXYGEN;
            }
        }
    }
}
