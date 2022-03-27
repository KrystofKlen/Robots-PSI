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

        while (true){
            try{
                socket.setSoTimeout(TIMEOUT_MESSAGE_MILLIS);
                if(!serverStateMachine.getCurrentState().equals(ServerState.FIRST_MOVE) &&
                        sc.hasNext()  && !sc.hasNext(".*\\z")) {
                    //whole message came
                    buffer = sc.next();
                    System.out.println("C: " + buffer);
                    messagesFromFromClient.add(buffer);
                    serverStateMachine.putToQueue(messagesFromFromClient);
                    messagesFromFromClient.clear();
                    buffer = "";
                }else{

                }
                String responce = serverStateMachine.respondToMessage();

                if(serverStateMachine.getCurrentState().equals(ServerState.FAIL)){
                    System.out.println(System.currentTimeMillis() + "S: CLOSING FOR FAILURE.");
                    closeEverything();
                    break;
                }

                if(!responce.equals("")){
                    System.out.println("S: " + responce);
                    bufferedWriter.write(responce);
                    bufferedWriter.flush();

                    if(responce.equals(SERVER_LOGOUT)){
                        System.out.println("S: CLOSING CONNECTION");
                        System.out.println("---------------------");
                        closeEverything();
                        break;
                    }
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
