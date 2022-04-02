package robot_navigator;

public class CONSTANTS {
    //____________MESSAGE__________________
    public static final String END_MESSAGE = "\u0007\u0008";
    public static final String MESSAGE_ENDING_PATTERN = ".*\\a\\b";

    //_________________CLIENT AUTHENTIFICATION__________________
    public static final int USERNAME_MAX_LENGTH = 18;
    public static final int CLIENT_KEY_ID_MAX_MAX_LENGTH = 5;
    public static final int CLIENT_OK_MAX_LENGTH = 12;
    public static final int REGULAR_MESSAGE_MAX_LENGTH = 20;
    public static final int CLIENT_FINAL_MESSAGE_MAX_LENGTH = 100;
    public static final int CONFORMATION_MAX_LENGTH = 7;

    //__________________CHARGING__________________
    public static final String CLIENT_FULL_POWER = "FULL POWER";
    public static final String CLIENT_RECHARGING = "RECHARGING";

    //___________________SERVER___________________
    public static final int TIMEOUT_MESSAGE_MILLIS = 1000;
    public static final int TIMEOUT_CHARGING_MILLIS = 5000;

    public static final String SERVER_KEY_REQUEST = "107 KEY REQUEST" + END_MESSAGE;
    public static final String SERVER_OK = "200 OK" + END_MESSAGE;
    public static final String SERVER_MOVE = "102 MOVE" + END_MESSAGE;
    public static final String SERVER_TURN_LEFT = "103 TURN LEFT" + END_MESSAGE;
    public static final String SERVER_TURN_RIGHT = "104 TURN RIGHT" + END_MESSAGE;
    public static final String SERVER_PICK_UP = "105 GET MESSAGE" + END_MESSAGE;
    public static final String SERVER_LOGOUT = "106 LOGOUT" + END_MESSAGE;


    public static final String SERVER_LOGIN_FAILED = "300 LOGIN FAILED" + END_MESSAGE;
    public static final String SERVER_SYNTAX_ERROR = "301 SYNTAX ERROR" + END_MESSAGE;
    public static final String SERVER_LOGIC_ERROR = "302 LOGIC ERROR" + END_MESSAGE;
    public static final String SERVER_KEY_OUT_OF_RANGE_ERROR = "303 KEY OUT OF RANGE" + END_MESSAGE;
}
