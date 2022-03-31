package robot_navigator;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.regex.Pattern;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.ServerState.GETTING_KEY_ID;

public class TEST {


    public static void main(String[] args) {


        Robot r = new Robot("R");
        Navigator nv = new Navigator(r,null);
        r.getCurrentPosition().setDirection(RobotDirection.UP);
        r.getCurrentPosition().setPosition(2,1);
        r.updatePosition(nv,SERVER_TURN_RIGHT);
        r.updatePosition(nv,SERVER_MOVE);
        r.printRobotInfo();

    }










}
