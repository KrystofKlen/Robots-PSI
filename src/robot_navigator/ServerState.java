package robot_navigator;

public enum ServerState {
    //AUTHENTIFICATION______________________________
    GETTING_USERNAME,
    GETTING_KEY_ID,
    CONFORMATION,

    //ROBOT MOVE____________________________________
    GETTING_POSITION,
    GETTING_DIRECTION,

    //FAIL
    FAIL
}
