package robot_navigator;


public class Position {
    private int x,y;
    private RobotDirection currentDirection;

    public Position(){currentDirection = null;}

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public RobotDirection getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(RobotDirection currentDirection) {
        this.currentDirection = currentDirection;
    }
}
