package robot_navigator.robot;

import java.util.*;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.robot.RobotDirection.*;

public class Navigator {

    private Robot robot;
    public List<String> moves;
    private boolean needToCheckMove = false;
    public List<Position> obstacles;
    public int movesNecessaryToDriveAroundY = 0;
    public int movesNecessaryToDriveAroundX = 0;

    public Navigator(Robot robot, Position anticipatedPosition){
       this.robot = robot;
       this.moves = new ArrayList<>();
       this.obstacles = new ArrayList<>();

    }

    /**
     * adds to moves steps to avoid obstacle which is located in front of robot
     * usage: movements along x axes (towards y)
     * @param currentPosition
     */
    public void moveAroundObstacleAlongX(Position currentPosition){
        int x = currentPosition.getX();
        int y = currentPosition.getY();
        RobotDirection direction = currentPosition.getCurrentDirection();

        List<String> goFullyAround = List.of(SERVER_TURN_LEFT, SERVER_MOVE, SERVER_TURN_RIGHT,SERVER_MOVE,
                SERVER_MOVE,SERVER_TURN_RIGHT, SERVER_MOVE,SERVER_TURN_LEFT);

        //there is a special function to make the final turn towards y axes
        List<String> goHalfWayLR = List.of(SERVER_TURN_LEFT, SERVER_MOVE,SERVER_TURN_RIGHT,SERVER_MOVE );
        List<String> goHalfWayRL = List.of(SERVER_TURN_RIGHT, SERVER_MOVE,SERVER_TURN_LEFT, SERVER_MOVE);

        if(Math.abs(x) - 1 == 0 && direction.equals(LEFT)) {

            //moves.addAll(y>0 ? goHalfWayLR: goHalfWayRL);
            for(int i = 0; i<4; i++){
                moves.add(i,y>0 ? goHalfWayLR.get(i) : goHalfWayRL.get(i));
            }
            return;
        }
        if(Math.abs(x) - 1 == 0 && direction.equals(RIGHT) ) {
            //moves.addAll(y > 0 ? goHalfWayRL : goHalfWayLR);
            for(int i = 0; i<4; i++){
                moves.add(i,y>0 ? goHalfWayRL.get(i) : goHalfWayLR.get(i));
            }
            return;
        }

        for(int i = 0; i<8; i++){
            moves.add(i,goFullyAround.get(i));
        }
        movesNecessaryToDriveAroundX = 7;
    }

    /**
     * usage: for moving along x axes (towrds mid)
     * @param currentPosition
     */
    public void moveAroundObstacleAlongY(Position currentPosition){

        List<String> goFullyAround = List.of(SERVER_TURN_LEFT, SERVER_MOVE,SERVER_TURN_RIGHT,
                SERVER_MOVE,SERVER_MOVE,SERVER_TURN_RIGHT,SERVER_MOVE, SERVER_TURN_LEFT);
        for(int i = 0; i<8; i++){
            moves.add(i,goFullyAround.get(i));
        }
        movesNecessaryToDriveAroundY = 7;
    }

    public void setCheckFlag(String command){
        if(command.equals(SERVER_MOVE)){
            needToCheckMove = true;
        }else{
            needToCheckMove = false;
        }
    }

    /**
     * Checks if previous move was successfull (confirmed by client)
     * @param positionFromClient
     * @return true = success, false = not
     */
    public boolean checkMove(Position positionFromClient){
       if(!needToCheckMove) return true;
       if(!positionFromClient.equals(robot.getCurrentPosition())) return false;
       else return true;
    }

    /**
     * Return new robots direction after it turned
     * @param msg
     * @param currentDirection
     * @return Direction in which robot is situated
     */
    public RobotDirection getDirection(String msg, RobotDirection currentDirection){
        RobotDirection [] arr = {UP,RIGHT,DOWN,LEFT,UP,RIGHT};
        int i;
        for(i = 1; i< arr.length - 1 && !arr[i].equals(currentDirection);i++);
        return msg.equals(SERVER_TURN_LEFT) ? arr[i - 1] : arr[i + 1];
    }

    public void turnTowardsY(Position startPosition){

        //already in good direction (located on y axes)
        if( startPosition.getX()==0 ||
                (startPosition.getCurrentDirection().equals(LEFT) && startPosition.getX() > 0) ||
                (startPosition.getCurrentDirection().equals(RIGHT) && startPosition.getX() < 0)
        ) return;

        //turn towards y axes to lower x
        Object [][] arr = {{UP,1},{RIGHT,2},{DOWN,1},{LEFT,2}};
        int i;
        for(i = 0; i<arr.length && !arr[i][0].equals(startPosition.getCurrentDirection()); i++);
        if(arr[i][0].equals(UP)) {
            moves.add(startPosition.getX() > 0 ? SERVER_TURN_LEFT : SERVER_TURN_RIGHT);
            return;
        }
        if(arr[i][0].equals(DOWN)) {
            moves.add(startPosition.getX() > 0 ? SERVER_TURN_RIGHT : SERVER_TURN_LEFT);
            return;
        }
        for(int j = 0; j < (int) arr[i][1]; j++)
            moves.add(startPosition.getX() > 0 ? SERVER_TURN_LEFT : SERVER_TURN_RIGHT);
    }

    public void turnTowardsX(Position currentPosition){

        //already in good direction (located on x axes)
        if( currentPosition.getY()==0 ) return;

        //turn towards x axes to lower y
        Object [][] arr = {{UP,2},{RIGHT,1},{DOWN,2},{LEFT,1}};
        int i;
        for(i = 0; i<arr.length && !arr[i][0].equals(currentPosition.getCurrentDirection()); i++);
        if(arr[i][0].equals(LEFT)) {
            moves.add(currentPosition.getY() > 0 ? SERVER_TURN_LEFT : SERVER_TURN_RIGHT);
            return;
        }
        else if(arr[i][0].equals(RIGHT)) {
            moves.add(currentPosition.getY() > 0 ? SERVER_TURN_RIGHT : SERVER_TURN_LEFT);
            return;
        }
        else if((arr[i][0].equals(UP) && currentPosition.getY() > 0) || (arr[i][0].equals(DOWN) && currentPosition.getY() < 0)) {
            moves.add(SERVER_TURN_LEFT);
            moves.add(SERVER_TURN_LEFT);
        }
    }

    public void addStraightMovesAlongX(Position currentPosition){
        for(int i = 0; i < Math.abs(currentPosition.getX()) ; i++)
            moves.add(SERVER_MOVE);
    }

    public void addStraightMovesAlongY(Position currentPosition){
        for(int i = 0; i < Math.abs(currentPosition.getY()) ; i++)
            moves.add(SERVER_MOVE);
    }
}
