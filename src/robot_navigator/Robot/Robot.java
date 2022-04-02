package robot_navigator.Robot;


import robot_navigator.CONSTANTS;

import static robot_navigator.Robot.RobotDirection.*;

public class Robot {

    private Position currentPosition;
    private String clientUsername;
    private int hash;
    private int keyID;
    public int numberOfCrashes;

    public Robot(String clientUsername) {
        this.currentPosition = new Position();
        this.clientUsername = clientUsername;
        numberOfCrashes = 0;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    public void updatePosition(Navigator navigator, String command){

        Object [][] move = {{UP,0,1},{DOWN,0,-1},{RIGHT,1,0},{LEFT, -1,0}};
        if(command.equals(CONSTANTS.SERVER_MOVE)){
            int i;
            for(i = 0; !currentPosition.getCurrentDirection().equals(move[i][0]); i++);
            currentPosition.setPosition(
                    currentPosition.getX() + (int) move[i][1],
                    currentPosition.getY() + (int) move[i][2]
            );
        }
        else currentPosition
                .setDirection(navigator.getDirection(command,currentPosition.getCurrentDirection()));
    }

    public int getHash(){
        return this.hash;
    }

    public int getKeyID() {
        return keyID;
    }

    public boolean setKeyID(int keyID){
        if(keyID > -9999 && keyID < 9999){
            this.keyID = keyID;
            return true;
        }
        return false;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

}
