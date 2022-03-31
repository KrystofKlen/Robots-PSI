package robot_navigator;


import static robot_navigator.RobotDirection.*;

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

    public void moveLeft(){
        if(currentPosition == null) return;

        switch (currentPosition.getCurrentDirection()){
            case UP -> {
                currentPosition.setCurrentDirection(LEFT);
            }
            case DOWN -> {
                currentPosition.setCurrentDirection(RIGHT);
            }
            case LEFT -> {
                currentPosition.setCurrentDirection(DOWN);
            }
            case RIGHT -> {
                currentPosition.setCurrentDirection(UP);
            }
        }
    }

    public void moveRight(){
        if(currentPosition == null) return;

        switch (currentPosition.getCurrentDirection()){
            case UP -> {
                currentPosition.setCurrentDirection(RIGHT);
            }
            case DOWN -> {
                currentPosition.setCurrentDirection(LEFT);
            }
            case LEFT -> {
                currentPosition.setCurrentDirection(UP);
            }
            case RIGHT -> {
                currentPosition.setCurrentDirection(DOWN);
            }
        }
    }

    public void moveForward(){
        if(currentPosition == null) return;

        switch (currentPosition.getCurrentDirection()){
            case UP -> {
                currentPosition.setPosition(currentPosition.getX(), currentPosition.getY() + 1);
            }
            case DOWN -> {
                currentPosition.setPosition(currentPosition.getX(), currentPosition.getY() - 1);
            }
            case LEFT -> {
                currentPosition.setPosition(currentPosition.getX() - 1, currentPosition.getY());
            }case RIGHT -> {
                currentPosition.setPosition(currentPosition.getX(), currentPosition.getY() + 1);
            }
        }
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

    //helper functions----------------------------------------
    public void printRobotInfo(){
        System.out.println("_____________________________");
        if(currentPosition == null){
            System.out.println("username: " + clientUsername + "\n keyID: " + keyID + "No position\n no direction");
            return;
        }
        if(currentPosition.getCurrentDirection() == null){
            System.out.println("username: " + clientUsername + "\n keyID: " + keyID + "\n [" +
                    currentPosition.getX() + "," + currentPosition.getY() + "]"  + "\n no direction");
            return;
        }
        System.out.println("username: " + clientUsername + "\n keyID: " + keyID + "\n [" +
                currentPosition.getX() + "," + currentPosition.getY() + "]"  + "\n direction: " +
                currentPosition.getCurrentDirection().toString());

    }

}
