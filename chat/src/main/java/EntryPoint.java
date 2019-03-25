import javax.swing.*;

public class EntryPoint {
    public static void main(String[] args) {
        Server server = new Server();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.run();
    }
}
