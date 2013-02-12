import java.util.concurrent.Semaphore;

public class CyclicBarrier {
    static final int NUM_THREADS = 100,
                     THREAD_RUNS = 100;

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(NUM_THREADS);
        
        for (int i = 0; i < NUM_THREADS; i++) {
            new Thread(new Worker(barrier, i, THREAD_RUNS)).start();
        }
    }
    
    int parties, indexCounter, level, contributerCounter;
    Semaphore indexCounterLock, contributerCounterLock, levelLock;
    
    public CyclicBarrier(int parties) {
        this.parties = parties;
        indexCounter = 0;
        level = 0;
        contributerCounter = 0;
        
        indexCounterLock = new Semaphore(1);
        contributerCounterLock = new Semaphore(1);
        levelLock = new Semaphore(1);
    }
    
    private int getIndex() throws InterruptedException {
        indexCounterLock.acquire();
        
        int index = indexCounter;
        indexCounter += 1;
        
        indexCounterLock.release();
        
        return index;
    }
    
    // wait till all threads from previous wave raise the level
    private void waitForPreviousLevel(int index) throws InterruptedException {
        while (true) {
            levelLock.acquire();
            
            // only when all threads of the past level have executed the 
            //  "IMPORTANT" step will this condition be true
            // and it will not become false until all threads of a future level 
            //  execute their "IMPORTANT" step 
            // (the "IMPORTANT" step is the only place where level can increase)
            if (level == index/parties) {
                break;
            }  
            
            levelLock.release();
        } 
        levelLock.release();
    }
    
    // wait for peers to collectively raise the level before proceeding
    private void waitForPeers(int index, int myLevel) throws InterruptedException {
        while (true) {
            indexCounterLock.acquire();
            
            // only when all threads of the same level have executed their
            //  getIndex() step will this condition be true
            // and it will not become false until all threads of the future level
            //  call getIndex()
            if (indexCounter/parties == myLevel + 1) { 
                break;
            }  
            
            indexCounterLock.release();
        } 
        indexCounterLock.release();
    }
    
    private void contributeToRaisingLevel() throws InterruptedException {
        contributerCounterLock.acquire();
        
        contributerCounter += 1;
        
        levelLock.acquire();
        level = contributerCounter/parties;
        levelLock.release();
        
        contributerCounterLock.release();
    }
    
    public int await() throws InterruptedException {
        int index = getIndex();
        
        waitForPreviousLevel(index);
        waitForPeers(index, level);
        
        contributeToRaisingLevel(); // "IMPORTANT" step
        
        return parties - (index%parties) - 1;
    } 
}

// for testing purposes

class Worker implements Runnable {
    CyclicBarrier barrier;
    int runs, id;
    
    public Worker (CyclicBarrier barrier, int id, int runs) {
        this.barrier = barrier;
        this.id = id;
        this.runs = runs;
    }
    
    private void doSomething(int iteration) {
        System.out.printf("thread %3d on iteration %3d\n", id, iteration);
    }
    
    public void run() {
        for (int i = 0; i < runs; i++) {
            doSomething(i);
            try {
                int index = barrier.await();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}












































