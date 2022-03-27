package robot_navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static robot_navigator.CONSTANTS.END_MESSAGE;
import static robot_navigator.ServerState.GETTING_KEY_ID;

public class TEST {


    public static void main(String[] args) {
        Robot robot1 = new Robot("ROBOT_1");
        robot1.setKeyID(10);
        robot1.setHash(11);
        robot1.initPosition(0,0);
        robot1.initDirection(RobotDirection.UP);
        robot1.printRobotInfo();
        robot1.moveRight();
        robot1.printRobotInfo();
        robot1.moveRight();
        robot1.printRobotInfo();
        robot1.moveRight();
        robot1.printRobotInfo();
        robot1.moveRight();
        robot1.printRobotInfo();
        String str = "string\u0007\u0008";
        System.out.println(END_MESSAGE.length());
    }
}
