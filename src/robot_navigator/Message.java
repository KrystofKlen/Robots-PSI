package robot_navigator;

import java.util.ArrayList;
import java.util.List;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.GETTING_KEY_ID;
import static robot_navigator.ServerState.GETTING_USERNAME;

public class Message {
    private String message;

    /**
     * Takes a buffer from pipe and cuts it into separate messages
     * which are stored into List
     * @param buffer
     * @param messages list where messages will be stored
     * @return cut buffrer which will be either empty String when
     * we don't have to wait for another buffer because messages in buffer are correctly ended.
     * In case that a message from client got stuck in pipe and we have to wait until the rest
     * arrivet, buffer will contail the message which is not completed
     */
    public String partionMessage(String buffer, List<String> messages){

        //String currentBuffer = buffer;
        for(int i = 0; buffer.contains(END_MESSAGE) ; i++){
            String message = getEndOfCurrentMessage(buffer);
            messages.add(message);
            buffer = buffer.substring(message.length() + 4);
        }
        return buffer;
    }

    public String getEndOfCurrentMessage(String message){
        int endIndex = message.indexOf(END_MESSAGE);
        return message.substring(0,endIndex);
    }

    public boolean checkIfContainsValidMessage(String message){
        if(message.contains(END_MESSAGE)) return true;
        else return false;
    }

    public boolean checkMessageLength(int messageLength, ServerState currentState){
        if(currentState.equals(GETTING_USERNAME) && messageLength > USERNAME_MAX_LENGTH) return false;
        else return true;
    }

    public boolean checkMessageSyntax(String message, ServerState currentState){
        if(currentState.equals(GETTING_KEY_ID)){
            try{
                Integer.parseInt(message);
                return true;
            }catch (NumberFormatException ex){
                //message should contain numbers but it isn't
                return false;
            }
        }
        return true;
    }



}
