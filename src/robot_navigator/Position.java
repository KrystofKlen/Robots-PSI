package robot_navigator;

import java.util.Objects;

import static robot_navigator.RobotDirection.*;


public class Position {
    private int x,y;
    private RobotDirection currentDirection;
    public String command;

    public Position(){currentDirection = null;}
    public Position(int x, int y){
        this.x = x;
        this.y = y;
        currentDirection = null;
    }
    public Position(int x, int y, RobotDirection direction, String command){
        this.x = x;
        this.y = y;
        currentDirection = direction;
        this.command = command;
    }
    public Position(Position position){
        this.x = position.getX();
        this.y = position.getY();
        currentDirection = position.getCurrentDirection();
    }

    public void setDirection(Position oldPosition){
        if(x > oldPosition.getX()){
            currentDirection = RIGHT;
        }else if(y > oldPosition.getY()){
            currentDirection = UP;
        }else if(x < oldPosition.getX()){
            currentDirection = LEFT;
        }else{
            currentDirection = DOWN;
        }
    }
    public void setDirection(RobotDirection direction){
        this.currentDirection = direction;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
