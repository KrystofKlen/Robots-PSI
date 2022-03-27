package robot_navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static robot_navigator.CONSTANTS.END_MESSAGE;
import static robot_navigator.ServerState.GETTING_KEY_ID;

public class TEST {


    public static void main(String[] args) {

        String str = "OK -6 7";
        System.out.println(str.matches("OK -\\d \\d"));

    }
}
