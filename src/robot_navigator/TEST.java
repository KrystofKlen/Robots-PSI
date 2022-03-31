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
        r.getCurrentPosition().setDirection(RobotDirection.LEFT);
        r.getCurrentPosition().setPosition(2,1);

        nv.moves.add("***************** move *********");
        nv.moves.add("***************** move *********");
        nv.moves.add("***************** move *********");


        r.getCurrentPosition().setPosition(1,1);

        nv.moveAroundObstacleAlongX(r.getCurrentPosition());
        nv.moves.forEach(str-> System.out.println(str));
    }










}
