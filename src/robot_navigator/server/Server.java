package robot_navigator.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static robot_navigator.CONSTANTS.PORT;

public class Server {

    ServerSocket serverSocket;

    Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try {
            System.out.println("SERVER LISTENING AT PORT: " + PORT);
            System.out.println("_________________________________");

            //Wait and receive clients
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("___ NEW CLIENT CONNECTED ___");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException ioEx){
            ioEx.printStackTrace();
            closeServerSocket();
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
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
