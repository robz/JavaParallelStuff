import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

class Server {
	int port;
	String IP, ID;
	
	public Server(int port, String IP) {
		this.port = port;
		this.IP = IP;
		this.ID = IP + ":" + port;
	}
}

public class NetworkInterface {
	Server[] servers;
	String ID;
	
	public NetworkInterface(Server[] servers, String ID) {
		this.servers = servers;
		this.ID = ID;
	}
	
	// sends request to all other servers & waits for their response
	// and update clock for each response from other servers
	// return true if all the servers respond 
	// return false if one of the servers times out
	public boolean broadcastRequest(Clock clock, Request req) {
		clock.tick();
		
		boolean result = false;
		
		String str = "RequestCS;" + ID;
		ArrayList<Socket> sockets = new ArrayList<Socket>();
		
		System.out.println("Requesting CS");
		
		try {
			for (Server server: servers) {
				if (!server.ID.equals(ID)) {
					String msg = str + ";" + clock.clock + ";" + req.clockval;
					
					System.out.println("  sending \"" + msg + "\" to " + server.IP 
							+ " on " + server.port);
					
					Socket socket = null;
					
					try {
						socket = new Socket(server.IP, server.port);
						
				        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
				        out.writeBytes(msg + '\n');
				        
				        sockets.add(socket);
					} catch (IOException ex) {
						System.out.println("  could not connect to " + server.IP 
							+ " on " + server.port);
					
						if (null != socket) {
							try {
				                socket.close();
				            } catch (IOException ex2) {
				                System.out.println("exception occured while trying to close a socket");
				            }
						}
			        }
				}
			}
			
			for (Socket socket: sockets) {
		        BufferedReader in = new BufferedReader(
		            new InputStreamReader(socket.getInputStream()));
		        String response = in.readLine();

				System.out.println("  received " + response);
				
				String[] parts = response.split(";");
		        int theirClock = Integer.parseInt(parts[2]);

				clock.update(theirClock);

				try {
	                socket.close();
	            } catch (IOException ex) {
	                System.out.println("exception occured while trying to close a socket");
	            }
			}
			
			result = true;
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
		
		return result;
	}
	
	// sends release message to all other servers, which is the same
	// message that was sent to us from the client
	// and increase clock for every release message we send
	public void broadcastRelease(Clock clock, Request req, String clientMsg) {
		clock.tick();
		
		String str = "ReleaseCS;" + ID;
		
		System.out.println("Releasing CS");
		
		for (Server server: servers) {
			if (!server.ID.equals(ID)) {
				Socket socket = null;
				
				try {
					String msg = str + ";" + clock.clock + ";" + req.clockval + ";" + clientMsg;

					System.out.println("  sending \"" + msg + "\" to " + server.IP 
							+ " on " + server.port);
					
					socket = new Socket(server.IP, server.port);
					
			        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			        out.writeBytes(msg + '\n');
				} catch (IOException ex) {
					System.out.println("  could not connect to " + server.IP 
							+ " on " + server.port);
        		} finally {
        			if (null != socket) {
        				try {
							socket.close();
						} catch (IOException ex) {
							System.out.println("exception occured while trying to close a socket");
				        }     
        			}
                }
			}
		}
	}
	
	public void replyToRequest(Clock clock, Socket socket) {
		clock.tick();
		
		String msg = "Reply;" + ID + ";" + clock.clock;
		
		System.out.println("replying to other server: " + msg);
		
		try {
			DataOutputStream output = new DataOutputStream(socket.getOutputStream());
			output.writeBytes(msg + '\n');
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}