package robot_navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static robot_navigator.CONSTANTS.END_MESSAGE;
import static robot_navigator.ServerState.GETTING_KEY_ID;

public class TEST {

    public static void checkMessageSyntax(String message, ServerState currentState){
        if(currentState.equals(GETTING_KEY_ID)){
            try{
                int i = Integer.parseInt(message);
                System.out.println(i);
            }catch (ArithmeticException ex){ex.printStackTrace();}
        }
    }

    public static void main(String[] args) {
        String msg = "-12-34";
        checkMessageSyntax(msg,GETTING_KEY_ID);
    }
}
