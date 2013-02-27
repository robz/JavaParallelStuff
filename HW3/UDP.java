import java.net.*;
import java.io.*;

public class UDP{
	private final static int c = 30; //number of seats in the theater
	private final static int port = 8800;
	private final static int len = 1024; //just following his example
	
	private static boolean[] seatOccupied;
	private static int currentCount;
	//private DatagramPacket receivePacket, returnPacket;
	//private byte[] buffer = new byte[len];
	
	public static void main(String[] args) throws Exception {
		DatagramSocket socket = new DatagramSocket(port);
		byte[] receiveBuffer = new byte[len];
		byte[] returnBuffer = new byte[len];
		currentCount = 0;
		seatOccupied = new boolean[c];
		
		while(true) {
			
			DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
			socket.receive(receivePacket);
			String receiveMessage = new String(receivePacket.getData());
			
			String response = selectRequest(receiveMessage);
			
			String returnMessage = "Received Packet";
			returnBuffer = returnMessage.getBytes();
			//returnPacket = new DatagramPacket(returnBuffer);
			
		}
		
		
	}
	
	public static synchronized String selectRequest(String message) {
		String[] parts = message.split(" ");
		String ret;
		
		if(parts[0].equals("reserve"))
			return reserve(message);
		else if(parts[0].equals("search"))
			return search(message);
		else if(parts[0].equals("delete"))
			return search(message);
		
		return "hello";
	}
	
	public static String reserve(String message) {
		
		
		return "hello";
	}
	
	public static String search(String message) {
		
		
		return "hello";
	}
}
