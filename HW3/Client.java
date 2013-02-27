import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        NetworkInterface ni = new UDPInterface(8800);
        JFrame gui = new GUI(ni);
        gui.setBounds(50, 50, 500, 600);
        gui.setVisible(true);
    }
}

interface NetworkInterface {
    public String reserve(String address, String name, int count);
    public String search(String address, String name);
    public String delete(String address, String name);
};

class TCPInterface implements NetworkInterface {
    public TCPInterface() {

    }

    public String reserve(String address, String name, int count) {
        return null;
    }

    public String search(String address, String name) {
        return null;
    }

    public String delete(String address, String name) {
        return null;
    }
}

class UDPInterface implements NetworkInterface {
    static final int RESPONSE_SIZE = 1024,
                     TIMEOUT = 2000;

    int port;

    public UDPInterface(int port) {
        this.port = port;
    }    

    private String sendMsg(String address, String msg) {
        DatagramSocket socket = null;
        String response = null;

        try {
            InetAddress host = InetAddress.getByName(address);
            socket = new DatagramSocket();

            byte [] data = msg.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, host, port);

            System.out.println("sending \"" + msg + "\" to "
                + address + " on port " + port);

            socket.send(packet);
            socket.setSoTimeout(TIMEOUT);

            packet.setData(new byte[RESPONSE_SIZE]);
            socket.receive(packet);
            
            response = new String(packet.getData());
        } catch (Exception e) {
            e.printStackTrace();
            response = "Exception: see console for details";
        } finally {
            if(socket != null) {
                socket.close();
            }
        }

        return response;
    }

    public String reserve(String address, String name, int count) {
        return sendMsg(address, "reserve " + name + " " + count);
    }

    public String search(String address, String name) {
        return sendMsg(address, "search " + name);
    }

    public String delete(String address, String name) {
        return sendMsg(address, "delete " + name);
    }
}

class GUI extends JFrame implements ActionListener {
    NetworkInterface ni;    
    JTextField addressField, nameField, countField;
    JButton reserveBtn, searchBtn, deleteBtn;
    JTextArea responseArea;

    public GUI(NetworkInterface ni) {
        this.ni = ni;

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));

        addressField = new JTextField("128.12.0.1");
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
    }

    private boolean isValidName(String name) {
        if (name == null || name.equals("")) {
            return false;
        }

        return name.matches("[a-zA-Z]+"); 
    }

    private boolean isValidAddress(String address) {
        if (address == null || address.equals("")) {
            return false;
        }

        return address.matches("(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))");
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
                System.out.println("reserving " + numCount + " spots for " + name);
                response = ni.reserve(address, name, numCount);
            } catch (NumberFormatException ex) {
                response = "Clientside error: {" + strCount + "} is not a valid number!";
            }
        } else if (e.getSource().equals(searchBtn)) {
            System.out.println("searching for " + name + "'s reservations");
            
            response = ni.search(address, name);
        } else if (e.getSource().equals(deleteBtn)) {
            System.out.println("deleting " + name + "'s reservations"); 

            response = ni.delete(address, name);
        } else {
            response = "Clientside error: what button is that?";
        }

        responseArea.setText(response);
    }    
}
