import java.util.concurrent.Semaphore

public class CyclicBarrier {
	public static void main(String[] args) {
		System.out.println("hi!");
	}

	int parties,
	    currentParties;

	Semaphore partyLock;
	
	public CyclicBarrier(int parties) {
		this.parties = parties;
		this.currentParties = parties;
		
		this.partyLock = new Semaphore(1);
	}
	
	public int await() throws InterruptedException {
		boolean lastThread = false;

		partyLock.acquire();

		if (this.currentParties == this.parties) {
			continueLock.acquire();
		} else if (this.currentParties == 1) {
			lastThread = true;
		}

		this.currentParties -= 1;
		int partyIndex = currentParties;

		partyLock.release();

		if (lastThread) {
			continueLock.release();
		} else {
			continueLock.acquire();
			continueLock.release();
		}

		return partyIndex;
	}

	static class Worker implements Runnable {
		CyclicBarrier barrier;
		public Worker(CyclicBarrier barrier) {
			this.barrier = barrier;
		}
		public void run() {
			while (true) {
				// do something
				barrier.await();
			}
		}
	}
}
