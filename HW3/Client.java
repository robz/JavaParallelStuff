import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client {
    public static void main(String[] args) {
        NetworkInterface ni = new UDPInterface();
        JFrame gui = new GUI(ni);
        gui.setBounds(50, 50, 500, 600);
        gui.setVisible(true);
    }
}

interface NetworkInterface {
    public String reserve(String name, int count);
    public String search(String name);
    public String delete(String name);
};

class TCPInterface implements NetworkInterface {
    public String reserve(String name, int count) {
        return null;
    }

    public String search(String name) {
        return null;
    }

    public String delete(String name) {
        return null;
    }
}

class UDPInterface implements NetworkInterface {
    public String reserve(String name, int count) {
        return null;
    }

    public String search(String name) {
        return null;
    }

    public String delete(String name) {
        return null;
    }
}

class GUI extends JFrame implements ActionListener {
    NetworkInterface ni;    
    JTextField nameField, countField;
    JButton reserveBtn, searchBtn, deleteBtn;
    JTextArea responseArea;

    public GUI(NetworkInterface ni) {
        this.ni = ni;

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        nameField = new JTextField();
        countField = new JTextField();
        reserveBtn = new JButton("reserve(name, count)");
        searchBtn = new JButton("search(name)");
        deleteBtn = new JButton("delete(name)");
        responseArea = new JTextArea("<server response>");

        JPanel namePanel = new JPanel();
        namePanel.setLayout(new GridLayout(1, 2));

        JPanel countPanel = new JPanel();
        countPanel.setLayout(new GridLayout(1, 2));

        namePanel.add(new JLabel("Enter name:", JLabel.CENTER));
        namePanel.add(nameField);

        countPanel.add(new JLabel("Enter reservation count:", JLabel.CENTER));
        countPanel.add(countField);

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

    public void actionPerformed(ActionEvent e) {
        String name = nameField.getText(),
               response = null;

        if (name == null || name.equals("")) {
            response = "Clientside error: that's not a valid name!";
        } else if (e.getSource().equals(reserveBtn)) {
            String strCount = countField.getText();
            int numCount = -1;

            try {
                numCount = Integer.parseInt(strCount);
                
                System.out.println("reserving "+numCount+" spots for "+name);
                
                response = ni.reserve(name, numCount);
            } catch (NumberFormatException ex) {
                response = "Clientside error: {"+strCount+"} is not a valid number!";
            }
        } else if (e.getSource().equals(searchBtn)) {
            System.out.println("searching for "+name+"'s reservations");
            
            response = ni.search(name);
        } else if (e.getSource().equals(deleteBtn)) {
            System.out.println("deleting "+name+"'s reservations"); 

            response = ni.delete(name);
        } else {
            response = "Clientside error: what button is that?";
        }

        responseArea.setText(response);
    }    
}
