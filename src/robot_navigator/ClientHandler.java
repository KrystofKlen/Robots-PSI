package robot_navigator;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.CharBuffer;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;

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
        Timer timer;
        TimerTask task;
        List<String> messagesFromFromClient = new ArrayList<>();
        String previousMessage = "";
        String buffer = "";
        char [] buff = new char[100];
        Scanner scc = new Scanner(StringReader.nullReader());
        while (true){
            try{
                socket.setSoTimeout(TIMEOUT_MESSAGE_MILLIS);
                if(!serverStateMachine.getCurrentState().equals(ServerState.FIRST_MOVE)) {
                    char r, prev = '\u0007';
                    boolean wasA = false;

                    while (!buffer.contains(END_MESSAGE)) {
                        //System.out.println(buffer);
                        r = (char) bufferedReader.read();
                        buffer += r;
                    }
                    buffer = buffer.substring(0,buffer.length() - 2);
                    //buffer.trim();
                    System.out.println("C: " + buffer);
                    messagesFromFromClient.add(buffer);
                    serverStateMachine.putToQueue(messagesFromFromClient);
                    messagesFromFromClient.clear();
                    buffer = "";
                }
                /*if(!serverStateMachine.getCurrentState().equals(ServerState.FIRST_MOVE) &&
                    sc.hasNext()  && !sc.hasNext(".*\\z")) {
                    //whole message came

                    clientResponded = true;
                    System.out.println("C: " + buffer);
                    messagesFromFromClient.add(buffer);
                    serverStateMachine.putToQueue(messagesFromFromClient);
                    messagesFromFromClient.clear();
                    buffer = "";
                }else{

                }*/

                String responce = serverStateMachine.respondToMessage();
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
            }catch (RobotDamagedException rde){
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
