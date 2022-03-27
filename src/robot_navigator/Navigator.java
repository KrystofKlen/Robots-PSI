package robot_navigator;

import java.util.LinkedList;
import java.util.List;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.RobotDirection.*;

public class Navigator {
    private List<Position> obstacles;
    private Position previousPosition;
    private Robot robot;

   public Navigator(Robot robot, Position positionBeforeMove){
       obstacles = new LinkedList<>();
       this.robot = robot;
       previousPosition = positionBeforeMove;
   }

   public void validatePreviousMoveSuccess(){

       if(previousPosition.equals(robot.getCurrentPosition())){
           //move not managed
           obstacles.add(robot.getCurrentPosition());
           robot.getCurrentPosition().setDirection(previousPosition.getCurrentDirection());
           robot.getCurrentPosition().setPosition(previousPosition.getX(), previousPosition.getY());
           return;
       }
       //move managed everything is set correctly

   }

    private void updatePreviousMove(Position position){
        previousPosition = position;
    }

    public String exploreNext(){
        int x = robot.getCurrentPosition().getX();
        int y = robot.getCurrentPosition().getY();
        RobotDirection direction = robot.getCurrentPosition().getCurrentDirection();

       //match with X axes
        if(x > 0){
            switch (direction){
                case UP -> {
                    robot.moveLeft();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_LEFT;
                }
                case DOWN -> {
                    robot.moveRight();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_RIGHT;
                }
                case LEFT -> {
                    robot.moveForward();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_MOVE;
                }
                case RIGHT -> {
                    robot.moveLeft();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_LEFT;
                }
            }
        }else if(x < 0){
            switch (direction){
                case UP -> {
                    robot.moveRight();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_RIGHT;
                }
                case DOWN -> {
                    robot.moveLeft();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_LEFT;
                }
                case LEFT -> {
                    robot.moveLeft();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_LEFT;
                }
                case RIGHT -> {
                    robot.moveForward();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_MOVE;
                }
            }
        }


        //match with Y axes
        if(y > 0){
            switch (direction){
                case UP -> {
                    robot.moveRight();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_RIGHT;
                }
                case DOWN -> {
                    robot.moveForward();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_MOVE;
                }
                case LEFT -> {
                    robot.moveLeft();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_LEFT;
                }
                case RIGHT -> {
                    robot.moveRight();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_RIGHT;
                }
            }
        } else if(y < 0){
            switch (direction){
                case UP -> {
                    robot.moveForward();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_MOVE;
                }
                case DOWN -> {
                    robot.moveLeft();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_LEFT;
                }
                case LEFT -> {
                    robot.moveRight();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_RIGHT;
                }
                case RIGHT -> {
                    robot.moveLeft();
                    updatePreviousMove(robot.getCurrentPosition());
                    return SERVER_TURN_LEFT;
                }
            }
        }

        return SERVER_PICK_UP;
    }




}
