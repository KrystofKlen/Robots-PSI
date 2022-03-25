package robot_navigator;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.*;

public class ServerStateMachine {

    private ServerState currentState;
    private Queue<String> messagesFromClient;
    private Robot robot;
    private Message msg;

    public ServerStateMachine() {
        this.currentState = GETTING_USERNAME;
        messagesFromClient = new LinkedList<>();
        msg = new Message();
    }

    public ServerState getCurrentState(){
        return currentState;
    }

    private void changeServerState(){
        switch (currentState){
            case GETTING_USERNAME -> currentState = GETTING_KEY_ID;
            case GETTING_KEY_ID -> currentState = CONFORMATION;
        }
        System.out.println("********** STATE CHANGED, CURRENT STATE = " + currentState.toString() + "****************");
    }

    public String respondToMessage(){
        //no response if no message from client
        if(messagesFromClient.isEmpty()) return "empty queue";
        if(! msg.checkMessageLength(messagesFromClient.peek().length(),currentState) ||
                ! msg.checkMessageSyntax(messagesFromClient.peek(),currentState)){
            currentState = FAIL;
            return SERVER_SYNTAX_ERROR;
        }

        if(currentState.equals(GETTING_USERNAME)){
            robot = new Robot( messagesFromClient.poll());
            changeServerState();
           return SERVER_KEY_REQUEST;
        }

        if(currentState.equals(GETTING_KEY_ID)){
            if (! robot.setKeyID(Integer.parseInt(messagesFromClient.poll()))){
                currentState = FAIL;
                return SERVER_KEY_OUT_OF_RANGE_ERROR;
            }
            robot.setHash(countHash());
            changeServerState();
            return String.valueOf(robot.getHash());
        }

        if(currentState.equals(CONFORMATION)){
            if(robot.getHash() == Integer.parseInt(messagesFromClient.poll())){
                changeServerState();
                return SERVER_OK;
            }else{
                currentState = FAIL;
                return SERVER_LOGIN_FAILED;
            }
        }
        return "TO IMPLEMENT";
    }

    public void putToQueue(List<String> messages){
        messagesFromClient.addAll(messages);
    }

    private int countHash(){
        int asciiValue = 0;
        char [] userName  = robot.getClientUsername().toCharArray();
        for (char c : userName) {
            asciiValue+= (int) c;
        }
        return (asciiValue * 1000) % 65536;
    }



}
