public class PSort implements Runnable {
	static int total_threads = 0,
		   num_threads = 0,
		   max_threads = -1;

	public static void main(String[] args) {
		int[] arr = new int[10000];
		genArr(arr, 0, 10000);
	
		// printArr(arr, 0, arr.length);	
		parallelSort(arr, 0, arr.length);
		// printArr(arr, 0, arr.length);
		System.out.println("sorted correctly: "+isSorted(arr));
		System.out.println("most threads active: "+max_threads);
		System.out.println("total threads created: "+total_threads);
	}

	public static void genArr(int[] arr, int min, int max) {
		int range = max - min + 1;

		for (int i = 0; i < arr.length; i++) {
			arr[i] = (int)(Math.random()*range + min);
		}
	}

	public static boolean isSorted(int[] arr) {
		for (int i = 0; i < arr.length - 1; i++) {
			if (arr[i] > arr[i+1]) {
				return false;
			}
		}

		return true;
	}

	public static void printArr(int[] arr, int begin, int end) {
		String str = "[", 
		       prefix = "";

		for (int i = begin; i < end; i++) {
			str += prefix + arr[i];
			prefix = ", ";	
		}

		System.out.println(str + "]");
	}

	int[] A;
	int begin, end;

	public PSort(int[] A, int begin, int end) {
		this.A = A;
		this.begin = begin;
		this.end = end;
	}

	public synchronized void count() {
		num_threads++;
		total_threads++;
		if (num_threads > max_threads) {
			max_threads = num_threads;
		}
	}

	public synchronized void substract() {
		num_threads--;
	}

	public void run() {
		count();
		parallelSort(this.A, this.begin, this.end);
		substract();
	}

	public static void parallelSort(int[] A, int begin, int end) {
		if (begin == end || begin + 1 == end) {
			return;
		}

		int[] tmp_arr = new int[end - begin];
		
		int pivot = median(A[begin], A[end - 1], A[(begin + end)/2]),
			front_index = 0, 
			back_index = end - begin - 1;
		
		boolean flag = false;	
	
		for (int i = begin; i < end; i++) {
			if (A[i] < pivot) {
				tmp_arr[front_index] = A[i];
				front_index++;
			} else if (A[i] > pivot) {
				tmp_arr[back_index] = A[i];
				back_index--;
			} else {
				if (flag) {
					tmp_arr[front_index] = A[i];
					front_index++;
				} else {
					tmp_arr[back_index] = A[i];
					back_index--;
				}

				flag = !flag;
			}
		}
		
		for (int i = begin; i < end; i++) {
			A[i] = tmp_arr[i - begin];
		}
		
		Thread thread = new Thread(new PSort(A, begin, begin + front_index));
		thread.start();

		parallelSort(A, begin + front_index, end);
		
		try {		
			thread.join();
		} catch (InterruptedException ex) {
			System.out.println("whoa whoa hay we gotz interrupted!");
		}
	}

	public static void sequentialSort(int[] A, int begin, int end) {
		if (begin == end || begin + 1 == end) {
			return;
		}

		int[] tmp_arr = new int[end - begin];
		
		int pivot = median(A[begin], A[end - 1], A[(begin + end)/2]),
			front_index = 0, 
			back_index = end - begin - 1;
		
		boolean flag = false;	
	
		for (int i = begin; i < end; i++) {
			if (A[i] < pivot) {
				tmp_arr[front_index] = A[i];
				front_index++;
			} else if (A[i] > pivot) {
				tmp_arr[back_index] = A[i];
				back_index--;
			} else {
				if (flag) {
					tmp_arr[front_index] = A[i];
					front_index++;
				} else {
					tmp_arr[back_index] = A[i];
					back_index--;
				}

				flag = !flag;
			}
		}
		
		for (int i = begin; i < end; i++) {
			A[i] = tmp_arr[i - begin];
		}
		
		sequentialSort(A, begin, begin + front_index);
		sequentialSort(A, begin + front_index, end);
	}
	
	public static int median(int val1, int val2, int val3) {
		if (val1 > val2) {
		  if (val2 > val3) {
			return val2;
		  } else if (val1 > val3) {
			return val3;
		  } else {
			return val1;
		  }
		} else {
		  if (val1 > val3) {
			return val1;
		  } else if (val2 > val3) {
			return val3;
		  } else {
			return val2;
		  }
		}
	}
}
