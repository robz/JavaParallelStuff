import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.net.*;
import java.util.Random;
import java.io.*;

public class Client {
    static final String DEFAULT_IP = "127.0.0.1";

    public static void main(String[] args) {
		System.out.println("client starting");
		
        ClientNetworkInterface ni = null;

        ni = new TCPInterface();
        
        JFrame gui = new GUI(ni, DEFAULT_IP);
        gui.setBounds(50, 50, 500, 600);
        gui.setVisible(true);
    }
}

//********************************************************************************//

abstract class ClientNetworkInterface {
    static final int RESPONSE_SIZE = 1024,
                     TIMEOUT = 5000;

    abstract protected String sendMsg(String msg);

    public String reserve(String name, int count) {
    	
        return sendMsg("reserve " + name + " " + count);
    }

    public String search(String name) {
        return sendMsg("search " + name);
    }

    public String delete(String name) {
        return sendMsg("delete " + name);
    }
}

class TCPInterface extends ClientNetworkInterface {
	static Server[] SERVERS = {
		new Server(8800, "127.0.0.1"), 
		new Server(8801, "127.0.0.1"),
		//new Server(8802, "127.0.0.1"),
	};
	
    public TCPInterface() {
    	
    }

    protected String sendMsg(String msg) {
        Socket socket = null;
        String response = null;

        try {
        	Random rand = new Random();
        	Integer randomNum = rand.nextInt(SERVERS.length);
        	
        	int port = SERVERS[randomNum].port;
        	String address = SERVERS[randomNum].IP;
        	
            System.out.println("sending \"" + msg + "\" to "
                + address + " on port " + port + " to server " + randomNum);
            
            socket = new Socket(address, port);
            
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            
            out.writeBytes(msg + '\n');
            
            response = in.readLine();
            
            System.out.println("received from server: " + response);
        } catch (IOException ex) {
        	response = "Connection failed, try again";
    	} catch (Exception ex) {
            ex.printStackTrace();
            response = "Exception: see console for details";
        } finally {
            try {
                if (null != socket) {
                    socket.close();
                } 
            } catch (IOException ex) {
                ex.printStackTrace();
                response = "Exception while closing socket: see console for details";
            }
        }

        return response;
    }
}

//********************************************************************************//

@SuppressWarnings("serial")
class GUI extends JFrame implements ActionListener {
	ClientNetworkInterface ni;    
    JTextField addressField, nameField, countField;
    JButton reserveBtn, searchBtn, deleteBtn;
    JTextArea responseArea;

    public GUI(ClientNetworkInterface ni, String defaultAddress) {
        this.ni = ni;

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        addressField = new JTextField(defaultAddress);
        nameField = new JTextField();
        countField = new JTextField();
        reserveBtn = new JButton("reserve(name, count)");
        searchBtn = new JButton("search(name)");
        deleteBtn = new JButton("delete(name)");
        responseArea = new JTextArea("<server response>");

        JPanel addressPanel = new JPanel();
        addressPanel.setLayout(new GridLayout(1, 2));

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new GridLayout(1, 2));

        JPanel countPanel = new JPanel();
        countPanel.setLayout(new GridLayout(1, 2));

        addressPanel.add(new JLabel("IP Address of host:", JLabel.CENTER));
        addressPanel.add(addressField);

        namePanel.add(new JLabel("Enter name:", JLabel.CENTER));
        namePanel.add(nameField);

        countPanel.add(new JLabel("Enter reservation count:", JLabel.CENTER));
        countPanel.add(countField);

        panel.add(addressPanel);
        panel.add(namePanel);
        panel.add(countPanel);
        panel.add(reserveBtn);
        panel.add(searchBtn);
        panel.add(deleteBtn);
        panel.add(responseArea);

        this.setContentPane(panel);

        reserveBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        
        responseArea.setLineWrap(true);
        responseArea.setEditable(false);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private boolean isValidName(String name) {
        if (null == name || name.equals("")) {
            return false;
        }

        return name.matches("[a-zA-Z]+"); 
    }

    private boolean isValidAddress(String address) {
        if (null == address || address.equals("")) {
            return false;
        }

        return address.matches("(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)"
            + "|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))");
    }

    public void actionPerformed(ActionEvent e) {
        String address = addressField.getText(),
               name = nameField.getText(),
               response = null;

        if (!isValidAddress(address)) {
            response = "Clientside error: that's not a valid IP address!";
        } else if (!isValidName(name)) {
            response = "Clientside error: that's not a valid name!";
        } else if (e.getSource().equals(reserveBtn)) {
            String strCount = countField.getText();
            int numCount = -1;

            try {
                numCount = Integer.parseInt(strCount);
                System.out.println("client: reserving " + numCount + " spots for " + name);
                response = ni.reserve(name, numCount);
            } catch (NumberFormatException ex) {
                response = "Clientside error: {" + strCount + "} is not a valid number!";
            }
        } else if (e.getSource().equals(searchBtn)) {
            System.out.println("client: searching for " + name + "'s reservations");
            
            response = ni.search(name);
        } else if (e.getSource().equals(deleteBtn)) {
            System.out.println("client: deleting " + name + "'s reservations"); 

            response = ni.delete(name);
        } else {
            response = "Clientside error: what button is that?";
        }

        responseArea.setText(response);
    }    
}