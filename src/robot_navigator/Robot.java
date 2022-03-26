package robot_navigator;


import static robot_navigator.RobotDirection.*;

public class Robot {

    private Position currentPosition;
    private String clientUsername;
    private int hash;
    private int keyID;

    public Robot(String clientUsername) {
        this.currentPosition = null;
        this.clientUsername = clientUsername;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public void initPosition(int x, int y){
        currentPosition = new Position();
        currentPosition.setPosition(x,y);
    }

    public void initDirection(RobotDirection direction){
        currentPosition.setCurrentDirection(direction);
    }

    public void moveLeft(){
        if(currentPosition == null) return;

        switch (currentPosition.getCurrentDirection()){
            case UP -> {
                currentPosition.setPosition(currentPosition.getX() - 1, currentPosition.getY());
                currentPosition.setCurrentDirection(LEFT);
            }
            case DOWN -> {
                currentPosition.setPosition(currentPosition.getX() + 1, currentPosition.getY());
                currentPosition.setCurrentDirection(RIGHT);
            }
            case LEFT -> {
                currentPosition.setPosition(currentPosition.getX(), currentPosition.getY() - 1);
                currentPosition.setCurrentDirection(DOWN);
            }
            case RIGHT -> {
                currentPosition.setPosition(currentPosition.getX(), currentPosition.getY() + 1);
                currentPosition.setCurrentDirection(UP);
            }
        }
    }

    public void moveRight(){
        if(currentPosition == null) return;

        switch (currentPosition.getCurrentDirection()){
            case UP -> {
                currentPosition.setPosition(currentPosition.getX() + 1, currentPosition.getY());
                currentPosition.setCurrentDirection(RIGHT);
            }
            case DOWN -> {
                currentPosition.setPosition(currentPosition.getX() - 1, currentPosition.getY());
                currentPosition.setCurrentDirection(LEFT);
            }
            case LEFT -> {
                currentPosition.setPosition(currentPosition.getX() , currentPosition.getY() + 1);
                currentPosition.setCurrentDirection(UP);
            }
            case RIGHT -> {
                currentPosition.setPosition(currentPosition.getX() , currentPosition.getY() - 1);
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
