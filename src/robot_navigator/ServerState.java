package robot_navigator;

public enum ServerState {
    //AUTHENTIFICATION______________________________
    GETTING_USERNAME,
    GETTING_KEY_ID,
    CONFORMATION,

    //ROBOT MOVE____________________________________
    FIRST_MOVE,
    SECOND_MOVE,

    //FAIL
    FAIL
}
