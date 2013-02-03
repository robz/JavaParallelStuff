import java.util.concurrent.*;

public class PSearch implements Callable<Integer> {
	int x, start, end;
	int[] A;

	public PSearch(int x, int[] A, int start, int end) {
		this.x = x;
		this.A = A;
		this.start = start;
		this.end = end;
	}

	public static void main(String[] args) {
		int[] arr = new int[10000];
		int index = genArr(3, arr);
		int res = parallelSearch(3, arr, 5);
		
		if (index != res) {
			System.out.println(index+" vs "+res);
		} else {
			System.out.println("good to go");
		}
	}

	public static int genArr(int x, int[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = (int)(Math.random()*arr.length);
			if (arr[i] == x) {
				arr[i] = x + 1;
			}
		} 

		int index = (int)(Math.random()*arr.length);
		arr[index] = x;

		return index;
	}

	public static int parallelSearch(int x, int[] A, int numThreads) {
		ExecutorService pool = Executors.newFixedThreadPool(numThreads);
		Future f[] = new Future[numThreads];
		
		int inc = A.length/numThreads + 1,
		    end = 0;

		for (int i = 0; i < numThreads; i++) {
			int start = end;
			end = start + inc;

			if (end > A.length) {
				end = A.length;
			}

			f[i] = pool.submit(new PSearch(x, A, start, end));
		}

		for (int i = 0; i < numThreads; i++) {
			Integer res = null;

			try {
				res = (Integer) f[i].get();
			} catch(Exception ex) {
				System.out.println("yo dawg we just got interrupted!");
			}

			if (res != -1) {
				pool.shutdown();	
				return res;
			}
		}

		pool.shutdown();
		return -1;
	}

	public Integer call() {
		return sequentialSearch(this.x, this.A, this.start, this.end);
	}

	public Integer sequentialSearch(int x, int[] A, int start, int end) {
		for (int i = start; i < end; i++) {
			if (A[i] == x) {
				return i;
			}
		}

		return -1;
	}
}
