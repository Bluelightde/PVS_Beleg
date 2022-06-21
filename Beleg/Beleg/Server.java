package Beleg.Beleg;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
      public static void main(String[] args) throws IOException {

        try (ServerSocket server = new ServerSocket(6000)) {
          Socket clntSock = server.accept(); // Socket connected to the client
          DataInputStream inputS = new DataInputStream(clntSock.getInputStream());

          int receive = inputS.read();
          //System.out.println(inputS + " " + receive);
          System.out.println(receive);
        }
      }
}
