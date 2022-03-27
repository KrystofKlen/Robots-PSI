package robot_navigator;


import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.*;



public class ServerStateMachine {

    private static final int SERVER = 0;
    private static final int CLIENT = 1;

    private ServerState currentState;
    private Queue<String> messagesFromClient;
    private Robot robot;
    private Message msg;
    private Navigator navigator;
    private final int [][] keys= { {23019, 32037, 18789, 16443, 18189},{32037, 	29295, 	13603, 29533, 21952}};

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
            case CONFORMATION -> currentState = FIRST_MOVE;
            case FIRST_MOVE -> currentState = GETTING_POSITION;
            case GETTING_POSITION -> currentState = GETTING_DIRECTION;
            case GETTING_DIRECTION -> currentState = NAVIGATING;
            case NAVIGATING -> currentState = PICKUP;
        }
        System.out.println("********** STATE CHANGED, CURRENT STATE = " + currentState.toString() + "****************");
    }

    public String respondToMessage(){
        if(! msg.checkMessageLength(
                messagesFromClient.peek() != null ? messagesFromClient.peek().length() : 0,
                currentState)){
            currentState = FAIL;
            return SERVER_SYNTAX_ERROR;
        }
        //no response if no message from client
        if(currentState.equals(FIRST_MOVE)) {
            changeServerState();
            return SERVER_MOVE;
        }

        if(currentState.equals(GETTING_POSITION)) {
            if(msg.readPosition(robot.getCurrentPosition(), messagesFromClient.poll())){
                robot.printRobotInfo();
                changeServerState();
                return SERVER_MOVE;
            }else{
                currentState = FAIL;
                return SERVER_SYNTAX_ERROR;
            }
        }


        if(messagesFromClient.isEmpty()) return "";
        if(messagesFromClient.peek().equals("")) return "";
        if(! msg.checkMessageLength(messagesFromClient.peek().length(),currentState)){
            currentState = FAIL;
            return SERVER_SYNTAX_ERROR;
        }
        if(! msg.checkMessageLogic(messagesFromClient.peek(),currentState)){
            currentState = FAIL;
            return SERVER_KEY_OUT_OF_RANGE_ERROR;
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
            robot.setHash(countHash(robot.getKeyID(), SERVER));
            changeServerState();
            return String.valueOf(robot.getHash()) + END_MESSAGE;
        }
        if(currentState.equals(CONFORMATION)){
            try{
                if(checkHash(robot.getKeyID(), Integer.parseInt(messagesFromClient.poll()))){
                    changeServerState();
                    return SERVER_OK;
                }else{
                    currentState = FAIL;
                    return SERVER_LOGIN_FAILED;
                }
            } catch (NumberFormatException ex){ex.printStackTrace();}

        }
        if(currentState.equals(GETTING_DIRECTION)){
            Position oldPosition = new Position(robot.getCurrentPosition());
            if(msg.readPosition(robot.getCurrentPosition(), messagesFromClient.poll())){
                robot.getCurrentPosition().setDirection(oldPosition);

                //robot intented to move forward
                navigator = new Navigator(robot, robot.getCurrentPosition());
                robot.printRobotInfo();

                changeServerState();
                return SERVER_MOVE;
            }else{
                currentState = FAIL;
                return SERVER_SYNTAX_ERROR;
            }
        }
        if(currentState.equals(NAVIGATING)){
            //set robots posirtiopn from client's message
            msg.readPosition(robot.getCurrentPosition(),messagesFromClient.poll());
            //check if the move was successfull
            navigator.validatePreviousMoveSuccess();
            //move to next position
            String responce = navigator.exploreNext();
            if(responce.equals(SERVER_PICK_UP)) changeServerState();
            return responce;
        }
        else return SERVER_LOGOUT;
    }

    public void putToQueue(List<String> messages){
        messagesFromClient.addAll(messages);
    }

    private int countHash(int keyID, int FLAG_SERVER_OR_CLIENT){
        int asciiValue = 0;
        char [] userName  = robot.getClientUsername().toCharArray();
        for (char c : userName) {
            asciiValue+= (int) c;
        }
        //System.out.println("ASCII: " + asciiValue);
        int hash = (asciiValue * 1000) % 65536;
        return (hash + keys[FLAG_SERVER_OR_CLIENT][keyID]) % 65536;
    }

    /**
     * Counts user hash
     * @param keyID
     * @return
     */
    private boolean checkHash(int keyID, int conformHashFromClient){
        return countHash(keyID, CLIENT) == conformHashFromClient;
    }



}
