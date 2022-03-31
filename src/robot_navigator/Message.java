package robot_navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.*;

public class Message {
    private String message;

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

    public boolean checkMessageLogic(String message, ServerState currentState) throws SyntaxError, KeyOutOfRange{
        if(currentState.equals(GETTING_KEY_ID)){
            try{
                int key = Integer.parseInt(message);
                if(key < 0 || key > 4) throw new KeyOutOfRange(); //key out of range
                return true;
            }catch (NumberFormatException ex){
                //message should contain numbers but it isn't
                System.out.println("FAIL " + message);
                throw new SyntaxError();
            }
        }
        if(currentState.equals(GETTING_POSITION) ||
        currentState.equals(GETTING_DIRECTION) ||
        currentState.equals(NAVIGATING_TO_X)){
            return message.matches("OK \\d+ \\d+") || message.matches("OK -\\d+ \\d+")
                    || message.matches("OK \\d+ -\\d+") || message.matches("OK -\\d+ -\\d+");
        }

        return true;
    }

    public boolean readPosition(Position position, String message){
        try{
            Scanner sc = new Scanner(message);
            if(!message.matches("OK -?\\d+ -?\\d+")) return false;
            sc.skip(Pattern.compile("OK\\s+"));
            int x = sc.nextInt();
            int y = sc.nextInt();
            position.setPosition(x,y);
            System.out.println("READ: "+ x+" "+y);
        }catch (NoSuchElementException ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }




}
