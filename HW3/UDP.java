import java.net.*;
import java.io.*;
import java.util.concurrent.*;

public class UDP extends Thread {
	private final static int c = 30; //number of seats in the theater
	private final static int port = 8800;
	private final static int len = 1024; //just following his example
	
	private static boolean[] seatOccupied;
	private static int currentCount;
	private static DatagramSocket socket;
	private static InetAddress address;
	private static int outport;
	
	public static String receiveMessage;
	
	
	public UDP() {
		//seatOccupied = new boolean[c];
		//currentCount = 0;
	}
	
	
	
	public static void main(String[] args) throws Exception {
		seatOccupied = new boolean[c];
		currentCount = 0;
		socket = new DatagramSocket(port);
		
		byte[] receiveBuffer = new byte[len];
		
		while(true) {
			
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socket.receive(receivePacket);
			address = receivePacket.getAddress();
			outport = receivePacket.getPort();
			receiveMessage = new String(receivePacket.getData());
			
			UDP t = new UDP();
			t.start();
			
		}
	}
	
	//************************************************************************
	
	public static String selectRequest(String message) {
		String[] parts = message.split(" ");
		
		if(parts[0].equals("reserve"))
			return reserve(message);
		else if(parts[0].equals("search"))
			return search(message);
		else if(parts[0].equals("delete"))
			return delete(message);
		
		return "hello";
	}
	
	public static synchronized String reserve(String message) {
		
		
		return "hello";
	}
	
	public static synchronized String search(String message) {
		
		
		return "hello";
	}
	
	public static synchronized String delete(String message) {
		
		
		
		return "hello";
	}
	
	public static synchronized void sendPacket(String message) throws IOException {
		byte[] sendBuffer = new byte[len];
		sendBuffer = message.getBytes();
		DatagramPacket returnPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, outport);
		socket.send(returnPacket);
	}

	//************************************************************************
	
	public void run() {
		String str = selectRequest(receiveMessage);
		try {
			sendPacket(str);
		} catch (Exception e) {
			
		}
	}
}
