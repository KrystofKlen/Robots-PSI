package robot_navigator.robot;

import java.util.Objects;

import static robot_navigator.robot.RobotDirection.*;


public class Position {
    private int x,y;
    private RobotDirection currentDirection;

    public Position(){currentDirection = null;}

    public Position(int x, int y){
        this.x = x;
        this.y = y;
        currentDirection = null;
    }

    public Position(Position position){
        this.x = position.getX();
        this.y = position.getY();
        currentDirection = position.getCurrentDirection();
    }

    public void setDirection(Position oldPosition) throws IllegalArgumentException{
        if(x > oldPosition.getX()){
            currentDirection = RIGHT;
        }else if(y > oldPosition.getY()){
            currentDirection = UP;
        }else if(x < oldPosition.getX()){
            currentDirection = LEFT;
        }else if(y < oldPosition.getY()){
            currentDirection = DOWN;
        }else{
            throw new IllegalArgumentException("ROBOT WAS NOT MOVED, IT HIT OBSTACLE");
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
