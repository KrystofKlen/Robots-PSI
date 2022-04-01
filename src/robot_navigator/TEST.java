package robot_navigator;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.GETTING_KEY_ID;

public class TEST {


    public static void main(String[] args) {

        String msg = "RECHARGING";
        Pattern p = Pattern.compile("[RECH]");
        Matcher m = p.matcher(msg);
        if (m.find())
            System.out.println("found");

    }










}
