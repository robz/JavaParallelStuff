import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Database {
	
	private final int c = 30; //number of seats in the theater
	private boolean[] seatOccupied;
	private int currentAvailable;
	private HashMap<String,ArrayList<Integer>> names;
	
	
	public Database() {
		seatOccupied = new boolean[c]; //should all be initialized to false
		currentAvailable = c;
		names = new HashMap<String, ArrayList<Integer>>();
		
	}
	

	public synchronized boolean parseMessage(String clientMsg, StrPtr returnMsg) {
		String[] parts = clientMsg.split(" ");
		String command = parts[0];
		
		if(command.equals("reserve")) {
			return reserve(clientMsg, returnMsg);
		} else if(command.equals("search")) {
			return search(clientMsg, returnMsg);
		} else if(command.equals("delete")) {
			return delete(clientMsg, returnMsg);
		}
		
		returnMsg.str = "Error! Unknown command: " + command;
		return false;
	}
	
	
//*******************************************************************************************
	
	private boolean reserve(String message, StrPtr returnMsg) {
		String[] parts = message.split("\\W+");
		
		//name exists?
		if(names.containsKey(parts[1])) {
			returnMsg.str = "Seats already booked against the name provided.";
			return false;
		}
		
		int seats = Integer.parseInt(parts[2]);
		
		//not enough seats?
		if(currentAvailable < seats) {
			returnMsg.str = "Seats not available.";
			return false;
		}
		
		//assign new reservation
		currentAvailable = currentAvailable - seats;
		int tempAssign = seats;
		ArrayList<Integer> assignSeats = new ArrayList<Integer>();
		for(int i = 0 ; i < c ; i++) {
			if(!seatOccupied[i]) {
				seatOccupied[i] = true;
				assignSeats.add(i+1);
				seats--;
				
				if(seats == 0)
					break;
			}
		}
		Collections.sort(assignSeats);
		names.put(parts[1], assignSeats); //add new entry to hashmap, associate seats to name
		 
		String ret = "Seat assigned to you are";
		int temp = tempAssign;
		for(Integer i : assignSeats) {
			ret = ret + " " + i;
			
			temp--;
			if(temp > 0)
				ret = ret + ",";
			else if(temp == 0)
				ret = ret + ".";
		}
		
		returnMsg.str = ret;
		return true;
	}
	
	private boolean search(String message, StrPtr returnMsg) {
		String[] parts = message.split("\\W+");
		
		//name not found?
		if(!names.containsKey(parts[1])) {
			returnMsg.str = "No reservation found for " + parts[1];
			return false;
		}
		
		//name exists, return seat numbers.
		ArrayList<Integer> curSeats = names.get(parts[1]);
		String ret = "Seat assigned to you are";
		int temp = curSeats.size();
		for(Integer i : curSeats) {
			ret = ret + " " + i;
			
			temp--;
			if(temp > 0)
				ret = ret + ",";
			else if(temp == 0)
				ret = ret + ".";
		}
		
		returnMsg.str = ret;
		return true;
	}
	
	private boolean delete(String message, StrPtr returnMsg) {
		String[] parts = message.split("\\W+");
		
		//name not found?
		if(!names.containsKey(parts[1])) {
			returnMsg.str = "No reservation found for " + parts[1];
			return false;
		}
		
		//name exists, gotta delete!
		ArrayList<Integer> curSeats = names.get(parts[1]);
		currentAvailable = currentAvailable + curSeats.size();
		
		String ret = "Freed up " + curSeats.size() + " seats:";
		int temp = curSeats.size();
		for(Integer i : curSeats) {
			seatOccupied[i-1] = false;
			ret = ret + " " + i;
			
			temp--;
			if(temp > 0)
				ret = ret + ",";
			else if(temp == 0)
				ret = ret + ".";
		}
		names.remove(parts[1]); //delete name
		
		returnMsg.str = ret;
		return true;
	}
	
}
