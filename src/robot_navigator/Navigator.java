package robot_navigator;

import java.util.*;
import java.util.stream.Collectors;

import static robot_navigator.CONSTANTS.*;
import static robot_navigator.RobotDirection.*;

public class Navigator {

    private Robot robot;
    public List<String> moves;
    private boolean needToCheckMove = false;
    public List<Position> obstacles;



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
       // moves.addAll(goFullyAround);
    }

    /**
     * usage: for moving along x axes (towrds mid)
     * @param currentPosition
     */
    public void moveAroundObstacleAlongY(Position currentPosition){
        List<String> goFullyAround = List.of(SERVER_TURN_LEFT, SERVER_MOVE, SERVER_TURN_RIGHT,SERVER_MOVE, SERVER_TURN_LEFT, SERVER_MOVE);
        moves.addAll(goFullyAround);
    }

   public void setCheckFlag(String command){
        if(command.equals(SERVER_MOVE)){
            needToCheckMove = true;
        }else{
            needToCheckMove = false;
        }
   }

   public boolean checkMove(Position positionFromClient){
       if(!needToCheckMove) return true;
       if(!positionFromClient.equals(robot.getCurrentPosition())) return false;
       else return true;
   }

    /**
     * Return new robots direction after it turned
     * @param msg
     * @param currentDirection
     * @return
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

        //turn towards x axes to lower y, when robot arrives comes either from left or from right to Y axes
        Object [][] arr = {{UP,2},{RIGHT,1},{DOWN,2},{LEFT,1}};
        int i;
        for(i = 0; i<arr.length && !arr[i][0].equals(currentPosition.getCurrentDirection()); i++);
        if(arr[i][0].equals(LEFT)) {
            moves.add(currentPosition.getY() > 0 ? SERVER_TURN_LEFT : SERVER_TURN_RIGHT);
            return;
        }
        if(arr[i][0].equals(RIGHT)) {
            moves.add(currentPosition.getY() > 0 ? SERVER_TURN_RIGHT : SERVER_TURN_LEFT);
            return;
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

   /*public boolean validatePreviousMoveSuccess(Position positionFromClient){
       if(movesToCheck.isEmpty()) return true;

       Position anticipated = movesToCheck.poll();
       System.out.println("anticipated: " + anticipated.getX() + " " + anticipated.getY());
       if(positionFromClient.equals(anticipated)) return true;

       isObstacle[anticipated.getX()][anticipated.getY()] = true;
       return false;
   }

   public void setDirection(){
       int x = robot.getCurrentPosition().getX();
       int y = robot.getCurrentPosition().getY();
       RobotDirection direction = robot.getCurrentPosition().getCurrentDirection();

       if(x>0){
           switch (direction){
               case UP -> movesPlan.add(SERVER_TURN_LEFT);
               case DOWN -> movesPlan.add(SERVER_TURN_RIGHT);
               case RIGHT -> {
                   for(int i = 0; i<2; i++, movesPlan.add(SERVER_TURN_LEFT));
               }
               case LEFT -> movesPlan.add(SERVER_MOVE);
           }
       }else if(x<0){
           switch (direction){
               case UP -> movesPlan.add(SERVER_TURN_RIGHT);
               case DOWN -> movesPlan.add(SERVER_TURN_LEFT);
               case LEFT -> {
                   for(int i = 0; i<2; i++, movesPlan.add(SERVER_TURN_LEFT));
               }
               case RIGHT -> movesPlan.add(SERVER_MOVE);
           }
       }
   }

   public void setMoves(){
       int x = robot.getCurrentPosition().getX();
       int y = robot.getCurrentPosition().getY();
       RobotDirection direction = robot.getCurrentPosition().getCurrentDirection();

       for(int i = 0; i < Math.abs(x); i++){
           movesPlan.add(SERVER_MOVE);
       }

       if(y>0 && x>0) {
           movesPlan.add(SERVER_TURN_LEFT);
       }else if(y>0 && x<0) {
           movesPlan.add(SERVER_TURN_RIGHT);
       }else if (y<0 && x<0){
           movesPlan.add(SERVER_TURN_LEFT);
       } else if (y<0 && x>0){
           movesPlan.add(SERVER_TURN_RIGHT);
       }

       for(int i = 0; i < Math.abs(y); i++){
           movesPlan.add(SERVER_MOVE);
       }

       movesPlan.add(SERVER_PICK_UP);
       for (String str: movesPlan) System.out.println(str);
   }




















   public void goAroundObstacle(){
       movesPlan.add(SERVER_TURN_LEFT);
       movesPlan.add(SERVER_MOVE);
       movesPlan.add(SERVER_TURN_RIGHT);
       movesPlan.add(SERVER_MOVE);
       movesPlan.add(SERVER_TURN_RIGHT);
       movesPlan.add(SERVER_MOVE);
       movesPlan.add(SERVER_TURN_LEFT);
   }

   public void updateRobotPosition(){
       robot.getCurrentPosition().setPosition(anticipatedPosition.getX(),
               anticipatedPosition.getY());
   }

   public boolean robotMoved(){
        if(robotWasMovedForward &&
        !anticipatedPosition.equals(robot.getCurrentPosition())
        ){
            obstacles.add(anticipatedPosition);
            return false;
        }
        return true;
   }

   public String executeMove(){
        String msg = movesPlan.get(0);
        movesPlan.remove(msg);
       RobotDirection direction = robot.getCurrentPosition().getCurrentDirection();
        if(msg.equals(SERVER_MOVE)){

            switch (direction){
                case UP -> anticipatedPosition.setPosition(
                        robot.getCurrentPosition().getX(),
                        robot.getCurrentPosition().getY() + 1
                );
                case DOWN -> anticipatedPosition.setPosition(
                        robot.getCurrentPosition().getX(),
                        robot.getCurrentPosition().getY() - 1
                );
                case LEFT -> anticipatedPosition.setPosition(
                        robot.getCurrentPosition().getX() - 1,
                        robot.getCurrentPosition().getY()
                );
                case RIGHT -> anticipatedPosition.setPosition(
                        robot.getCurrentPosition().getX() + 1,
                        robot.getCurrentPosition().getY()
                );
            }
        }else{
            anticipatedPosition.setPosition(
                    robot.getCurrentPosition().getX(),
                    robot.getCurrentPosition().getY()
            );
            int i,j;
            RobotDirection [] directions = {UP,RIGHT,DOWN,LEFT};
            for(i = 0; direction.equals(directions[i]); i++);
            if(msg.equals(SERVER_TURN_RIGHT) && i==0 )
            else if(msg.equals(SERVER_TURN_RIGHT) && (i == 0 || i == 4) )
                direction = (i == 0)? directions[0] : directions[4];
            else if(msg.equals(SERVER_TURN_LEFT) && (i == 1 || i == 2)) direction = directions[i + 1]

        }
        return msg;
   }

   public String move() {
       int x = robot.getCurrentPosition().getX();
       int y = robot.getCurrentPosition().getY();
       RobotDirection direction = robot.getCurrentPosition().getCurrentDirection();

       if(x == 0 && y == 0) return SERVER_PICK_UP;

       // ROBOT LOCATED IN 1 QUADRANT
       if (x > 0 && y > 0) {
           //we want to decrement x and y

           //drive until x == 0
           switch (direction) {
               case UP -> {
                   anticipatedPosition.setPosition(x,y);
                   return SERVER_TURN_LEFT;
               }
               case DOWN -> {
                   anticipatedPosition.setPosition(x,y);
                   return SERVER_TURN_RIGHT;
               }
               case LEFT -> {
                   if (!isObstacle[x - 1][y]) {
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX() - 1,
                               robot.getCurrentPosition().getY()));
                       return SERVER_MOVE;
                   } else {
                       anticipatedPosition.setPosition(x,y);
                       return SERVER_TURN_LEFT;
                   }
               }
               case RIGHT -> {
                   if (!isObstacle[x - 1][y]) return SERVER_TURN_LEFT;
               }
           }

           //drive until y == 0
           switch (direction) {
               case UP -> {
                   if (!isObstacle[x][y - 1]) return SERVER_TURN_LEFT;
               }
               case DOWN -> {
                   if (!isObstacle[x][y - 1]) {
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX(),
                               robot.getCurrentPosition().getY() - 1));
                       return SERVER_MOVE;
                   } else return SERVER_TURN_LEFT;
               }
               case LEFT -> {
                   if (!isObstacle[x][y - 1]) return SERVER_TURN_LEFT;
               }
               case RIGHT -> {
                   if (!isObstacle[x][y - 1]) return SERVER_TURN_RIGHT;
               }
               default -> {
                   return SERVER_MOVE;
               }
           }
            }

       // ROBOT LOCATED IN 2 QUADRANT
       if (x < 0 && y > 0) {
           //we want to increment x and decrement y

           //drive until x == 0
           switch (direction) {
               case UP -> {
                   if (!isObstacle[x + 1][y]) return SERVER_TURN_RIGHT;
               }
               case DOWN -> {
                   if (!isObstacle[x + 1][y]) return SERVER_TURN_LEFT;
               }
               case LEFT -> {
                   if (!isObstacle[x + 1][y]) return SERVER_TURN_LEFT;

               }
               case RIGHT -> {
                   if (!isObstacle[x + 1][y]) {
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX() + 1,
                               robot.getCurrentPosition().getY()));
                       return SERVER_MOVE;
                   }
                   else return SERVER_TURN_LEFT;
               }
               default -> {
                   return SERVER_MOVE;
               }
           }

           //drive until y == 0
           switch (direction) {
               case UP -> {
                   if (!isObstacle[x][y - 1]) return SERVER_TURN_RIGHT;
               }
               case DOWN -> {
                   if (!isObstacle[x][y - 1]) {
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX(),
                               robot.getCurrentPosition().getY() - 1));
                       return SERVER_MOVE;
                   }
                   else return SERVER_TURN_RIGHT;
               }
               case LEFT -> {
                   if (!isObstacle[x][y - 1]) return SERVER_TURN_LEFT;
               }
               case RIGHT -> {
                   if (!isObstacle[x][y - 1]) return SERVER_TURN_RIGHT;
               }
               default -> {
                   return SERVER_MOVE;
               }
           }
       }

       // ROBOT LOCATED IN 3 QUADRANT
       if (x < 0 && y < 0) {
           //we want to increment x and y

           //drive until x == 0
           switch (direction) {
               case UP -> {
                   if (!isObstacle[x + 1][y]) return SERVER_TURN_RIGHT;
               }
               case DOWN -> {
                   if (!isObstacle[x + 1][y]) return SERVER_TURN_LEFT;
               }
               case LEFT -> {
                   if (!isObstacle[x + 1][y]) return SERVER_TURN_RIGHT;
               }
               case RIGHT -> {
                   if (!isObstacle[x + 1][y]) {
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX() + 1,
                               robot.getCurrentPosition().getY()));
                       return SERVER_MOVE;
                   }
                   else return SERVER_TURN_LEFT;
               }
               default -> {
                   return SERVER_MOVE;
               }
           }

           //drive until y == 0
           switch (direction) {
               case UP -> {
                   if (!isObstacle[x][y + 1]){
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX(),
                               robot.getCurrentPosition().getY() + 1));
                       return SERVER_MOVE;
                   }
                   else return SERVER_TURN_RIGHT;
               }
               case DOWN -> {
                   if (!isObstacle[x][y + 1]) return SERVER_TURN_LEFT;
               }
               case LEFT -> {
                   if (!isObstacle[x][y + 1]) return SERVER_TURN_RIGHT;
               }
               case RIGHT -> {
                   if (!isObstacle[x][y + 1]) return SERVER_TURN_LEFT;
               }
               default -> {
                   return SERVER_MOVE;
               }
           }
       }

       // ROBOT LOCATED IN 4 QUADRANT
       if (x > 0 && y < 0) {
           //we want to decrement x and increment y

           //drive until x == 0
           switch (direction) {
               case UP -> {
                   return SERVER_TURN_LEFT;
               }
               case DOWN -> {
                   return SERVER_TURN_RIGHT;
               }
               case LEFT -> {
                   if (!isObstacle[x - 1][y]){
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX() - 1,
                               robot.getCurrentPosition().getY()));
                       return SERVER_MOVE;
                   }
                   else return SERVER_TURN_RIGHT;
               }
               case RIGHT -> {
                   if (!isObstacle[x - 1][y]) return SERVER_TURN_LEFT;
               }
               default -> {
                   return SERVER_MOVE;
               }
           }

           //drive until y == 0
           switch (direction) {
               case UP -> {
                   if (!isObstacle[x][y + 1]) {
                       movesToCheck.add(new Position(robot.getCurrentPosition().getX(),
                               robot.getCurrentPosition().getY() + 1));
                       return SERVER_MOVE;
                   }
                   else return SERVER_TURN_LEFT;
               }
               case DOWN -> {
                   if (!isObstacle[x][y + 1]) return SERVER_TURN_RIGHT;
               }
               case LEFT -> {
                   if (!isObstacle[x][y + 1]) return SERVER_TURN_LEFT;
               }
               case RIGHT -> {
                   if (!isObstacle[x][y + 1]) return SERVER_TURN_RIGHT;
               }
               default -> {
                   return SERVER_MOVE;
               }
           }
       }

       if(x == 0 && y>0){
           switch (direction){
               case UP -> {
                   return SERVER_TURN_RIGHT;
               }
               case DOWN -> {
                   if(isObstacle[x][y]) return SERVER_TURN_LEFT;
                   return SERVER_MOVE;
               }
               case RIGHT -> {
                   return SERVER_TURN_LEFT;
               }
               case LEFT -> {
                   return SERVER_TURN_RIGHT;
               }
           }
       }
       if(x == 0 && y<0){
           switch (direction){
               case UP -> {
                   if(isObstacle[x][y + 1]) return SERVER_TURN_LEFT;
                   return SERVER_MOVE;
               }
               case DOWN -> {
                   return SERVER_TURN_LEFT;
               }
               case RIGHT -> {
                   return SERVER_TURN_LEFT;
               }
               case LEFT -> {
                   return SERVER_TURN_RIGHT;
               }
           }
       }
       if(x > 0 && y==0){
           switch (direction){
               case UP -> {
                   return SERVER_TURN_RIGHT;
               }
               case DOWN -> {
                   return SERVER_TURN_LEFT;
               }
               case RIGHT -> {
                   return SERVER_TURN_LEFT;
               }
               case LEFT -> {
                   if(isObstacle[x - 1][y]) return SERVER_TURN_LEFT;
                   return SERVER_MOVE;
               }
           }
       }
       if(x < 0 && y==0){
           switch (direction){
               case UP -> {
                   return SERVER_TURN_LEFT;
               }
               case DOWN -> {
                   return SERVER_TURN_LEFT;
               }
               case RIGHT -> {
                   if(isObstacle[x + 1][y]) return SERVER_TURN_RIGHT;
                   return SERVER_MOVE;
               }
               case LEFT -> {
                   return SERVER_TURN_RIGHT;
               }
           }
       }

       return "___NO___";
   }



















  /* public String makeBestMove(){

       int x = robot.getCurrentPosition().getX();
       int y = robot.getCurrentPosition().getY();

       //get all positions robot can go to
       List<Position> availablePositions = new ArrayList<>();
       switch (robot.getCurrentPosition().getCurrentDirection()){
           case UP -> {
               availablePositions.add(new Position(x, y + 1, UP, SERVER_MOVE));
               availablePositions.add(new Position(x + 1, y, RIGHT, SERVER_TURN_RIGHT));
               availablePositions.add(new Position(x - 1, y, LEFT, SERVER_TURN_LEFT));
           }
           case DOWN -> {
               availablePositions.add(new Position(x, y - 1, DOWN, SERVER_MOVE));
               availablePositions.add(new Position(x + 1, y, RIGHT,SERVER_TURN_LEFT));
               availablePositions.add(new Position(x - 1, y, LEFT, SERVER_TURN_RIGHT));
           }
           case LEFT -> {
               availablePositions.add(new Position(x - 1, y, LEFT,SERVER_MOVE));
               availablePositions.add(new Position(x, y + 1, UP,SERVER_TURN_RIGHT));
               availablePositions.add(new Position(x, y - 1, DOWN,SERVER_TURN_LEFT));
           }
           case RIGHT -> {
               availablePositions.add(new Position(x + 1, y, RIGHT, SERVER_MOVE));
               availablePositions.add(new Position(x, y + 1, UP, SERVER_TURN_LEFT));
               availablePositions.add(new Position(x, y - 1, DOWN,SERVER_TURN_RIGHT));
           }
       }

        //check if any of these positions are obstacles
       /*availablePositions.stream().filter(position ->
           obstacles.stream().anyMatch(position::equals)
       ).collect(Collectors.toList());
       availablePositions.removeAll(obstacles);
       availablePositions.sort(new Comparator<Position>() {
           @Override
           public int compare(Position p1, Position p2) {
               if(Math.abs(p1.getX()) > Math.abs(p2.getX())) return 1;
               else if(Math.abs(p1.getX()) < Math.abs(p2.getX())) return -1;

               else if(Math.abs(p1.getY()) > Math.abs(p2.getY())) return 1;
               else if(Math.abs(p1.getY()) < Math.abs(p2.getY())) return -1;
               else return 0;
           }
       });
       for (Position p:
            availablePositions) {
           System.out.printf("[%d, %d]\n",p.getX(),p.getY());
       }
        //move robot to the best position
       Position bestPosition = availablePositions.get(0);
       updateAnticipatedMove(bestPosition);
       //check if in [0,0]
       if(bestPosition.getX() == 0 && bestPosition.getY() == 0){
           return SERVER_PICK_UP;
       }
       System.out.println("BEST POSITION: " + bestPosition.getX() +"/"+ bestPosition.getY());
       robot.getCurrentPosition().setPosition(bestPosition.getX(), bestPosition.getY());
       robot.getCurrentPosition().setDirection(bestPosition.getCurrentDirection());
       return bestPosition.command;
   }

    private void updateAnticipatedMove(Position position){
        anticipatedPosition = position;
    }

   public String exploreNext(){
        int x = robot.getCurrentPosition().getX();
        int y = robot.getCurrentPosition().getY();
        RobotDirection direction = robot.getCurrentPosition().getCurrentDirection();
       //match with X axes
        if(x > 0){
            switch (direction){
                case UP -> {

                    return SERVER_TURN_LEFT;
                }
                case DOWN -> {
                    return SERVER_TURN_RIGHT;
                }
                case LEFT -> {
                    return SERVER_MOVE;
                }
                case RIGHT -> {
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
        else if(y > 0){
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
    }*/

}
