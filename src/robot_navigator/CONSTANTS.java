package robot_navigator;

public class CONSTANTS {
    //____________MESSAGE__________________
    public static final String END_MESSAGE = "\\a\\b";
    public static final String MESSAGE_ENDING_PATTERN = ".*\\a\\b";

    //_________________CLIENT AUTHENTIFICATION__________________
    public static final int USERNAME_MAX_LENGTH = 20;
    public static final int CLIENT_KEY_ID_MAX_MAX_LENGTH = 5;

    //___________________SERVER___________________
    public static final int TIMEOUT_MESSAGE_MILLIS = 30000;

    public static final String SERVER_KEY_REQUEST = "107 KEY REQUEST\\a\\b";
    public static final String SERVER_OK = "OK\\a\\b";
    public static final String SERVER_LOGIN_FAILED = "300 LOGIN FAILED\\a\\b";
    public static final String SERVER_SYNTAX_ERROR = "301 SYNTAX ERROR\\a\\b";
    public static final String SERVER_LOGIC_ERROR = "302 LOGIC ERROR\\a\\b";
    public static final String SERVER_KEY_OUT_OF_RANGE_ERROR = "303 KEY OUT OF RANGE\\a\\b";




}
