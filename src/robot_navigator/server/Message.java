package robot_navigator.server;

import robot_navigator.KeyOutOfRange;
import robot_navigator.Robot.Position;
import robot_navigator.SyntaxError;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.server.ServerState.*;

public class Message {

    public boolean checkMessageLength(int messageLength, ServerState currentState){
        if(!currentState.equals(PICKUP) && messageLength > REGULAR_MESSAGE_MAX_LENGTH) return false;
        if(currentState.equals(GETTING_USERNAME) && messageLength >= USERNAME_MAX_LENGTH) return false;
        if(currentState.equals(GETTING_KEY_ID) && messageLength > CLIENT_KEY_ID_MAX_MAX_LENGTH) return false;
        if((currentState.equals(FIRST_MOVE) ||
                currentState.equals(GETTING_POSITION) ||
                currentState.equals(GETTING_DIRECTION) ) &&
                messageLength > CLIENT_OK_MAX_LENGTH) return false;
        if(currentState.equals(PICKUP) && messageLength > CLIENT_FINAL_MESSAGE_MAX_LENGTH) return false;
        else return true;
    }

    public void checkKeyLogic(String message, ServerState currentState) throws SyntaxError, KeyOutOfRange {
        if(currentState.equals(GETTING_KEY_ID)) {
            try {
                int key = Integer.parseInt(message);
                if (key < 0 || key > 4) throw new KeyOutOfRange(); //key out of range
            } catch (NumberFormatException ex) {
                //message should contain numbers but it isn't
                System.out.println("FAIL " + message);
                throw new SyntaxError();
            }
        }
    }

    public boolean readPosition(Position position, String message){
        try{
            Scanner sc = new Scanner(message);
            if(!message.matches("OK -?\\d+ -?\\d+")) return false;
            sc.skip(Pattern.compile("OK\\s+"));
            int x = sc.nextInt();
            int y = sc.nextInt();
            position.setPosition(x,y);
        }catch (NoSuchElementException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }


}
