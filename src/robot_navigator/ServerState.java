package robot_navigator;

public enum ServerState {
    //AUTHENTIFICATION______________________________
    GETTING_USERNAME,
    GETTING_KEY_ID,
    CONFORMATION,

    //ROBOT MOVE____________________________________
    FIRST_MOVE,
    GETTING_POSITION,
    GETTING_DIRECTION,
    NAVIGATING,

    //ROBOT REACHED MID
    PICKUP,

    //FAIL
    FAIL
}
