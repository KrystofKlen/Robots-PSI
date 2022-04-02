package robot_navigator.server;

public enum ServerState {
    //AUTHENTIFICATION______________________________
    GETTING_USERNAME,
    GETTING_KEY_ID,
    CONFORMATION,

    //ROBOT MOVE____________________________________
    FIRST_MOVE,
    GETTING_POSITION,
    GETTING_DIRECTION,
    DIRECTING_TOWARDS_X,
    NAVIGATING,

    //ROBOT REACHED MID
    PICKUP,
    LOG_OUT,

    //FAIL
    FAIL
}
