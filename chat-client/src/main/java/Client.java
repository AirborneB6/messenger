import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Client extends JFrame {

    private JTextField userTextBuffer;
    private JTextArea chatHistory;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private String message = "";

    private String serverIP = "";
    private Socket connectionSocket;

    public Client(String hostIP){
        super("Messenger [client]");

        this.serverIP = hostIP;
        userTextBuffer = new JTextField();
        userTextBuffer.setEditable(false);
        userTextBuffer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                userTextBuffer.setText("");
            }
        });

        this.add(userTextBuffer , BorderLayout.SOUTH);
        this.chatHistory = new JTextArea();
        this.add(new JScrollPane(this.chatHistory) , BorderLayout.CENTER);
        this.setSize(300 , 150);
        this.setVisible(true);
    }

    public void run(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();
        }
        catch (EOFException e){
            this.showMessage("Client terminated the connection!\n");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            closeCrap();
        }
    }

    private void connectToServer() throws IOException {
        this.showMessage("Attempting connection...\n");

        this.connectionSocket = new Socket(InetAddress.getByName(serverIP) , 1234);

        this.showMessage("Connected to " + connectionSocket.getInetAddress().getHostName() + "!");
    }

    private void setupStreams() throws IOException {
        this.output = new ObjectOutputStream(connectionSocket.getOutputStream());
        output.flush();
        this.input = new ObjectInputStream(connectionSocket.getInputStream());

        this.showMessage("Buddy pipes good to go!");
    }

    private void whileChatting() throws IOException {
        this.ableToType(true);

        do {
            try {
                this.message = (String) input.readObject();
                this.showMessage(message + "\n");
            } catch (ClassNotFoundException e) {
                this.showMessage("idk what that object is man :(\n");
            }
        }
        while (!message.equals("SERVER - END"));
    }

    private void closeCrap(){
        this.showMessage("Closing crap down...");
        this.ableToType(false);
        try{
            this.input.close();
            this.output.close();
            this.connectionSocket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {

        try{
            output.writeObject(message);
            output.flush();
            this.showMessage(message);
        }
        catch (IOException e){
            chatHistory.append("Failed to send message.");
        }
    }

    private void showMessage(final String message){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatHistory.append(message);
            }
        });
    }

    private void ableToType(final boolean flag){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                userTextBuffer.setEditable(flag);
            }
        });
    }
}
