import java.net.*;
import java.io.*;

public class Main extends Thread {
	static Server[] SERVERS = new Server[]{
		new Server(8800, "127.0.0.1"), 
		new Server(8801, "127.0.0.1"),
		new Server(8802, "127.0.0.1")
	};
	
	public static void main(String[] args) throws Exception {
		//
		// Check arguments
		//
		if (args.length != 1) {
			System.out.println("bad # of arguments! expecting port number...");
			System.exit(0);
		}
		
		//
		// Get port
		//
		int port = -1;
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException ex) {
			System.out.println("port # not formatted correctly!");
			System.exit(0);
		}
		
		//
		// Get this server's IP address
		//
		//InetAddress addr = InetAddress.getLocalHost();
	    //String ipAddress = addr.getHostAddress();
		String ipAddress = "127.0.0.1";
	    
	    //
	    // Define this server's unique ID 
	    //
	    String ID = ipAddress + ":" + port;
		System.out.println("server starting with ID " + ID);
		
	    //
	    // Initialize everything
		//
		Database database = new Database();
	    ServerSocket socket = new ServerSocket(port);
		NetworkInterface network = new NetworkInterface(SERVERS, ID);
		LamportInterface lamport = new LamportInterface(database, network, ID);
		
		//
		// Begin processing outside requests
		//
		Lab4_serviceClients(lamport, socket);
		//Lab3_serviceClients(database, socket);
	}
	
	public static void Lab4_serviceClients(LamportInterface lp, ServerSocket socket) throws Exception {
		while (true) {
			Socket client = socket.accept();
			BufferedReader read = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			
			String msg = read.readLine();
			System.out.println("received from client: " + msg);
			
			Lab4_ClientHandler handler = new Lab4_ClientHandler(lp, msg, client);
			new Thread(handler).start();
		}
	}
	
	public static void Lab3_serviceClients(Database db, ServerSocket socket) throws Exception {
		while (true) {
			Socket client = socket.accept();
			BufferedReader read = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			
			String msg = read.readLine();
			System.out.println("received from client: " + msg);
			
			Lab3_ClientHandler handler = new Lab3_ClientHandler(db, msg, client);
			new Thread(handler).start();
		}
	}
}

class Lab4_ClientHandler implements Runnable {
	LamportInterface lamport;
	String msgReceived;
	Socket client;
	
	public Lab4_ClientHandler(LamportInterface lp, String msg, Socket client) {
		this.lamport = lp;
		this.msgReceived = msg;
		this.client = client;
	}
	
	public void run() {
		//
		// Figure out if the message we received is from a client of server
		//
		String[] parts = msgReceived.split("\\s+");
		String command = parts[0];
		
		if (command.contains(";")) {
			handleOtherServer();
		} else {
			handleClient();
		}
		
		try {
			client.close();
		} catch (IOException ex) {
			System.out.println("exception occured while trying to close a socket");
	    }
	}
	
	private void handleOtherServer() {
		//
		// Extract the necessary info from the message
		//
		String[] parts = msgReceived.split(";");
		
		String operation = parts[0];
		String theirID = parts[1];
		int theirClock = Integer.parseInt(parts[2]);
		int reqClock = Integer.parseInt(parts[3]);
		
		Request request = new Request(theirID, reqClock);
		
		//
		// Figure out if this is a requestCS or a releaseCS
		//
		if (operation.equals("RequestCS")) {
			lamport.handleRequest(request, client);
		} else if (operation.equals("ReleaseCS")) {
			String clientMsg = parts[3];
			lamport.handleRelease(request, clientMsg);
		}
	}
	
	private void handleClient() {
		String returnMsg = lamport.sendRequest(msgReceived);
		
		try {
			System.out.println("response to client: " + returnMsg);
			sendResponse(returnMsg);
		} catch (Exception ex) {
			ex.printStackTrace();
		} 
	}
	
	private void sendResponse(String msg) throws IOException {
		DataOutputStream output = new DataOutputStream(client.getOutputStream());
		output.writeBytes(msg + '\n');
	}
}

class Lab3_ClientHandler implements Runnable {
	Database database;
	String msgReceived;
	Socket client;
	
	public Lab3_ClientHandler(Database db, String msg, Socket client) {
		this.database = db;
		this.msgReceived = msg;
		this.client = client;
	}
	
	public void run() {
		StrPtr returnMsgPtr = new StrPtr();
		database.parseMessage(msgReceived, returnMsgPtr);
		String returnMsg = returnMsgPtr.str;
		
		try {
			System.out.println("response to client: " + returnMsg);
			sendResponse(returnMsg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private void sendResponse(String msg) throws IOException {
		DataOutputStream output = new DataOutputStream(client.getOutputStream());
		output.writeBytes(msg + '\n');
	}
}
