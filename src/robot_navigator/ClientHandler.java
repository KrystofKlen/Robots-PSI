package robot_navigator;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.CharBuffer;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.*;

public class ClientHandler implements Runnable{

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private ServerStateMachine serverStateMachine;
    private Scanner sc;
    InputStream is;
    DataInputStream in;
    boolean clientResponded = false;

    public ClientHandler(Socket socket) {

        try{
            this.socket = socket;

            this.bufferedReader =
                    new BufferedReader(
                            new InputStreamReader(new DataInputStream(socket.getInputStream())));
            this. bufferedWriter =
                    new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream()));
            serverStateMachine = new ServerStateMachine();

            sc = new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())))
                    .useDelimiter(Pattern.compile(END_MESSAGE));
            is = socket.getInputStream();
            in = new DataInputStream(socket.getInputStream());

        } catch (IOException ioEx){
            closeEverything();
        }

    }

    @Override
    public void run() {
        List<String> messagesFromFromClient = new ArrayList<>();
        String buffer = "";
        String responce = "";

        while (true){
            try{
                socket.setSoTimeout(TIMEOUT_MESSAGE_MILLIS);
                if(!serverStateMachine.getCurrentState().equals(ServerState.FIRST_MOVE)) {
                    char r;
                    while (!buffer.contains(END_MESSAGE) && buffer.length() < 100) {
                        r = (char) bufferedReader.read();
                        buffer += r;
                        if(buffer.length() >= USERNAME_MAX_LENGTH && serverStateMachine.getCurrentState().equals(GETTING_USERNAME)) break;
                        if(buffer.length() >= CLIENT_KEY_ID_MAX_MAX_LENGTH && serverStateMachine.getCurrentState().equals(GETTING_KEY_ID)) break;
                        if(buffer.length() >= CLIENT_OK_MAX_LENGTH &&
                                (serverStateMachine.getCurrentState().equals(GETTING_POSITION) ||
                                serverStateMachine.getCurrentState().equals(GETTING_DIRECTION) ||
                                serverStateMachine.getCurrentState().equals(NAVIGATING_TO_X))) break;
                    }

                    if(!buffer.contains(END_MESSAGE)){
                        bufferedWriter.write(SERVER_SYNTAX_ERROR);
                        bufferedWriter.flush();
                        closeEverything();
                        break;
                    }
                    buffer = buffer.substring(0,buffer.length() - 2);
                    System.out.println("C: " + buffer);

                    messagesFromFromClient.add(buffer);
                    serverStateMachine.putToQueue(messagesFromFromClient);
                    messagesFromFromClient.clear();
                    buffer = "";
                }

                responce = serverStateMachine.respondToMessage();
                if(!responce.equals("")){
                    System.out.println("S: " + responce);
                    bufferedWriter.write(responce);
                    bufferedWriter.flush();
                    if(serverStateMachine.getCurrentState().equals(ServerState.FAIL)){
                        System.out.println("S: CLOSING FOR FAILURE.");
                        System.out.println("-----------------------");
                        closeEverything();
                        break;
                    }
                    if(responce.equals(SERVER_LOGOUT)){
                        System.out.println("S: CLOSING CONNECTION");
                        System.out.println("---------------------");
                        closeEverything();
                        break;
                    }
                }
                clientResponded = false;
                //System.out.println("CLIENT WAIT");
            }catch (SocketTimeoutException se){
                closeEverything();
                break;
            } catch (Exception ioEx){
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
