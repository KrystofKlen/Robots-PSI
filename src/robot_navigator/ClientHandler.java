package robot_navigator;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;

public class ClientHandler implements Runnable{

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ServerStateMachine serverStateMachine;
    private Scanner sc;
    InputStream is;
    DataOutputStream out;

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

            sc = new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())))
                    .useDelimiter(Pattern.compile(END_MESSAGE));
            is = socket.getInputStream();
            out = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ioEx){
            closeEverything();
        }

    }

    @Override
    public void run() {
        Message message = new Message();
        List<String> messagesFromFromClient = new ArrayList<>();
        String previousMessage = "";
        String buffer = "";
        byte[] buff = new byte[1024];

        while (!socket.isClosed()){
            try{
                socket.setSoTimeout(TIMEOUT_MESSAGE_MILLIS);

                if(sc.hasNext()) {
                    //whole message came
                    buffer = sc.next();
                    System.out.println("C: " + buffer);
                }else{
                    //System.out.println("IN ELSE: " + buffer);
                    /*buffer = sc.next();
                    previousMessage = message.partionMessage(previousMessage + buffer , messagesFromFromClient);
                    System.out.println("break");
                    break;*/
                }
                //previousMessage = buffer.substring(buffer.length());
                messagesFromFromClient.add(buffer);
                serverStateMachine.putToQueue(messagesFromFromClient);
                messagesFromFromClient.clear();
                buffer = "";
                String responce = serverStateMachine.respondToMessage();

                if(!responce.equals("")){
                    System.out.println("S: " + responce);
                    bufferedWriter.write(responce);

                    bufferedWriter.flush();
                }

                if(serverStateMachine.getCurrentState().equals(ServerState.FAIL)){
                    System.out.println("S: CLOSING FOR FAILURE.");
                    closeEverything();
                    break;
                }

            } catch (SocketTimeoutException soTe){
                closeEverything();
                System.out.println("TIME IS OUT");
                break;
            } catch (IOException ioEx){
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
