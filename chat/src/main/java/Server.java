import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame {

    private JTextField userTextBuffer;
    private JTextArea chatHistory;

    private ObjectOutputStream output;
    private ObjectInputStream input;

    private ServerSocket serverSocket;
    private Socket connectionSocket;

    public Server(){

        //title
        super("Messenger");

        this.userTextBuffer = new JTextField();
        this.userTextBuffer.setEditable(false);
        this.userTextBuffer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                userTextBuffer.setText("");
            }

        });

        this.add(userTextBuffer , BorderLayout.SOUTH);
        this.chatHistory = new JTextArea();
        this.add(new JScrollPane(chatHistory));

        this.setSize(300 , 150);

        this.setVisible(true);
    }

    public void run(){
        try{
            this.serverSocket = new ServerSocket(1234 , 100);

            while (true){
                try{
                    //talk
                    waitForConnection();
                    setupStreams();
                    whileChatting();
                }
                catch (EOFException e){
                    showMessage("Server closed the connection!\n");
                }
                finally {
                    closeCrap();
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {


        try{
            this.output.writeObject(message);
            this.output.flush();
            showMessage(message);
        }
        catch (IOException e){
            this.chatHistory.append("AYY WHACHU TRYNA SEND NIGGA");
        }
    }

    private void waitForConnection() throws IOException {
        showMessage("Waiting on my buddies to connect\n");

        connectionSocket = serverSocket.accept();

        showMessage("Found buddy at [" + connectionSocket.getInetAddress().getHostName() + "]!");
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connectionSocket.getOutputStream());
        output.flush(); //just in case

        input = new ObjectInputStream(connectionSocket.getInputStream());

        showMessage("   Buddy pipes operational!\n");
    }

    private void whileChatting() throws IOException {

        String message = "You are connected! Say hi!\n";
        sendMessage(message);

        this.ableToType(true);

        do{
            try{
                message = (String) input.readObject();
                showMessage(message + "\n");
            }
            catch (ClassNotFoundException e){
                showMessage("idk wtf that buddy sent!");
            }
        }
        while(!message.equals("CLIENT - END"));
    }

    private void closeCrap(){
        showMessage("Closing connections with buddy...\n");

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
