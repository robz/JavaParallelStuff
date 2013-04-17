import java.net.Socket;
import java.util.PriorityQueue;

class StrPtr {
	public String str;
}

class Clock {
	int clock = 0;
	
	public void tick() {
		clock += 1;
	}
	
	public void update(int otherClock) {
		clock = 1 + ((otherClock > clock) ? otherClock : clock);
	}
}

class Request implements Comparable<Request> {
	String id;
	int clockval;
	
	public Request(String id, int clockval) {
		this.clockval = clockval;
		this.id = id;
	}
	
	public String toString() {
		return "id: " + this.id + "; clock:" + this.clockval;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Request) {
			Request req = (Request)obj;
			return req.id.equals(this.id) && req.clockval == this.clockval;
		}
		
		return false;
	}

	@Override
	public int compareTo(Request arg0) {
		int clockdif = this.clockval - arg0.clockval;
		
		if (clockdif != 0) {
			return clockdif;
		}
		
		return this.id.compareTo(arg0.id);
	}
}

public class LamportInterface {
	static String NOOP_MSG = "no-op",
			      SERVER_TIMEOUT_MSG = "error: server timed out!";
	
	Database database;
	PriorityQueue<Request> requestQueue;
	NetworkInterface network;
	String id;
	Clock clock;
	
	public LamportInterface(Database database, NetworkInterface network, String id) {
		this.database = database;
		this.requestQueue = new PriorityQueue<Request>();
		this.network = network;
		this.id = id;
		this.clock = new Clock();
	}
	
	// deals with other servers to process request, updates database, returns error message
	public synchronized String sendRequest(String clientMsg) {
		// register the event of receiving a client message
		clock.tick(); 
		
		// Enters its request in its own queue (ordered by time stamps)
		Request request = new Request(id, clock.clock);
		requestQueue.add(request);
		
		// Sends a request to every node & wait for replies
		boolean success = network.broadcastRequest(clock, request);
		
		if (!success) {
			// TODO: ignore timeout error, or return error?
			// return SERVER_TIMEOUT_MSG;
		}
		
		// If own request is at the head of the queue, enter critical section
		while (!requestQueue.peek().equals(request)) {
			System.out.println("this is not our request: " + request.toString());
			try {
				wait();
			} catch (InterruptedException ex) {
				System.out.println("whoa dude interrupted while waiting wtf?");
			}
		}
		
		requestQueue.remove();
		
		StrPtr returnMsg = new StrPtr();
		success = database.parseMessage(clientMsg, returnMsg);
		
		// Upon exiting the critical section, send a release message to every process.
		if (success) {
			// send client's message to other servers in release message
			network.broadcastRelease(clock, request, clientMsg);
		} else {
			// send no-op message to other servers in release message
			network.broadcastRelease(clock, request, NOOP_MSG);
		}
		
		return returnMsg.str;
	}
	
	public synchronized void handleRequest(Request request, Socket socket) {
		// update lamport clock
		clock.update(request.clockval);
		
		// After receiving a request, enter the request in the request queue 
		// (ordered by time stamps) 
		requestQueue.add(request);
		
		// And reply with a time stamp
		network.replyToRequest(clock, socket);
		
		notify();
	}
	
	public synchronized void handleRelease(Request request, String clientMsg) {
		// update lamport clock
		clock.update(request.clockval);
		
		// After receiving release message, remove the corresponding request 
		// from the request queue.
		boolean removed = requestQueue.remove(request); // this should work because we overrode Request's equals function
		if (!removed) {
			System.out.println("ERROR: WHAT THE FUCK WHAT THE FUCK WHY WHY GOD OH GOD WHY");
		}
		
		// Then do the action specified in the message if it's not a no-op
		if (!clientMsg.equals(NOOP_MSG)) {
			StrPtr returnMsg = new StrPtr();
			database.parseMessage(clientMsg, returnMsg);
		}
		
		notify();
	}
}