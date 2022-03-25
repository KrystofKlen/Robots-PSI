package robot_navigator;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static robot_navigator.CONSTANTS.*;

public class ClientHandler implements Runnable{

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ServerStateMachine serverStateMachine;

    public ClientHandler(Socket socket) {

        try{
            this.socket = socket;

            this.bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(socket.getInputStream()));
            this. bufferedWriter =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream()));
            serverStateMachine = new ServerStateMachine();

        } catch (IOException ioEx){
            closeEverything();
        }

    }

    @Override
    public void run() {
        Message message = new Message();
        List<String> messagesFromFromClient = new ArrayList<>();
        String buffer;
        String previousMessage = "";

        while (socket.isConnected()){
            try{
                socket.setSoTimeout(TIMEOUT_MESSAGE_MILLIS);
                buffer = bufferedReader.readLine();
                previousMessage = message.partionMessage(previousMessage + buffer , messagesFromFromClient);

                System.out.println("------------");
                for(String str: messagesFromFromClient){
                    System.out.println("S: " + str);
                }
                serverStateMachine.putToQueue(messagesFromFromClient);
                messagesFromFromClient.clear();
                String responce = serverStateMachine.respondToMessage();
                bufferedWriter.write(responce);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                if(serverStateMachine.getCurrentState().equals(ServerState.FAIL)){
                    System.out.println("S: CLOSING FOR FAILURE.");
                    break;
                }

                /*if(!message.checkMessageLength(messageFromClient.length(), serverStateMachine.getCurrentState())){
                    //message too long
                    System.out.println("S: Message too long!" + messageFromClient.length());
                    throw new IOException();
                }
                if(!message.checkIfContainsValidMessage(messageFromClient)){
                    //not a valid message
                    System.out.println("S: Not contains " + END_MESSAGE);
                    throw  new IOException();
                }

                if(message.checkIfMessageEnded(messageFromClient)){
                    //message completed
                    System.out.println("S: Message complete: " + messageFromClient);
                }
                String msg1 = message.getEndOfCurrentMessage(messageFromClient);
                System.out.println("S: Message got: " + msg1 + " but there is more " + messageFromClient);
                */

            }catch (IOException ioException){
                closeEverything();
                break;
            }
        }
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
}
