import javax.swing.*;

public class Client {
    public static void main(String[] args) {
        System.out.println("hi!");
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

class GUI extends JFrame extends ActionListener {
    NetworkInterface ni;    
    JTextField nameField, countField;
    JButton reserveBtn, searchBtn, deleteBtn;

    public GUI(NetworkInterface ni) {
        this.ni = ni;

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1));

        nameField = new JTextField();
        countField = new JTextField();
        reserveBtn = new JButton("reserve(name, count)");
        searchBtn = new JButton("search(name)");
        deleteBtn = new JButton("delete(name)");

        this.setContentPane(panel);
    }

    public void actionListener(ActionEvent e) {
        if (e.getSource().equals(reserveBtn)) {

        } else if (e.getSource().equals(searchBtn) {

        } else if (e.getSource().equals(deleteBtn) {

        }
    }    
}
