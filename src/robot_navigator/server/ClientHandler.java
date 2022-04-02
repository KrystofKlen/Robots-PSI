package robot_navigator.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.server.ServerState.*;

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
                            new InputStreamReader(new DataInputStream(socket.getInputStream())));
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
        List<String> messagesFromFromClient = new ArrayList<>();
        String buffer = "";
        String responce;
        boolean isCharging = false;

        while (true){
            try{
                socket.setSoTimeout(isCharging ? TIMEOUT_CHARGING_MILLIS : TIMEOUT_MESSAGE_MILLIS);
                if(!serverStateMachine.getCurrentState().equals(ServerState.FIRST_MOVE)) {
                    char r;
                    while (!buffer.contains(END_MESSAGE) && buffer.length() < 100) {
                        r = (char) bufferedReader.read();
                        buffer += r;

                        if(buffer.length() >= USERNAME_MAX_LENGTH &&
                                serverStateMachine.getCurrentState().equals(GETTING_USERNAME)) break;
                        if(buffer.length() >= CLIENT_OK_MAX_LENGTH &&
                                (serverStateMachine.getCurrentState().equals(GETTING_POSITION) ||
                                        serverStateMachine.getCurrentState().equals(GETTING_DIRECTION) ||
                                        serverStateMachine.getCurrentState().equals(NAVIGATING))) break;
                    }

                    //checking if message properly ended
                    if(!buffer.contains(END_MESSAGE)){
                        bufferedWriter.write(SERVER_SYNTAX_ERROR);
                        bufferedWriter.flush();
                        closeEverything();
                        break;
                    }

                    //cutting delimiter from users message
                    buffer = buffer.substring(0,buffer.length() - 2);
                    System.out.println("C: " + buffer);

                    //handle robot charging
                    if(isCharging){
                        if(!buffer.equals(CLIENT_FULL_POWER)){
                            bufferedWriter.write(SERVER_LOGIC_ERROR);
                            bufferedWriter.flush();
                            closeEverything();
                            break;
                        }
                        isCharging = false;
                        buffer = "";
                        continue;
                    }else if(buffer.equals(CLIENT_RECHARGING)){
                        isCharging = true;
                        System.out.println("$$$ charging $$$");
                        buffer = "";
                        continue;
                    }

                    //add message to queue
                    messagesFromFromClient.add(buffer);
                    serverStateMachine.putToQueue(messagesFromFromClient);
                    messagesFromFromClient.clear();
                    buffer = "";
                }

                //responding
                responce = serverStateMachine.respondToMessage();
                System.out.println("S: " + responce);
                bufferedWriter.write(responce);
                bufferedWriter.flush();

                //closing if error occured
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
            }catch (SocketTimeoutException timeoutEx){
                closeEverything();
                break;
            }catch (IOException ioEx){
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
