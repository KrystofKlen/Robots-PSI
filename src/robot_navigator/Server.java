package robot_navigator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    ServerSocket serverSocket;
    public static final int PORT = 5555;
    public static final int BACKLOG = 30;

    Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {
            System.out.println("SERVER LISTENING AT PORT: " + PORT);
            System.out.println("delimiter = " + CONSTANTS.END_MESSAGE);
            System.out.println("-----------------------------------");

            //Wait and receive clients
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("new client connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException ioEx){
            ioEx.printStackTrace();
        }
    }

    private void closeServerSocket(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException ioEx){
            ioEx.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT, BACKLOG);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
