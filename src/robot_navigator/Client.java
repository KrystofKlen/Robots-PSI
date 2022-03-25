package robot_navigator;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static final int PORT = 5555;
    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String userName;

    public Client(Socket socket, String userName) {
        try{
            this.socket = socket;
            this.userName = userName;
            bufferedWriter =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream()
                            )
                    );
            bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );
        }catch (IOException ioEx){
            closeEverything();
        }
    }

    private void sendMessage(){
        try{
            bufferedWriter.write(userName);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);
            while(socket.isConnected() && socket != null){
                String message = sc.nextLine();
                bufferedWriter.write(message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException ioEx){
            closeEverything();
        }
    }

    public void listenForMessage(){
        new Thread(() -> {
            //listening for messages
            while (socket.isConnected()){
                try{
                    String messageFromOthers = bufferedReader.readLine();
                    System.out.println(messageFromOthers);
                }catch (IOException ioEx){
                    closeEverything();
                }
            }
        }).start();
    }

    private void closeEverything(){
        try{
            if(socket != null){
                socket.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(bufferedReader != null){
                bufferedReader.close();
            }
        }catch (IOException ioEx){
            ioEx.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException{
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter username: ");
        String username = sc.nextLine();
        Socket socket = new Socket("localhost",PORT);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }

}
