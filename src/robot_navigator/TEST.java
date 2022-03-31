package robot_navigator;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.GETTING_KEY_ID;

public class TEST {


    public static void main(String[] args) {

        String msg = "OK -1 19 ";
        Scanner sc = new Scanner(msg);
        System.out.println(msg.matches("OK -?\\d+ -?\\d+"));

    }










}
