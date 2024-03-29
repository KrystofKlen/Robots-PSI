package robot_navigator.server;

import robot_navigator.*;
import robot_navigator.robot.Navigator;
import robot_navigator.robot.Position;
import robot_navigator.robot.Robot;

import java.util.*;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.server.ServerState.*;

public class ServerStateMachine {

    private static final int SERVER = 0;
    private static final int CLIENT = 1;

    private ServerState currentState;
    private Queue<String> messagesFromClient;
    private Robot robot;
    private Message msg;
    private Navigator navigator;
    private final int [][] keys= { {23019, 32037, 18789, 16443, 18189},{32037, 	29295, 	13603, 29533, 21952}};
    public boolean firstTurnIFObstacleAheadDone = false;

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
            case GETTING_DIRECTION -> currentState = DIRECTING_TOWARDS_X;
            case DIRECTING_TOWARDS_X -> currentState = NAVIGATING;
            case NAVIGATING -> currentState = LOG_OUT;
        }
        System.out.println("********** STATE CHANGED, CURRENT STATE = " + currentState.toString() + "****************");
    }

    public String respondToMessage() {

        //we don't check message but make direct move
        if(currentState.equals(FIRST_MOVE)) {
            changeServerState();
            return SERVER_MOVE;
        }

        if(! msg.checkMessageLength(messagesFromClient.peek().length(),currentState)){
            currentState = FAIL;
            return SERVER_SYNTAX_ERROR;
        }
        if(messagesFromClient.isEmpty()) return "";
        if(messagesFromClient.peek().equals("")) return "";

        if(currentState.equals(GETTING_POSITION)) {
            if(msg.readPosition(robot.getCurrentPosition(), messagesFromClient.poll())){
                changeServerState();
                return SERVER_MOVE;
            }else{
                currentState = FAIL;
                return SERVER_SYNTAX_ERROR;
            }
        }

        if(currentState.equals(GETTING_USERNAME)){
            String message = messagesFromClient.poll();
            robot = new Robot(message);
            changeServerState();
            return SERVER_KEY_REQUEST;
        }

        if(currentState.equals(GETTING_KEY_ID)){
            try{
                msg.checkKeyLogic(messagesFromClient.peek(),currentState);
                if (! robot.setKeyID(Integer.parseInt(messagesFromClient.poll()))){
                    currentState = FAIL;
                    return SERVER_KEY_OUT_OF_RANGE_ERROR;
                }
                robot.setHash(countHash(robot.getKeyID(), SERVER));
                changeServerState();
                return robot.getHash() + END_MESSAGE;

            }catch (KeyOutOfRange korex){
                currentState = FAIL;
                return SERVER_KEY_OUT_OF_RANGE_ERROR;
            }catch (SyntaxError seex){
                currentState = FAIL;
                return SERVER_SYNTAX_ERROR;
            }catch (NumberFormatException numex){
                return SERVER_SYNTAX_ERROR;
            }
        }

        if(currentState.equals(CONFORMATION)){
            try{
                int confirmationNum = Integer.parseInt(messagesFromClient.poll());
                if(confirmationNum > 99999) return SERVER_SYNTAX_ERROR;
                if(checkHash(robot.getKeyID(), confirmationNum)){
                    changeServerState();
                    return SERVER_OK;
                }else{
                    currentState = FAIL;
                    return SERVER_LOGIN_FAILED;
                }
            } catch (NumberFormatException ex){
                currentState = FAIL;
                return SERVER_SYNTAX_ERROR;
            }

        }

        if(currentState.equals(GETTING_DIRECTION)){
            Position oldPosition = new Position(robot.getCurrentPosition());
            if(msg.readPosition(robot.getCurrentPosition(), messagesFromClient.poll())){
                //position syntax ok
                try{
                    robot.getCurrentPosition().setDirection(oldPosition);
                    System.out.println("J");
                }catch (IllegalArgumentException ilex){
                    //ROBOT WAS NOT MOVED, IT HIT OBSTACLE
                    if(firstTurnIFObstacleAheadDone){
                        return SERVER_MOVE;
                    }else{
                        firstTurnIFObstacleAheadDone = true;
                        return SERVER_TURN_LEFT;
                    }
                }

                //robot intented to move forward
                navigator = new Navigator(robot, robot.getCurrentPosition());
                changeServerState();
            }else{
                currentState = FAIL;
                return SERVER_SYNTAX_ERROR;
            }
        }

        if(currentState.equals(DIRECTING_TOWARDS_X)){
            navigator.turnTowardsY(robot.getCurrentPosition());
            navigator.addStraightMovesAlongX(robot.getCurrentPosition());
            changeServerState();
        }

        if(currentState.equals(NAVIGATING)){
            //checking if robot reached mid
            if(robot.getCurrentPosition().equals(new Position(0,0))){
                changeServerState();
                return SERVER_PICK_UP;
            }
            //checking if previous move was ok
            Position clientPosition = new Position();

            if(!messagesFromClient.isEmpty()) {
                msg.readPosition(clientPosition,messagesFromClient.poll());
            }

            boolean moveWasOK = navigator.checkMove(clientPosition);
            if(!moveWasOK){
                //reset robots position
                robot.getCurrentPosition().setPosition(clientPosition.getX(), clientPosition.getY());
                //direction by robot same because it  was moving forward
            }

            if(navigator.moves.isEmpty() || robot.getCurrentPosition().getX() == 0
            || robot.getCurrentPosition().getY() == 0 || !moveWasOK){
                //we are at X = 0;
                if(robot.getCurrentPosition().equals(new Position(0,0))){
                    changeServerState();
                    return SERVER_PICK_UP;
                }
                else if(!moveWasOK && robot.getCurrentPosition().getX() != 0){
                    //we have a obstacle
                    navigator.moves.clear();
                    navigator.moveAroundObstacleAlongX(robot.getCurrentPosition());

                }else if(!moveWasOK && robot.getCurrentPosition().getX() == 0){
                    navigator.moves.clear();
                    navigator.moveAroundObstacleAlongY(robot.getCurrentPosition());
                }
                else if(robot.getCurrentPosition().getX() == 0 && navigator.movesNecessaryToDriveAroundY == 0){
                    navigator.moves.clear();
                    navigator.turnTowardsX(robot.getCurrentPosition());
                    navigator.addStraightMovesAlongY(robot.getCurrentPosition());
                }else if(robot.getCurrentPosition().getX() != 0 && navigator.movesNecessaryToDriveAroundX == 0){
                    navigator.moves.clear();
                    navigator.turnTowardsY(robot.getCurrentPosition());
                    navigator.addStraightMovesAlongX(robot.getCurrentPosition());
                }
            }

            //executing next move
            if(navigator.movesNecessaryToDriveAroundY > 0) navigator.movesNecessaryToDriveAroundY--;
            if(navigator.movesNecessaryToDriveAroundX > 0) navigator.movesNecessaryToDriveAroundX--;
            String plannedCommand = navigator.moves.get(0);
            navigator.setCheckFlag(plannedCommand);
            robot.updatePosition(navigator, plannedCommand);
            navigator.moves.remove(0);
            return plannedCommand;
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
